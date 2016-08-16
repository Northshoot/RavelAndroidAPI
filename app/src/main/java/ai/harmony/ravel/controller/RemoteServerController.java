package ai.harmony.ravel.controller;


import ai.harmony.ravel.defines.RavelDefines;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RemoteServerController {


    private static RemoteServer remoteServer;

    public static RemoteServer getServer(){
        if (remoteServer == null) {
            remoteServer = null;
            OkHttpClient client = new OkHttpClient();
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.interceptors().add(interceptor);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RavelDefines.API_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            remoteServer = retrofit.create(RemoteServer.class);
        }
        return remoteServer;
    }
    public class RemoteResult {

        private String status;


        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
        public void setResults(String results) {
            this.status = results;
        }
    }

    public interface RemoteServer {
//        @POST("/led/add_status/")
//        @Headers({"Content-Type: application/json"})
//       // Call<RemoteServerResponse.RemoteResult> add_status(@Body LedStatusRepresentation status);
//
//
//        @POST("/gcm/v1/device/register/")
//        @Headers({"Content-Type: application/json"})
//        Call<RemoteServerResponse.RemoteResult>  register(@Body Registration registration);
    }

    //"Content-Type: application/json" -d '{"dev_id": "test1", "reg_id":"fPPse59La3Y:APA91bHLe8tZ6lTSidR5lzZ7avMOcDSrXkj_y81jIqQ3d4t3s33gbqGwKrMmsDbyE2pcKGSYX-Mufdx8d14eQLJBwJAeOP7_s0lw7xdCdAM4CFZwlCySdTnGX5EqLnT1Q0D8HDUcBPqU", "name":"test device"}'


}
