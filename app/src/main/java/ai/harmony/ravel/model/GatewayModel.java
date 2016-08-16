package ai.harmony.ravel.model;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by lauril on 1/29/16.
 */
public class GatewayModel {
    public final static GatewayModel INSTANCE = new GatewayModel();
    //each field has an atomic reference to avoid race conditions

    private final AtomicReference<String> ID = new AtomicReference<>();

}
