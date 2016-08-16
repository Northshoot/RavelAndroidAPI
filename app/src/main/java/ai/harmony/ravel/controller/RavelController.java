package ai.harmony.ravel.controller;

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
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ai.harmony.ravel.R;
import ai.harmony.ravel.base.controller.RavelAbstractModelController;
import ai.harmony.ravel.base.controller.RavelControllerInterface;
import ai.harmony.ravel.defines.BleDefines;
import ai.harmony.ravel.defines.RavelDefines;
import ai.harmony.ravel.defines.RavelErrorCodes;
import ai.harmony.ravel.defines.RavelGattAtrributes;
import ai.harmony.ravel.model.RavelAbstractModel;
import ai.harmony.ravel.ui.DeviceListActivity;
import ai.harmony.ravel.ui.RavelNotificationCenter;
import ai.harmony.ravel.utils.NoSuchModelException;

//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
//import ai.harmony.ravel.service.RegistrationService;

/**
 * Created by lauril on 1/21/16.
 */
public class RavelController extends Service implements RavelControllerInterface {

    private final static String TAG = RavelController.class.getSimpleName();
    private RavelModelControllerFactory modelFactory;


    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    List<BluetoothGattService> mBluetoohServiceList;
    private ScanSettings settings;
    List<ScanFilter> filters;
    private BluetoothGatt mBluetoothGatt;
    private Handler mHandler;
    List<BluetoothDevice> deviceList;
    private boolean mScanning;
    private String mBluetoothDeviceAddress;

    private int mBLEConnectionState = BLE_STATE_DISCONNECTED;
    private static final int BLE_STATE_DISCONNECTED = 0;
    private static final int BLE_STATE_CONNECTING = 1;
    private static final int BLE_STATE_CONNECTED = 2;
    private static final int BLE_STATE_INITIALIZED = 3;

    private RavelNotificationCenter rncenter;
    public boolean EMBEDDED_CONNECTED = false; // indicates if EMBEDDED device connected
    public boolean GATEWAY_CONNECTED = false; // indicates open gateway application
    public boolean CLOUD_CONNECTED = false; // indicates if cloud is connected

    private Handler.Callback embedded_callback=null;
    private Handler.Callback cloud_callback=null;
    private Handler.Callback gateway_callback=null;
    public static final String BROADCAST_ACTION = "ai.harmony.ravelbroadcast";

    //TODO: need adjustment with modelController factory
    //private LedStatusController lsController;


    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        /**
         * Callback indicating when GATT client has connected/disconnected to/from a remote
         * GATT server.
         *
         * @param gatt     GATT client
         * @param status   Status of the connect or disconnect operation.
         *                 {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
         * @param newState Returns the new connection state. Can be one of
         *                 {@link BluetoothProfile#STATE_DISCONNECTED} or
         *                 {@link BluetoothProfile#STATE_CONNECTED}
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    mBLEConnectionState = BLE_STATE_CONNECTED;
                    rncenter.setConnectionIcon(RavelDefines.CONNECTION_M);
                    try {
                        RavelAbstractModelController modelController = modelFactory.getModelControllerByUUID(RavelGattAtrributes.RANDOM_MODEL_MODEL_UUID);
                        Log.d(TAG, "HERE");
                        modelController.setDevice(gatt.getDevice().getAddress());
                    } catch (NoSuchModelException e){
                        Log.e(TAG, e.getMessage());
                    }
                    broadcastUpdate(BleDefines.ACTION_GATT_CONNECTED, gatt.getDevice().getAddress());
                    EMBEDDED_CONNECTED = true;
                    Log.i(TAG, "Connected to GATT server.");
                    //Scan for services
                    if ( gatt.discoverServices() ) {
                        Log.i(TAG, "Service Discovery started.");
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    mBLEConnectionState = BLE_STATE_DISCONNECTED;
                    broadcastUpdate(BleDefines.ACTION_GATT_DISCONNECTED);
                    //start scanning after predefined period
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scanLeDevice();
                        }
                    }, BleDefines.BLE_RECONNECT_TIME);
                    Log.i(TAG, "Device disconnected.");
                } else {
                    Log.e(TAG, "Should not be in this state: " + String.valueOf(status) +
                            " for the gatt " + gatt.toString());
                }
        }

        /**
         * Callback invoked when the list of remote services, characteristics and descriptors
         * for the remote device have been updated, ie new services have been discovered.
         *
         * @param gatt   GATT client invoked {@link BluetoothGatt#discoverServices}
         * @param status {@link BluetoothGatt#GATT_SUCCESS} if the remote device
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //broadcastUpdate(BleDefines.ACTION_GATT_SERVICES_DISCOVERED);
                Log.d(TAG, "onServicesDiscovered");
                if (mBluetoothGatt != null) {

                        //TODO: this assumes we have more than one service!
                        mBluetoohServiceList = mBluetoothGatt.getServices();
                    Log.d(TAG, "Service list: " + mBluetoohServiceList);
                    /** analyze services,
                     * each user models is represented by a service
                     * additionally device model is created locally
                     * Currently it is assumed connection of the same device to the gateway
                     * TODO: A new model should be created for each new device that is connected to the gateway
                     *
                     */

                    checkForServices(mBluetoohServiceList);
                } else {
                    Log.e(TAG, " GATT null when retrieving services");
                }

            } else {
                Log.w(TAG, "onServicesDiscovered error status: " + status);
            }
        }

        /**
         * Callback reporting the result of a characteristic read operation.
         *
         * @param gatt           GATT client invoked {@link BluetoothGatt#readCharacteristic}
         * @param characteristic Characteristic that was read from the associated
         *                       remote device.
         * @param status         {@link BluetoothGatt#GATT_SUCCESS} if the read operation
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //dispatch the characteristic to the proper model

            } else {
                Log.e(TAG, "onCharacteristicRead error status: " + status);
            }
        }

        /**
         * Callback indicating the result of a characteristic write operation.
         * <p/>
         * <p>If this callback is invoked while a reliable write transaction is
         * in progress, the value of the characteristic represents the value
         * reported by the remote device. An application should compare this
         * value to the desired value to be written. If the values don't match,
         * the application must abort the reliable write transaction.
         *
         * @param gatt           GATT client invoked {@link BluetoothGatt#writeCharacteristic}
         * @param characteristic Characteristic that was written to the associated
         *                       remote device.
         * @param status         The result of the write operation
         *                       {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Message message = new Message();
            message.setAsynchronous(true);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //report back to the model the characteristic
                message.arg1 = RavelDefines.WRITE_SUCCESS;
                sendCallback(embedded_callback, message);

            } else {
                message.arg1 = RavelDefines.WRITE_ERROR;
                sendCallback(embedded_callback, message);
                Log.w(TAG, "onCharacteristicWrite error status: " + status);
            }
            embedded_callback = null;
        }

        /**
         * Callback triggered as a result of a remote characteristic notification.
         *
         * @param gatt           GATT client the characteristic is associated with
         * @param characteristic Characteristic that has been updated as a result
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            //read data
            Log.d(TAG, "onCharacteristicChanged: " + characteristic.getUuid());
            try {
                RavelAbstractModelController model = modelFactory.getModelControllerByUUID(characteristic.getService().getUuid());
                //write to model

            model.dataReceivedEmbedded(characteristic.getValue(), gatt.getDevice().getAddress());
            } catch (NoSuchModelException e) {
                Log.e(TAG, "NoSuchModelException!");
            }

        }

        /**
         * Callback reporting the result of a descriptor read operation.
         *
         * @param gatt       GATT client invoked {@link BluetoothGatt#readDescriptor}
         * @param descriptor Descriptor that was read from the associated
         *                   remote device.
         * @param status     {@link BluetoothGatt#GATT_SUCCESS} if the read operation
         */
        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

            } else {
                Log.w(TAG, "onDescriptorRead error status: " + status);
            }
        }

        /**
         * Callback indicating the result of a descriptor write operation.
         *
         * @param gatt       GATT client invoked {@link BluetoothGatt#writeDescriptor}
         * @param descriptor Descriptor that was writte to the associated
         *                   remote device.
         * @param status     The result of the write operation
         *                   {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
         */
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.w(TAG, "onDescriptorWrite error status: " + status);
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                Log.d(TAG, )
//            } else {
//
//            }
        }

        /**
         * Callback invoked when a reliable write transaction has been completed.
         *
         * @param gatt   GATT client invoked {@link BluetoothGatt#executeReliableWrite}
         * @param status {@link BluetoothGatt#GATT_SUCCESS} if the reliable write
         */
        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

            } else {
                Log.w(TAG, "onReliableWriteCompleted error status: " + status);
            }
        }

        /**
         * Callback reporting the RSSI for a remote device connection.
         * <p/>
         * This callback is triggered in response to the
         * {@link BluetoothGatt#readRemoteRssi} function.
         *
         * @param gatt   GATT client invoked {@link BluetoothGatt#readRemoteRssi}
         * @param rssi   The RSSI value for the remote device
         * @param status {@link BluetoothGatt#GATT_SUCCESS} if the RSSI was read successfully
         */
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

            } else {
                Log.w(TAG, "onReadRemoteRssi error status: " + status);
            }
        }

        /**
         * Callback indicating the MTU for a given device connection has changed.
         * <p/>
         * This callback is triggered in response to the
         * {@link BluetoothGatt#requestMtu} function, or in response to a connection
         * event.
         *
         * @param gatt   GATT client invoked {@link BluetoothGatt#requestMtu}
         * @param mtu    The new MTU size
         * @param status {@link BluetoothGatt#GATT_SUCCESS} if the MTU has been changed successfully
         */
        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

            } else {
                Log.w(TAG, "onMtuChanged error status: " + status);
            }
        }
    };


    private void checkForServices(List<BluetoothGattService> mBluetoohServiceList) {
        Log.d(TAG, "Checking BLE services");
        String error = null;
        RavelAbstractModelController service_model;
        try {
            //get all models by service, enable all notify characteristics

            for (BluetoothGattService bleS : mBluetoohServiceList) {
                service_model = modelFactory.getModelControllerByUUID(bleS.getUuid());
                if (service_model != null){
                    Log.d(TAG, service_model.toString());
                }
                //We only check for ravel models
                if(service_model instanceof RavelAbstractModelController){
                    for (UUID notf : service_model.getNotifications()) {
                        Log.d(TAG, "Enabling service: " + notf.toString());
                        enableNotification(notf, bleS.getUuid());
                    }
                    Log.d(TAG, bleS.getUuid().toString());
                } else {
                    Log.e(TAG, "no compatible models were found");
                }
            }
        } catch (NoSuchModelException e) {
            error = "connected to the device but no model found";
        } catch (Exception e) {
            error = e.getMessage();
        } finally {
            if(error != null) {
                Log.e(TAG, error);
            }
        }
    }


    public void enableNotification(UUID notification, UUID service) {
        BluetoothGattCharacteristic charnot = mBluetoothGatt.getService(service).getCharacteristic(notification);
        if (charnot != null) enableNotification(charnot);
    }

    private void enableNotification(BluetoothGattCharacteristic characteristic){
        mBluetoothGatt.setCharacteristicNotification(characteristic, true);
        //TODO: not sure this is the right way
        //https://devzone.nordicsemi.com/question/55669/enabling-multiple-notifications-characteristic/
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(BleDefines.CLIENT_CHARACTERISTIC_CONFIG);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
    }


    @Override
    public void onCreate() {
        /**
         * Create and show notification in icon bar that Ravel is running
         */


        rncenter = RavelNotificationCenter.INSTANCE;
        rncenter.setContext(this);
        modelFactory = RavelModelControllerFactory.INSTANCE;
        modelFactory.setContext(this);
        mHandler = new Handler(Looper.getMainLooper());

        /**
         *  bluetooth
         */
        //create BLE manager
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            this.onDestroy();
        }
        deviceList = new ArrayList<>();
        if ( initialize() ) {
            mBLEConnectionState = BLE_STATE_INITIALIZED;
            scanLeDevice();
        }

        /** END BLE */

        /**
         * handle GCM
         */
        //start GCM service
        // we can not extend two classes

        // Start IntentService to register this application with GCM.
//        Intent intent = new Intent(this, RegistrationService.class);
//        startService(intent);
//
//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                SharedPreferences sharedPreferences =
//                        PreferenceManager.getDefaultSharedPreferences(context);
//                boolean sentToken = sharedPreferences
//                        .getBoolean(RavelDefines.SENT_TOKEN_TO_SERVER, false);
//                if (sentToken) {
//                    Log.d(TAG, "Device registered");
//
//                } else {
//                    Log.d(TAG, "Device registration error");
//                }
//            }
//        };
//        IntentFilter registerIntentFilter = new IntentFilter();
//        registerIntentFilter.addAction(RavelDefines.REGISTRATION_COMPLETE);
//        this.registerReceiver(mRegistrationBroadcastReceiver, registerIntentFilter);
//
//        this.registerReceiver(gcmBroadcastReceiver, makeGCMIntentFilter());

        /** END GCM */
        rncenter.showNotification();
    }


    private void deleteDB() {
       //this.deleteDatabase(RavelDatabaseHelper.DATABASE_FILE_NAME);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        //TODO: stop deleting
        deleteDB();
        return START_STICKY;
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
                mBLEConnectionState = BLE_STATE_CONNECTING;
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
        mBLEConnectionState = BLE_STATE_CONNECTING;
        return true;
    }

    private void scanLeDevice() {
        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().setDeviceName(RavelDefines.DEVICE_NAME).build());
        mScanning = true;
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(mLeScanCallback);
                    scanningDone();

                }
            }, BleDefines.SCAN_PERIOD);

            mScanning = true;
            bluetoothLeScanner.startScan(filters, settings, mLeScanCallback);
    }

    private ScanCallback mLeScanCallback =
            new ScanCallback() {

                public void onScanResult(int callbackType, final ScanResult result){
                    Log.d(TAG, "onScanResult " + result.getDevice() + "" + result.getRssi());
                            addDevice(result.getDevice(), result.getRssi());
                        }


                public void onScanFailed(int errorCode){
                    Log.e(TAG, "onScanFailed " + errorCode);

                }
                public void onBatchScanResults(List<ScanResult> results){
                    Log.e(TAG,"onBatchScanResults " +  "onBatchScanResults");

                }


            };

    private void scanningDone(){
        Log.i(TAG, "Scanning done, found: " + deviceList.size() + " device (s)");
        if(deviceList.size() == 1) {
            //only one device we can connect to it directly
            connect(deviceList.get(0).getAddress());
        } else {
            //we need to start an activity for intent and grab the address of the device
            rncenter.showNotification(R.string.more_than_one_device, DeviceListActivity.class);

        }

    }

    private void addDevice(BluetoothDevice device, int rssi) {
        boolean deviceFound = false;

        for (BluetoothDevice listDev : deviceList) {
            if (listDev.getAddress().equals(device.getAddress())) {
                deviceFound = true;
                break;
            }
        }
//
//        devRssiValues.put(device.getAddress(), rssi);
        if (!deviceFound) {
            deviceList.add(device);
        }
    }
    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        if (mBluetoothManager == null && mBLEConnectionState == BLE_STATE_DISCONNECTED) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Log.e(TAG, "Trying to initialize manager in state: " + String.valueOf(mBLEConnectionState)
            + " with manager " + mBluetoothManager.toString());
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }



    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * service should clean up any resources it holds (threads, registered
     * receivers, etc) at this point.  Upon return, there will be no more calls
     * in to this Service object and it is effectively dead.  Do not call this method directly.
     */
    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        rncenter.cancel();

        try {
            this.unregisterReceiver(mRegistrationBroadcastReceiver);
           // this.unregisterReceiver(gcmBroadcastReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");
    }

    @Override
    public void onLowMemory() {
        Log.d(TAG, "onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        Log.d(TAG, "onTrimMemory");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * This is called if the service is currently running and the user has
     * removed a task that comes from the service's application.  If you have
     * set {@link ServiceInfo#FLAG_STOP_WITH_TASK ServiceInfo.FLAG_STOP_WITH_TASK}
     * then you will not receive this callback; instead, the service will simply
     * be stopped.
     *
     * @param rootIntent The original root Intent that was used to launch
     *                   the task that is being removed.
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved");
    }

    /**
     * Print the Service's state into the given stream.  This gets invoked if
     * you run "adb shell dumpsys activity service &lt;yourservicename&gt;"
     * (note that for this command to work, the service must be running, and
     * you must specify a fully-qualified service name).
     * This is distinct from "dumpsys &lt;servicename&gt;", which only works for
     * named system services and which invokes the {@link IBinder#dump} method
     * on the {@link IBinder} interface registered with ServiceManager.
     *
     * @param fd     The raw file descriptor that the dump is being sent to.
     * @param writer The PrintWriter to which you should dump your state.  This will be
     *               closed for you after you return.
     * @param args   additional arguments to the dump request.
     */
    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        Log.d(TAG, "dump");
    }
    private void broadcastUpdate(final String action) {
        Log.d(TAG,"Sending broadcast: " + action);
        final Intent intent = new Intent();
        intent.setAction(RavelController.BROADCAST_ACTION);
        intent.putExtra("NEW_DATA", action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final String extra){
        Log.d(TAG,"Sending broadcast: " + action +  " extra " + extra);
        final Intent intent = new Intent();
        intent.setAction(BleDefines.ACTION_GATT_CONNECTED);
        intent.putExtra("DEVICE_ADDRESS", extra);
        sendBroadcast(intent);
    }



    /**
     * Generic methods to communicate from the cloud
     *
     * @param callback the callback when the write has been performed
     * @param model    serialized model
     * @return true if channel is available
     */
    @Override
    public boolean write_to_cloud(Handler.Callback callback, RavelAbstractModel model) {
//        Handler handler = new Handler(callback);
//        Message message = new Message();
//        message.setAsynchronous(true);
//        message.arg1 = RavelDefines.WRITE_SUCCESS;
////        handler.sendMessage(message);
//        try {
//            Call<RemoteServerResponse.RemoteResult> call = RemoteServerController.getServer().add_status(
//                    (LedStatusRepresentation) model);
//            call.enqueue(
//                    new Callback<RemoteServerResponse.RemoteResult>() {
//
//
//                        @Override
//                        public void onResponse(Response<RemoteServerResponse.RemoteResult> response, Retrofit retrofit) {
//                            Log.d(TAG, "Response " + response.body().getStatus());
//                            //TODO: mark ACK
//                        }
//
//                        @Override
//                        public void onFailure(Throwable t) {
//                            Log.d(TAG, "Failure");
//                            Log.e(TAG, t.getMessage());
//                        }
//                    }
//            );
//        } catch (Exception e) {
//            Log.e(TAG, "response error");
//            e.printStackTrace();
//
//        }
        return true;
    }

    /**
     * Generic method that writes to the model instance on the embedded device
     * BLE assumption here!
     * TODO: extract BLE to a generic method
     *
     * @param callback    the callback when the write has been performed
     * @param serviceUUID models service uuid
     * @param charUUID    characteristic to write to write to
     * @param data        model in byte[] format
     * @return true if channel is available
     */
    @Override
    public boolean write_to_embedded(Handler.Callback callback, UUID serviceUUID, UUID charUUID, byte[] data) {
        //TODO: write to the characteristic
        this.embedded_callback = callback;
        if( mBluetoothGatt != null) {
            BluetoothGattService service = mBluetoothGatt.getService(serviceUUID);
            if (service == null){
                Log.e(TAG, RavelErrorCodes.NO_SUCH_SERVICE);
                broadcastUpdate(RavelErrorCodes.NO_SUCH_SERVICE);
                return false;
            }
            BluetoothGattCharacteristic writeChar = service.getCharacteristic(charUUID);
            if (service == null) {
                Log.e(TAG, RavelErrorCodes.NO_SUCH_WRITE_CHARACTERISTIC);
                broadcastUpdate(RavelErrorCodes.NO_SUCH_WRITE_CHARACTERISTIC);
                return false;
            }
            writeChar.setValue(data);
            boolean status = mBluetoothGatt.writeCharacteristic(writeChar);
            if (!status) {
                Log.e(TAG, RavelErrorCodes.WRITE_CHARACTERISTIC_ERROR);
                return false;
            }
            Log.d(TAG,"writeCharacteristic Success");
        } else {
            Log.d(TAG,"BLE Disconnected");
        }
        return true;
    }

    /**
     * Writes to the gateway instance, the gateway instance is implemented
     * via presenter pattern
     *
     * @param callback the callback when the write has been performed
     * @param model    serialized model
     * @return true if channel is available
     */
    @Override
    public boolean write_to_gateway(Handler.Callback callback, RavelAbstractModel model) {
        //TODO: write to the ui
        Log.d(TAG, "Writing to the gateway: " + model.toString());
        broadcastUpdate("UPDATE_MODEL");
        return true;
    }

    private void sendCallback(Handler.Callback callback, Message message){
//        Handler handler = new Handler(callback);
//        handler.sendMessage(message);
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            //TODO: deal with the user ui,
//            //TODO: need to create a fragment for this
////            if (apiAvailability.isUserResolvableError(resultCode)) {
////                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
////                        .show();
////            } else {
////                Log.i(TAG, "This device is not supported.");
////
////            }
//            return false;
//        }
        return true;
    }

    private static IntentFilter makeGCMIntentFilter(){
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RavelDefines.CLOUD_DATA);
        return intentFilter;

    }


    private final BroadcastReceiver gcmBroadcastReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, " gcmBroadcastReceiver " + intent.getAction());
            String action = intent.getAction();
            final Intent mIntent = intent;
            if (action.equals(RavelDefines.CLOUD_DATA)) {
                String msg = intent.getStringExtra(RavelDefines.CLOUD_DATA);
                Log.d(TAG, "Got msg: " + msg);
                //TODO: update model and call sync on model
                try {
                    RavelAbstractModelController model = modelFactory.getModelControllerByUUID(RavelGattAtrributes.RANDOM_MODEL_MODEL_UUID);
                    //write to model

                    //((RandomController)model).dataReceivedCloud(msg);
                } catch (NoSuchModelException e) {
                    Log.e(TAG, "NoSuchModelException!");
                }

            }
        }
    };

}
