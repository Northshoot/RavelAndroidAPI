package ai.harmony.ravel.presenter;

import ai.harmony.ravel.model.RavelAbstractModel;

/**
 * Created by lauril on 2/8/16.
 */
public interface ModelListener {
    void onModelUpdate(RavelAbstractModel serializedModel);
}
