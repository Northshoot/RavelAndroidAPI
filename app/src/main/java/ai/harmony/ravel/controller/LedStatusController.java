//package ai.harmony.ravel.controller;
//
//import android.content.Context;
//import android.database.ContentObserver;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import ai.harmony.ledcontrol.model.LedStatusRepresentation;
//import ai.harmony.ledcontrol.presenter.LedStatusPresenter;
//import ai.harmony.ravel.base.controller.RavelAbstractModelController;
//import ai.harmony.ravel.defines.RavelDefines;
//import ai.harmony.ravel.defines.RavelGattAtrributes;
//import ai.harmony.ravel.model.LedModelContent;
//import ai.harmony.ravel.model.ledstatus.LedstatusColumns;
//
///**
// * Created by lauril on 1/21/16.
// */
//public class LedStatusController extends RavelAbstractModelController {
//
//    public final static String model_id = "111111111111111111";
//    private static final String TAG = "LedStatusController";
//    //each field has an atomic reference to avoid race conditions
//
//    private Context context;
//    private final UUID mModelServiceUUID = RavelGattAtrributes.LED_MODEL_MODEL_UUID;
//    private final UUID mModelStatusCharUUID = RavelGattAtrributes.LED_STATUS__CHAR_UUID;
//
//    private List<UUID> notificationList;
//    private LedModelContentObserver ledModelContentObserver = new LedModelContentObserver();
//    private LedStatusPresenter ledStatusPresenter;
//    private LedStatusRepresentation ledStatus;
//    private String device;
//
//    private boolean local_db_change = false;
//
//    private Uri mControllerUri = LedstatusColumns.CONTENT_URI;
//    /**
//     * Callbacks to handle the reliable communication from the tiers
//     *
//     */
//    private Handler.Callback mCallbackEmbeddedSendDone;
//    private Handler.Callback mCallbackGatewaySendDone;
//    private Handler.Callback mCallbackCloudSendDone;
//    private Handler.Callback mCallbackDataBaseCreated;
//
//    public LedStatusController(Context context){
//        this.context = context;
//        // because we running in a separate process we cant interact with the gui
//        //hence we register an observer that listens to change in the content.
//        //the code is generated and the wrapper only allows defined view interaction.
//        //the actual deletion happened here
//        ledStatusPresenter = new LedStatusPresenter(context);
//        context.getApplicationContext()
//                .getContentResolver()
//                .registerContentObserver(Uri.parse(LedModelContent.CONTENT_URI_BASE), true, ledModelContentObserver);
//
//
//        mCallbackEmbeddedSendDone = new Handler.Callback() {
//            public boolean handleMessage(Message msg) {
//                Log.d(TAG, "mCallbackEmbeddedSendDone");
//                return true;
//            }
//        };
//        mCallbackGatewaySendDone = new Handler.Callback() {
//            public boolean handleMessage(Message msg) {
//                Log.d(TAG, "mCallbackGatewaySendDone");
//                return true;
//            }
//        };
//        mCallbackCloudSendDone = new Handler.Callback() {
//            public boolean handleMessage(Message msg) {
//                Log.d(TAG, "mCallbackCloudSendDone");
//                return true;
//            }
//        };
//
////        mCallbackDataBaseCreated = new Handler.Callback() {
////            public boolean handleMessage(Message msg) {
////                Log.d(TAG, "mCallbackDBDone");
////                db_created = true;
////                return true;
////            }
////        };
//
//        notificationList = new ArrayList<>();
//        notificationList.add(mModelStatusCharUUID);
//    }
//
//    /**
//     * This class observers the content provider,
//     * The event will fire on both local and ui changes
//     * Therefore, a booleans local_db_change is used to not trigger a loop
//     *
//     */
//    private class LedModelContentObserver extends ContentObserver {
//
//        public LedModelContentObserver() {
//            super(null);
//        }
//
//        @Override
//        public void onChange(boolean selfChange) {
//            onChange(selfChange, null);
//
//        }
//
//
//        @Override
//        public void onChange(boolean selfChange, Uri uri) {
//            Log.d(TAG, "selfChange: " + String.valueOf(selfChange) + " local_db_change: " + String.valueOf(local_db_change) + " URI: " + uri.toString());
//            //Check if it is db creation and update
//            if (selfChange) {
//                return;
//            }
//            //TODO: deal with actual change
//            if (uri.equals(mControllerUri)) {
//                Log.d(TAG, " " + (ledStatusPresenter.getLastLedStatus()).origin);
//                if ((ledStatusPresenter.getLastLedStatus()).origin.equals(RavelDefines.GATEWAY)) {
//                    Log.d(TAG, "LedModelContentObserver: change occurred from gateway");
//                    dataReceivedGateway();
//                }
//            }
//        }
//    }
//
//
//    protected void write_to_G(){
//        //TODO: write to model presenter
//        Log.e(TAG, "write to the gui");
//        ((RavelController)context).write_to_gateway(mCallbackGatewaySendDone, ledStatus);
//    }
//
//    protected void write_to_M(){
//        //TODO: Write to the embedded device
//        Log.e(TAG, "write to the embedded");
//        byte[] data = new byte[1];
//        switch (ledStatus.led_status){
//            case 0:
//                data[0] = 0;
//                break;
//            case 1:
//                data[0] = 1;
//                break;
//
//            default:
//                Log.e(TAG, "Model state ERROR");
//        }
//        ((RavelController)context).write_to_embedded(
//                mCallbackEmbeddedSendDone,
//                mModelServiceUUID,
//                mModelStatusCharUUID,
//                data);
//    }
//
//    protected void write_to_C(){
//        //TODO: write to the cloud
//        ((RavelController)context).write_to_cloud(mCallbackCloudSendDone, ledStatus);
//    }
//
//    @Override
//    public String toString(){
//        return TAG;
//    }
//
//
//
//    /**
//     * Data is added to the model
//     * Model instance implements particular data parsing method
//     *
//     * @param data byte array, data to add
//     */
//
//    public void dataReceivedEmbedded(byte[] data) {
//        //TODO: this is a dangerous conversion generations
//        //TODO: could be achieved in a smarter so that the errors are prevented
//        local_db_change = true;
//        Integer i = Integer.highestOneBit((int) data[0] & 0xFF);
//        Log.d(TAG, "dataReceivedEmbedded: " + i.toString());
//        ledStatus =  new LedStatusRepresentation(i, null, RavelDefines.EMBEDDED);
//        synchronize_model();
//    }
//
//    @Override
//    public void dataReceivedEmbedded(byte[] data, String device) {
//        //TODO: this is a dangerous conversion generations
//        //TODO: could be achieved in a smarter so that the errors are prevented
//        local_db_change = true;
//        Integer i = (int) data[0] & 0xFF;
//        Log.d(TAG, "dataReceivedEmbedded from:" + device + "; data: " + i.toString());
//        ledStatus = new LedStatusRepresentation(i, device, RavelDefines.EMBEDDED);
//        synchronize_model();
//
//    }
//
//    /**
//     * @deprecated
//     * so this is not happening, rather it has been moded to the
//     */
//    @Deprecated
//    public void dataReceivedGateway(String data) {
//
//    }
//
//    /**
//     * We only know when change happened in the database
//     * in the function we need to query
//     */
//    @Override
//    public void dataReceivedGateway() {
//        // the observer will trigger due to updates
//        local_db_change = false;
//        //we want to get the last entry and put create a objects
//        ledStatus = ledStatusPresenter.getLastLedStatus();
//        synchronize_model();
//    }
//
//    /**
//     * @deprecated
//     * so this is not happening, rather it has been model to the
//     */
//    @Deprecated
//    public void dataReceivedGateway(int data) {
//
//    }
//    /**
//     * Data received from GCM
//     *
//     * @param data Bundle data
//     */
//    @Override
//    public void dataReceivedCloud(Bundle data) {
//        local_db_change = true;
//        ledStatus = new LedStatusRepresentation(
//                data.getInt("status"),
//                data.getString("device"),
//                RavelDefines.CLOUD);
//        synchronize_model();
//    }
//
//    public void dataReceivedCloud(String data) {
//        local_db_change = true;
//        Log.d(TAG, "Got data from cloud: " + data);
//        ledStatus = new LedStatusRepresentation(
//                Integer.valueOf(data),
//                null,
//                RavelDefines.CLOUD);
//        synchronize_model();
//    }
//
//    @Override
//    public void setDevice(String device) {
//        this.device = device;
//    }
//
//
//    /**
//     * Returns list of notifications that models listens to
//     *
//     * @return List<UUID> of notifications
//     */
//    public List<UUID> getNotifications() {
//        return notificationList;
//    }
//
//    @Override
//    protected void synchronize_model() {
//        Log.d(TAG, "synchronizing model origin: " + ledStatus.origin);
//
//            switch (ledStatus.origin){
//                case "M": //change form EMBEDDED device
//                    //we write to the database
//                    ledStatusPresenter.insertLedStatus(ledStatus);
//                    //expect local trigger from database
//                    local_db_change = true;
//                    write_to_G();
//                    //call remote server
//                    write_to_C();
//                    break;
//                case "G"://change from gateway
//                    //the data is in the database already
//                    local_db_change = false;
//                    write_to_C();
//                    //write to the device
//                    write_to_M();
//                    break;
//                case "C"://change from cloud
//                    //we write to the database
//                    ledStatusPresenter.insertLedStatus(ledStatus);
//                    //expect local trigger from database
//                    local_db_change = true;
//                    write_to_G();
//                    //write to the device
//                    write_to_M();
//                    break;
//                default:
//                    Log.e(TAG, "Should not have ended up in default state!!!");
//
//            }
//
//    }
//
//
//}
