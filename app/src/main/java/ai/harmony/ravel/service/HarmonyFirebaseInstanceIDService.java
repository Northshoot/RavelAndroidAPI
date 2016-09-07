package ai.harmony.ravel.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import ai.harmony.ravel.controller.RemoteServerController;
import ai.harmony.ravel.defines.RavelDefines;
import ai.harmony.ravel.model.Registration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ██╗  ██╗ █████╗ ██████╗ ███╗   ███╗ ██████╗ ███╗   ██╗██╗   ██╗ █████╗ ██╗
 * ██║  ██║██╔══██╗██╔══██╗████╗ ████║██╔═══██╗████╗  ██║╚██╗ ██╔╝██╔══██╗██║
 * ███████║███████║██████╔╝██╔████╔██║██║   ██║██╔██╗ ██║ ╚████╔╝ ███████║██║
 * ██╔══██║██╔══██║██╔══██╗██║╚██╔╝██║██║   ██║██║╚██╗██║  ╚██╔╝  ██╔══██║██║
 * ██║  ██║██║  ██║██║  ██║██║ ╚═╝ ██║╚██████╔╝██║ ╚████║   ██║██╗██║  ██║██║
 * ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝     ╚═╝ ╚═════╝ ╚═╝  ╚═══╝   ╚═╝╚═╝╚═╝  ╚═╝╚═╝
 * <p>
 * Copyright (C) 2016 Laurynas Riliskis
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created by lauril on 9/6/16.
 */

public class HarmonyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FB-Instance-Service";
    private static final String ID = RavelDefines.PHONE_ID;
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {

        Call call = RemoteServerController.getServer().register(new Registration(ID, token, "name", "gateway"));
        call.enqueue(
                new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.d(TAG, "Response " + response.body());
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Log.d(TAG, "Failure");
                        Log.e(TAG, t.getMessage());
                    }

                }
        );

        Log.d(TAG, token);
        Log.d(TAG, ID);
    }
}
