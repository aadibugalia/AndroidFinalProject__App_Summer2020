package ab.lasalle.androidfinalproject.ui.fragments.common;

import org.json.JSONObject;

import ab.lasalle.androidfinalproject.server.callbacks.ApiResponseCallback;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.LoggedInUser;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.LoginDataSource;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.LoginResultCallback;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.Result;

public class DataRepository implements ApiResponseCallback {

    private static volatile DataRepository instance;
    private APIDataSource mDataSource;
    private ApiResponseCallback mCallback;

    private DataRepository(APIDataSource dataSource) {
        this.mDataSource = dataSource;

    }

    public static DataRepository getInstance(APIDataSource apiDataSource) {

        if (instance == null) {
            instance = new DataRepository(apiDataSource);
        }
        return instance;
    }



    public void fetchAllIdeas(Constants.API_REQUEST requestType, ApiResponseCallback callback) {
        this.mCallback = callback;
        mDataSource.fetchAllIdeas(requestType,callback);
    }

    public void searchUser(Constants.API_REQUEST requestType, String userName, ApiResponseCallback callback) {
        this.mCallback = callback;
        mDataSource.searchUser(requestType, userName,this);
    }

    public void updateToDo(Constants.API_REQUEST requestType, String userName, String ideaId, ApiResponseCallback callback) {
        this.mCallback = callback;
        mDataSource.fetchAllIdeas(requestType, this);
    }
  public void updateFollowingList(Constants.API_REQUEST requestType, String userName, String userNametoFollow , ApiResponseCallback callback) {
        this.mCallback = callback;
        mDataSource.fetchAllIdeas(requestType, this);
    }


    public void registerIdea(Constants.API_REQUEST requestType, String userName, String title, String content, String context, ApiResponseCallback callback) {
        this.mCallback = callback;
        mDataSource.registerIdea(requestType, userName, title, content, context,this);
    }


    @Override
    public void onApiResponseRecieved(JSONObject responseObject) {

        mCallback.onApiResponseRecieved(responseObject);



    }
}
