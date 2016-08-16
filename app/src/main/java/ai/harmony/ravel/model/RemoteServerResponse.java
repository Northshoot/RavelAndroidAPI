package ai.harmony.ravel.model;

import java.util.List;

/**
 * Created by lauril on 11/6/15.
 */
public class RemoteServerResponse {

        public  RemoteServerResponse(){
        }

        public List<RemoteResult> getResults() {
            return results;
        }

        public void setResults(List<RemoteResult> results) {
            this.results = results;
        }

        private List<RemoteResult> results;

    public class RemoteResult {

        private String status;


        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

}
