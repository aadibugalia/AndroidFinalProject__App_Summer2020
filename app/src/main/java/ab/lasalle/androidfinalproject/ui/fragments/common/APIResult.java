package ab.lasalle.androidfinalproject.ui.fragments.common;


import androidx.annotation.Nullable;

import org.json.JSONObject;


public class APIResult {
    @Nullable
    private JSONObject success;
    @Nullable
    private String error;
    @Nullable
    private Constants.API_REQUEST requestType;

    APIResult(@Nullable String error, Constants.API_REQUEST requestType) {
        this.error = error;
        this.requestType = requestType;
    }

    APIResult(@Nullable JSONObject success, Constants.API_REQUEST requestType) {
        this.success = success;
        this.requestType = requestType;
    }

    @Nullable
    public JSONObject getSuccess() {
        return success;
    }

    @Nullable
    public Constants.API_REQUEST getRequestType() {
        return requestType;
    }

    @Nullable
    public String getError() {
        return error;
    }
}