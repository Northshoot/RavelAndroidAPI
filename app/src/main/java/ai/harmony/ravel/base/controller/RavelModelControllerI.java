package ai.harmony.ravel.base.controller;

import android.os.Bundle;

/**
 * Created by lauril on 2/10/16.
 */
public interface RavelModelControllerI {

    String toString();

    void dataReceivedEmbedded(byte[] data);

    void dataReceivedEmbedded(byte[] data, String device);

    /**
     * @deprecated
     * so this is not happening, rather it has been model to the
     */
    @Deprecated
    void dataReceivedGateway(String data);

    /**
     * We only know when change happened in the database
     * in the function we need to query
     */
    void dataReceivedGateway();

    /**
     * @deprecated
     * so this is not happening, rather it has been moded to the
     */
    @Deprecated
    public void dataReceivedGateway(int data) ;

    void dataReceivedCloud(Bundle data);


}
