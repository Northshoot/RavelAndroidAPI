package ai.harmony.ravel.controller;

import android.content.Context;
import android.util.Log;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ai.harmony.app.RandomController;
import ai.harmony.ravel.base.controller.RavelAbstractModelController;
import ai.harmony.ravel.defines.RavelGattAtrributes;
import ai.harmony.ravel.utils.NoSuchModelException;

/**
 * This class only create all the models in the ravel framework
 * Should not be edited manually as it is generated and is
 * Created by lauril on 2/1/16.
 */
public class RavelModelControllerFactory {
    public static final String TAG = "RavelMCFractory";
    public final static RavelModelControllerFactory INSTANCE = new RavelModelControllerFactory();
    private Context context;
    /**
     * to avoid possible concurrent read, writes we use
     * ConcurrentHashMap
     *
     * http://docs.oracle.com/javase/8/docs/api/index.html?java/util/concurrent/ConcurrentHashMap.html
     */
    private ConcurrentMap mModelMap;

    public void setContext(Context context ){
        this.context = context;
        //init the hash
        mModelMap = new ConcurrentHashMap(RavelGattAtrributes.NUMBER_OF_MODEL);
        createModels();
    }
    /**
     * private constructor for the RavelModelControllerFactory
     */
    private RavelModelControllerFactory(){

    }

    public RavelAbstractModelController getModel(String name) throws NoSuchModelException {
        if ( mModelMap.containsKey(name) ) {
            return (RavelAbstractModelController) mModelMap.get(name);
        } else {
            throw new NoSuchModelException(name);
        }

    }
    /**
     * Creates models and pushes then in the maps
     */
    private void createModels(){
        //autogenerated
        /// This is Ravel's framework autogenerated model
        Log.d(TAG, "Factory, making models");
//        mModelMap.put(RavelGattAtrributes.RAVEL_EMBEDDED_MODEL, EmbeddedModel.INSTANCE);
//        mModelMap.put(RavelGattAtrributes.RAVEL_GATEWAY_MODEL, GatewayModel.INSTANCE);
//        mModelMap.put(RavelGattAtrributes.RAVEL_CLOUD_MODEL, CloudModel.INSTANCE);
//        mModelMap.put(RavelGattAtrributes.RAVEL_USER_MODEL, UserModel.INSTANCE);
//        mModelMap.put(RavelGattAtrributes.RAVEL_SECURITY_MODEL, SecurityModel.INSTANCE);

        /**
         * bellow is generated used created models
         */
        mModelMap.put(RavelGattAtrributes.RANDOM_MODEL, new RandomController(context));

    }

    public int numberOfModels(){ return mModelMap.size(); }

    public RavelAbstractModelController getModelControllerByUUID(UUID modelUUID) throws NoSuchModelException {
        String uuidToModeName = RavelGattAtrributes.lookup(modelUUID);
        Log.d(TAG, "Looking up service: " +  uuidToModeName + " " +modelUUID);
        if (uuidToModeName != null){
            return (RavelAbstractModelController) mModelMap.get(uuidToModeName);
        } else {
            throw new NoSuchModelException();
        }


    }

}
