
package ai.harmony.ravel.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.List;
import java.util.UUID;

import ai.harmony.ravel.defines.BleDefines;
import ai.harmony.ravel.defines.RavelErrorCodes;


/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BleService extends Service {
    private final static String TAG = BleService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;


    
   
    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = BleDefines.ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = BleDefines.ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
            	Log.w(TAG, "mBluetoothGatt = " + mBluetoothGatt );
            	
                broadcastUpdate(BleDefines.ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(BleDefines.ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(BleDefines.ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        Log.d(TAG, "Broadcastupdate action: " + action);

        // This is handling for the notification on TX Character of NUS service
//        if (TemporarilyGatt.LED_STATUS__CHAR_UUID.equals(characteristic.getUuid())) {
//
//           // Log.d(TAG, String.format("Received TX: %d",characteristic.getValue() ));
//            intent.putExtra(BleDefines.EXTRA_DATA, characteristic.getValue());
//        } else {
//
//        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }




    public class LocalBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    public final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }





    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enable all notifications
     * TODO: veryfy implementation
     */
    public void enableAllNotifications(){
        Log.d(TAG, "enableAllNotifications");
        for (BluetoothGattService service : mBluetoothGatt.getServices()){
            Log.d(TAG, "enableAllNotifications service " + service.toString());
            for(BluetoothGattCharacteristic characteristic : service.getCharacteristics()){
                if(characteristic.getProperties() == BluetoothGattCharacteristic.PROPERTY_NOTIFY){
                    Log.d(TAG, "notify characteristic: " + characteristic.toString());
                    mBluetoothGatt.setCharacteristicNotification(characteristic, true);
                    //TODO: not sure this is the right way
                    //https://devzone.nordicsemi.com/question/55669/enabling-multiple-notifications-characteristic/
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(BleDefines.CLIENT_CHARACTERISTIC_CONFIG);
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descriptor);
                }

            }
        }
    }
    public void enableNotification(UUID notification, UUID service) {
        BluetoothGattCharacteristic charnot = mBluetoothGatt.getService(service).getCharacteristic(notification);
        if (charnot != null) enableNotification(charnot);
    }
    public void enableNotification(UUID notification) {
        for (BluetoothGattService service : mBluetoothGatt.getServices()) {
            Log.d(TAG, "enableNotification service " + service.toString());
            BluetoothGattCharacteristic charnot = service.getCharacteristic(notification);
            if (charnot != null) enableNotification(charnot);
        }
    }

    private void enableNotification(BluetoothGattCharacteristic characteristic){
        mBluetoothGatt.setCharacteristicNotification(characteristic, true);
        //TODO: not sure this is the right way
        //https://devzone.nordicsemi.com/question/55669/enabling-multiple-notifications-characteristic/
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(BleDefines.CLIENT_CHARACTERISTIC_CONFIG);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
    }
    

    public void enableNotifications()
    {
//        Log.d(TAG, "Printing services");
//        for (BluetoothGattService service : mBluetoothGatt.getServices()){
//            Log.d(TAG, service.toString());
//        }
//    	BluetoothGattService RxService = mBluetoothGatt.getService(TemporarilyGatt.BLINK_SERVICE_UUID);
//    	if (RxService == null) {
//            showMessage("Rx service not found!");
//            broadcastUpdate(BleDefines.DEVICE_DOES_NOT_SUPPORT_UART);
//            return;
//        }
//    	BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(TemporarilyGatt.LED_STATUS__CHAR_UUID);
//        if (TxChar == null) {
//            showMessage("LED status charateristic not found!");
//            broadcastUpdate(BleDefines.DEVICE_DOES_NOT_SUPPORT_UART);
//            return;
//        }
//        mBluetoothGatt.setCharacteristicNotification(TxChar, true);
//
//        BluetoothGattDescriptor descriptor = TxChar.getDescriptor(BleDefines.CLIENT_CHARACTERISTIC_CONFIG);
//        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//        mBluetoothGatt.writeDescriptor(descriptor);
//
    }

    /**
     * Write to any characteristic and service
     * Allows multiple model mapping in BLE
     * @param value
     * @param service_UUID
     * @param characteristic_UUID
     */
    public void writeCharacteristic(byte[] value, UUID service_UUID, UUID characteristic_UUID){
        if( mBluetoothGatt != null) {
            BluetoothGattService service = mBluetoothGatt.getService(service_UUID);
            if (service == null){
                Log.e(TAG, RavelErrorCodes.NO_SUCH_SERVICE);
                broadcastUpdate(RavelErrorCodes.NO_SUCH_SERVICE);
                return;
            }
            BluetoothGattCharacteristic writeChar = service.getCharacteristic(characteristic_UUID);
            if (service == null) {
                Log.e(TAG, RavelErrorCodes.NO_SUCH_WRITE_CHARACTERISTIC);
                broadcastUpdate(RavelErrorCodes.NO_SUCH_WRITE_CHARACTERISTIC);
                return;
            }
                writeChar.setValue(value);
                boolean status = mBluetoothGatt.writeCharacteristic(writeChar);
                if (!status) {
                    Log.e(TAG, RavelErrorCodes.WRITE_CHARACTERISTIC_ERROR);
                    broadcastUpdate(RavelErrorCodes.WRITE_CHARACTERISTIC_ERROR);
                    return;
                }

            Log.d(TAG,"writeCharacteristic Success");
        } else {
            Log.d(TAG,"BLE Disconnected");
        }
    }

    
    private void showMessage(String msg) {
        Log.e(TAG, msg);
    }
    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        Log.w(TAG, "mBluetoothGatt closed");
        mBluetoothDeviceAddress = null;
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

}
