package ab.lasalle.androidfinalproject.server.callbacks;

import org.json.JSONObject;

public interface ApiResponseCallback {

    void onApiResponseRecieved(JSONObject responseObject);
}
