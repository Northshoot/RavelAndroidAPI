package ai.harmony.ravel.model;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by lauril on 1/29/16.
 */
public class EmbeddedModel {
    public final static EmbeddedModel INSTANCE = new EmbeddedModel();
    //each field has an atomic reference to avoid race conditions

    private final AtomicReference<String> address = new AtomicReference<>();

}
