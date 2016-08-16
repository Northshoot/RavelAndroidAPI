//package ai.harmony.ravel.service;
//
//import android.app.IntentService;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.preference.PreferenceManager;
//import android.support.v4.content.LocalBroadcastManager;
//import android.util.Log;
//
//import com.google.android.gms.gcm.GcmPubSub;
//import com.google.android.gms.gcm.GoogleCloudMessaging;
//import com.google.android.gms.iid.InstanceID;
//
//import java.io.IOException;
//
//
//import ai.harmony.ravel.controller.RemoteServerController;
//import ai.harmony.ravel.defines.RavelDefines;
//import ai.harmony.ravel.model.Registration;
//import retrofit.Call;
//import retrofit.Callback;
//import retrofit.Response;
//import retrofit.Retrofit;
//
///**
// * Created by lauril on 1/6/16.
// */
//public class RegistrationService extends IntentService {
//
//    private static final String TAG = "RegService";
//    private static final String[] TOPICS = {"global"};
//    private static final String ID = RavelDefines.PHONE_ID;
//
//    public RegistrationService() {
//        super(TAG);
//    }
//
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//
//        try {
//            // [START register_for_gcm]
//            // Initially this call goes out to the network to retrieve the token, subsequent calls
//            // are local.
//            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
//            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
//            // [START get_token]
//            InstanceID instanceID = InstanceID.getInstance(this);
//            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
//                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
//            // [END get_token]
//            Log.i(TAG, "GCM Registration Token: " + token);
//
//            sendRegistrationToServer(token);
//
//            // Subscribe to topic channels
//            //subscribeTopics(token);
//
//            // You should store a boolean that indicates whether the generated token has been
//            // sent to your server. If the boolean is false, send the token to your server,
//            // otherwise your server should have already received the token.
//            sharedPreferences.edit().putBoolean(RavelDefines.SENT_TOKEN_TO_SERVER, true).apply();
//            // [END register_for_gcm]
//        } catch (Exception e) {
//            Log.d(TAG, "Failed to complete token refresh", e);
//            // If an exception happens while fetching the new token or updating our registration data
//            // on a third-party server, this ensures that we'll attempt the update at a later time.
//            sharedPreferences.edit().putBoolean(RavelDefines.SENT_TOKEN_TO_SERVER, false).apply();
//        }
//        // Notify UI that registration has completed, so the progress indicator can be hidden.
//        Intent registrationComplete = new Intent(RavelDefines.REGISTRATION_COMPLETE);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
//    }
//
//    /**
//     * Persist registration to third-party servers.
//     *
//     * Modify this method to associate the user's GCM registration token with any server-side account
//     * maintained by your application.
//     *
//     * @param token The new token.
//     */
//    private void sendRegistrationToServer(String token) {
//
//        Call call = RemoteServerController.getServer().register(new Registration(ID, token, "name"));
//        call.enqueue(
//                new Callback() {
//
//
//                    @Override
//                    public void onResponse(Response response, Retrofit retrofit) {
//                        Log.d(TAG, "Responce " + response.body());
//                    }
//
//                    @Override
//                    public void onFailure(Throwable t) {
//                        Log.d(TAG, "Failure");
//                        Log.e(TAG, t.getMessage());
//                    }
//                }
//        );
//
//        Log.d(TAG, token);
//        Log.d(TAG, ID);
//    }
//
//    /**
//     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
//     *
//     * @param token GCM token
//     * @throws IOException if unable to reach the GCM PubSub service
//     */
//    // [START subscribe_topics]
//    //TODO: here we should subscribe for the model updates!
//    //each topic representing a model in the system
//    private void subscribeTopics(String token) throws IOException {
//        GcmPubSub pubSub = GcmPubSub.getInstance(this);
//        for (String topic : TOPICS) {
//            pubSub.subscribe(token, "/topics/" + topic, null);
//        }
//    }
//    // [END subscribe_topics]
//
//}
