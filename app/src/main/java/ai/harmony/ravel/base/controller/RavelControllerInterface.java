package ai.harmony.ravel.base.controller;

import android.os.Handler;
import java.util.UUID;
import ai.harmony.ravel.model.RavelAbstractModel;

/**
 * Created by lauril on 2/4/16.
 */
public interface RavelControllerInterface {

    /**
     * Generic methods to communicate from the cloud
     *
     * @param callback the callback when the write has been performed
     * @param model serialized model
     * @return true if channel is available
     */
    boolean write_to_cloud(Handler.Callback callback, RavelAbstractModel model);

    /**
     * Generic method that writtes to the model instance on the embedded device
     * BLE assumption here!
     * TODO: extract BLE to a generic method
     *
     * @param callback the callback when the write has been performed
     * @param serviceUUID models service uuid
     * @param charUUID characteristic to write to write to
     * @param data model in byte[] format
     * @return true if channel is available
     */
    boolean write_to_embedded(Handler.Callback callback, UUID serviceUUID, UUID charUUID, byte[] data);

    /**
     * Writes to the gateway instance, the gateway instance is implemented
     * via presenter pattern
     *
     * @param callback the callback when the write has been performed
     * @param model serialized model
     * @return true if channel is available
     */
    boolean write_to_gateway(Handler.Callback callback, RavelAbstractModel model);
}
