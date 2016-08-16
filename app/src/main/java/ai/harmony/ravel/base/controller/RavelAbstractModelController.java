package ai.harmony.ravel.base.controller;

import java.util.List;
import java.util.UUID;

/**
 * Created by lauril on 2/10/16.
 */
public abstract class RavelAbstractModelController implements RavelModelControllerI {



    protected abstract void synchronize_model();
    public abstract void setDevice(String device);
    public abstract List<UUID> getNotifications();
    protected abstract void write_to_C();
    protected abstract void write_to_M();
    protected abstract void write_to_G();

}
