package ab.lasalle.androidfinalproject.ui.fragments.common;

import org.json.JSONObject;

import ab.lasalle.androidfinalproject.server.callbacks.ApiResponseCallback;
import ab.lasalle.androidfinalproject.server.threads.HttpServiceThread;
import ab.lasalle.androidfinalproject.ui.fragments.ideas.IdeasFragment;

public class APIDataSource implements ApiResponseCallback {

    private HttpServiceThread thread;
    private ApiResponseCallback mCallback;


    public void fetchAllIdeas(Constants.API_REQUEST requestType, ApiResponseCallback callback) {

        this.mCallback = callback;

        if (thread == null) {
            thread = new HttpServiceThread("getAllIdeas", getDummyRequestObject(), this);
        } else {
            thread.setApiName("getAllIdeas");
            thread.setRequestBody(getDummyRequestObject());
        }
        thread.start();
    }


    public void searchUser(Constants.API_REQUEST requestType, String userName, ApiResponseCallback callback) {

        this.mCallback = callback;

        JSONObject object = null;
        try {


            object = new JSONObject();
            object.put("userName", userName);


        } catch (Exception e) {


        }

        if (thread == null) {
            thread = new HttpServiceThread("searchUser", object.toString(), this);
        } else {
            thread.setApiName("searchUser");
            thread.setRequestBody(object.toString());
        }
        thread.start();
    }


    public void updateFollowingList(Constants.API_REQUEST requestType, String userName, String userNametoFollow, ApiResponseCallback callback) {
        this.mCallback = callback;

        JSONObject object = null;
        try {


            object = new JSONObject();
            object.put("userName", userName);


            object.put("userNameToFollow", userNametoFollow);

        } catch (Exception e) {


        }

        if (thread == null) {
            thread = new HttpServiceThread("updateFollowingList", object.toString(), this);
        } else {
            thread.setApiName("updateFollowingList");
            thread.setRequestBody(object.toString());
        }
        thread.start();
    }

    public void registerIdea(Constants.API_REQUEST requestType, String userName, String title, String content, String context, ApiResponseCallback callback) {

        this.mCallback = callback;


        JSONObject object = null;
        try {


            object = new JSONObject();
            object.put("userName", userName);
            object.put("title", title);
            object.put("context", content);
            object.put("content", context);


        } catch (Exception e) {


        }

        if (thread == null) {
            thread = new HttpServiceThread("operations/registerIdea", object.toString(), this);
        } else {
            thread.setApiName("operations/registerIdea");
            thread.setRequestBody(object.toString());
        }
        thread.start();
    }

    public void updateToDo(Constants.API_REQUEST requestType, String userName, String ideaId, ApiResponseCallback callback) {


        this.mCallback = callback;
        JSONObject object = null;
        try {


            object = new JSONObject();
            object.put("userName", userName);
            object.put("ideaID", ideaId);


        } catch (Exception e) {


        }

        if (thread == null) {
            thread = new HttpServiceThread("operations/addToPersonalToDo", object.toString(), this);
        } else {
            thread.setApiName("operations/addToPersonalToDo");
            thread.setRequestBody(object.toString());
        }
        thread.start();
    }

    private String getDummyRequestObject() {
        JSONObject object = new JSONObject();
        try {


            object.put("dummy", "");


        } catch (Exception e) {


        }

        return object.toString();

    }

    private JSONObject getDummyResponseObject() {
        JSONObject object = new JSONObject();
        try {


            object.put("status", "999");
            object.put("message", "response null");


        } catch (Exception e) {


        }

        return object;

    }

    @Override
    public void onApiResponseRecieved(JSONObject responseObject) {

        if (responseObject != null) {

            mCallback.onApiResponseRecieved(responseObject);
        } else {

            mCallback.onApiResponseRecieved(getDummyResponseObject());
        }

    }
}
