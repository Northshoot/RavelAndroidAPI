package ai.harmony.ravel.utils;

import android.util.Log;

/**
 * Created by lauril on 2/1/16.
 */
public class NoSuchModelException extends Exception {
    public NoSuchModelException() {}

    //Constructor that accepts a message
    public NoSuchModelException(String message)
    {
        super(message);
        Log.e("NoSuchModelException", "Model " + message + " does not exist");
    }
}
