package ai.harmony.ravel.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by lauril on 1/26/16.
 */
public class BootController extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BootController", "On boot receive");
        if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED))
        {
            Intent serviceIntent = new Intent(context, RavelController.class);
            context.startService(serviceIntent);
        }
    }

}
