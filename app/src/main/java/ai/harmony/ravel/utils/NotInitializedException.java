package ai.harmony.ravel.utils;

/**
 * Created by lauril on 1/21/16.
 */
public class NotInitializedException extends Exception{
    //Parameterless Constructor
    public NotInitializedException() {}

    //Constructor that accepts a message
    public NotInitializedException(String message)
    {
        super(message);
    }
}
