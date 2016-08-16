package ai.harmony.ravel.defines;

import ai.harmony.ravel.utils.PhoneID;

/**
 * Created by lauril on 10/30/15.
 */
public class RavelDefines {

    public static final String  DEVICE_NAME = "Ravel";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";

    public final static String PHONE_ID = PhoneID.getUniquePseudoID();
    public static final int CONNECTION_MGC = 21000;
    public static final int CONNECTION_MG = 21001;
    public static final int CONNECTION_MC = 21002;
    public static final int CONNECTION_M = 21003;
    public static final int CONNECTION_C = 21004;
    public static final int CONNECTION_G = 21005;
    public static final int CONNECTION_GC = 21006;


    public static final int REQUEST_SELECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;
    public static final int UART_PROFILE_READY = 10;

    public static final int DEVICE_CONNECTED = 20;
    public static final int DEVICE_DISCONNECTED = 21;
    public static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0;

    public static final int MODEL_UPDATE_FROM_CLOUD = 30;
    public final static String CLOUD_DATA =
            "ai.harmony.blink.remote.cloud";


    public static final String EMBEDDED = "M";
    public static final String GATEWAY = "G";
    public static final String CLOUD = "C";

    public static final String API_URL = "http://192.168.2.71:8080/";


    public static final int WRITE_SUCCESS = 0 ;
    public static final int WRITE_ERROR = 1 ;
}
