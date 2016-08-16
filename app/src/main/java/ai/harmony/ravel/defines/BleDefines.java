package ai.harmony.ravel.defines;

import java.util.UUID;

/**
 * Most of the defines here are static and used by Ravel application to enable data sync
 *
 * Created by lauril on 10/30/15.
 */
public class BleDefines {

    /**
     * BLE control defines
     */

    public final static long SCAN_PERIOD = 1000; //scanning for 10 seconds
    public final static long BLE_RECONNECT_TIME = 10000;

    /**
     * BLE intent action filters
     */

    public final static String ACTION_GATT_CONNECTED =
            "ai.harmony.randroid.ble.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "ai.harmony.randroid.ble.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "ai.harmony.randroid.ble.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "ai.harmony.randroid.ble.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.nordicsemi.nrfUART.EXTRA_DATA";
    public final static String DEVICE_DOES_NOT_SUPPORT_UART =
            "ai.harmony.randroid.ble.DEVICE_DOES_NOT_SUPPORT_UART";



    public static final  UUID CLIENT_CHARACTERISTIC_CONFIG =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
}
