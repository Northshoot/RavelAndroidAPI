package ai.harmony.app;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ai.harmony.ravel.base.controller.RavelAbstractModelController;
import ai.harmony.ravel.defines.RavelGattAtrributes;

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
 * Created by lauril on 8/15/16.
 */

public class RandomController extends RavelAbstractModelController {
    private static final String TAG = "RandomController";
    private Context context;
    private String m_device;
    private final UUID mModelServiceUUID = RavelGattAtrributes.RANDOM_MODEL_MODEL_UUID;
    private final UUID mModelFieldNotificationUUID = RavelGattAtrributes.RANDOM_FIELD__CHAR_UUID;

    private List<UUID> notificationList;

    private RandomModel randomModel;

    public RandomController(Context c){
        context = c;
        notificationList = new ArrayList<>();
        notificationList.add(mModelFieldNotificationUUID);
        randomModel = new RandomModel();
    }
    @Override
    protected void synchronize_model() {
        Log.e(TAG, "synchronize_model");
    }

    @Override
    public void setDevice(String device) {

        m_device = device;
        Log.e(TAG, "setDevice");
    }


    @Override
    protected void write_to_C() {
        Log.e(TAG, "write_to_C");
    }

    @Override
    protected void write_to_M() {
        Log.e(TAG, "write_to_M");
    }

    @Override
    protected void write_to_G() {
        Log.e(TAG, "write_to_G");
    }

    @Override
    public void dataReceivedEmbedded(byte[] data) {
        Log.e(TAG, "write_to_G");
    }

    @Override
    public void dataReceivedEmbedded(byte[] data, String device) {
        randomModel.add(data);
        Log.e(TAG, "dataReceivedEmbedded" + data.toString());
    }

    @Override
    public void dataReceivedGateway(String data) {
        Log.e(TAG, "dataReceivedGateway");
    }

    @Override
    public void dataReceivedGateway() {
        Log.e(TAG, "dataReceivedGateway");
    }

    @Override
    public void dataReceivedGateway(int data) {
        Log.e(TAG, "dataReceivedGateway");
    }

    @Override
    public void dataReceivedCloud(Bundle data) {
        Log.e(TAG, "dataReceivedCloud");

    }

    /**
     * Returns list of notifications that models listens to
     *
     * @return List<UUID> of notifications
     */
    public List<UUID> getNotifications() {
        return notificationList;
    }

    @Override
    public String toString(){
        return TAG;
    }
}
