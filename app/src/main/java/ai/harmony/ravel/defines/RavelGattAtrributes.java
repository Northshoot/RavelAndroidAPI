package ai.harmony.ravel.defines;

/**
 * Created by lauril on 9/25/15.
 */
import java.util.HashMap;
import java.util.UUID;

public class RavelGattAtrributes {

    private static HashMap<UUID, String> nameToUUID = new HashMap<>();
    public static final int NUMBER_OF_MODEL = 10;
    //default BLE uuids
    //org.bluetooth.characteristic.current_time

    /**
     * Device Information Service (DIS)
     * The Device Information Service (DIS) exposes manufacturer information about a device.
     *
     * Manufacturer Name String - shall represent the name of the manufacturer of the device.
     * Model Number String - shall represent the model number that is assigned by the device vendor.
     * Serial Number String - shall represent the serial number for a particular instance of the device.
     * Hardware Revision String - shall represent the hardware revision for the hardware within the device.
     * Firmware Revision String - shall represent the firmware revision for the firmware within the device.
     * Software Revision String - shall represent the software revision for the software within the device.
     * System ID - shall represent a structure containing an Organizationally Unique Identifier (OUI) followed by a manufacturer-defined identifier and is unique for each individual instance of the product.
     * IEEE 11073-20601 Regulatory Certification Data List - shall represent regulatory and certification information for the product in a list defined in IEEE 11073- 20601.

     * Overview: https://developer.bluetooth.org/TechnologyOverview/Pages/DIS.aspx
     * Details: https://www.bluetooth.org/docman/handlers/downloaddoc.ashx?doc_id=244369&_ga=1.56233835.1589973911.1454522943
     */
    public static String BLE_DIS = "DeviceInformationService"; // belongs to the EMBEDDED_MODEL
    public static final String BLE_DIS_FIRMWARE_REVISON = "00002a26-0000-1000-8000-00805f9b34fb";
    public static final UUID BLE_DIS_FIRMWARE_REVISON_UUID = UUID.fromString(BLE_DIS_FIRMWARE_REVISON);

    /**
     * Tx Power Service (TPS)
     * The Tx Power Service (TPS) exposes a device's current transmit power level when in a connection.
     *
     */
    public static final String BLE_TPS = "DevicePowerService";
    public static final String BLE_TX_POWER = "00001804-0000-1000-8000-00805f9b34fb";
    public static final UUID BLE_TX_POWER_UUID = UUID.fromString(BLE_TX_POWER);

    public static final String BLE_TX_POWER_LEVEL = "00002a07-0000-1000-8000-00805f9b34fb";
    public static final UUID BLE_TX_POWER_LEVEL_UUID = UUID.fromString(BLE_TX_POWER_LEVEL);
    //
    //ravel model service uuid's
    public static final String RAVEL_EMBEDDED_MODEL = "EmbeddedModel";
    public static final String RAVEL_EMBEDDED_MODEL_ID = "00006000-1000-1000-8000-00805f9b34fb";
    public static final UUID RAVEL_EMBEDDED_MODEL_UUID = UUID.fromString(RAVEL_EMBEDDED_MODEL_ID);

    public static final String RAVEL_GATEWAY_MODEL = "GatewayModel";
    public static final String RAVEL_GATEWAY_MODEL_ID = "00006000-2000-1000-8000-00805f9b34fb";
    public static final UUID RAVEL_GATEWAY_MODEL_UUID = UUID.fromString(RAVEL_GATEWAY_MODEL_ID);

    public static final String RAVEL_CLOUD_MODEL = "CloudModel";
    public static final String RAVEL_CLOUD_MODEL_ID = "00006000-3000-1000-8000-00805f9b34fb";
    public static final UUID RAVEL_CLOUD_MODEL_UUID = UUID.fromString(RAVEL_CLOUD_MODEL_ID);

    public static final String RAVEL_USER_MODEL = "UserModel";
    public static final String RAVEL_USER_MODEL_ID = "00006000-4000-1000-8000-00805f9b34fb";
    public static final UUID RAVEL_USER_MODEL_UUID = UUID.fromString(RAVEL_USER_MODEL_ID);

    public static final String RAVEL_SECURITY_MODEL = "SecurityModel";
    public static final String RAVEL_SECURITY_MODEL_ID = "00006000-5000-1000-8000-00805f9b34fb";
    public static final UUID RAVEL_SECURITY_MODEL_UUID = UUID.fromString(RAVEL_SECURITY_MODEL_ID);


    public static final String RAVEL_PREFIX_UUID = "7810";
    public static final String RAVEL_POSTFIX_UUID="-7283-5042-7666-902400000000";


    /**
     * User Defined Models
     */
    public static final String RANDOM_MODEL = "RandomModel";
    public static final String RANDOM_MODEL_ID = "1000";
    public static final UUID RANDOM_MODEL_MODEL_UUID = getUUID(RANDOM_MODEL_ID);
    public static final UUID RANDOM_FIELD__CHAR_UUID = getUUID("1100");

    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG_UUID = UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG);


    static final UUID  getUUID(String uuid){
        return UUID.fromString(RAVEL_PREFIX_UUID + uuid + RAVEL_POSTFIX_UUID);
    }
    /**
     * Put everything into hash map
     *
     */
    static {
        // Ravel Services.
        nameToUUID.put( BLE_DIS_FIRMWARE_REVISON_UUID, BLE_DIS);
        nameToUUID.put( BLE_TX_POWER_UUID, BLE_TPS);
        nameToUUID.put( RAVEL_EMBEDDED_MODEL_UUID, RAVEL_EMBEDDED_MODEL);
        nameToUUID.put( RAVEL_GATEWAY_MODEL_UUID, RAVEL_GATEWAY_MODEL);
        nameToUUID.put( RAVEL_CLOUD_MODEL_UUID, RAVEL_CLOUD_MODEL);
        nameToUUID.put( RAVEL_USER_MODEL_UUID, RAVEL_USER_MODEL);
        nameToUUID.put( RAVEL_SECURITY_MODEL_UUID, RAVEL_SECURITY_MODEL);

        /**
         * User Defined Models
         */
        nameToUUID.put(RANDOM_MODEL_MODEL_UUID,RANDOM_MODEL);
    }



    public static String lookup(UUID uuid) {
        String defaultName = "NoModel";
        String name = nameToUUID.get(uuid);
        return name == null ? defaultName : name;
    }
}
