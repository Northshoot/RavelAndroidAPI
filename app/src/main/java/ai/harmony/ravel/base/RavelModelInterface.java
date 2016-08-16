package ai.harmony.ravel.base;

import android.os.Bundle;

import java.util.List;
import java.util.UUID;

import ai.harmony.ravel.model.RavelAbstractModel;
import ai.harmony.ravel.presenter.ModelListener;

/**
 * Created by lauril on 2/1/16.
 */
public interface RavelModelInterface {

    /**
     * Data is added to the model
     * Model instance implements particular data parsing method
     * @param data byte array, data to add
     */
     void dataReceivedEmbedded(byte[] data);

    //adding the device to the model
    //TODO: this should be handled gracefully
    //does each device had its own model? or one model to rule them all
     void dataReceivedEmbedded(byte[] data, String device);
    /**
     * Data received from the gateway UI
     * @param data
     */
    void dataReceivedGateway(String data);

    void dataReceivedGateway(int data);

    void dataReceivedGateway();

    /**
     * Data received from GCM
     * @param data Bundle data
     */
    void dataReceivedCloud(Bundle data);


    /**
     * Returns lates models
     *
     * @return serialized latest model
     */
    RavelAbstractModel getModel();


    /**
     * Returns list of notifications that models listens to
     *
     * @return List<UUID> of notifications
     */
    List<UUID> getNotifications();

    void addModelListner(ModelListener modelListener);


}
