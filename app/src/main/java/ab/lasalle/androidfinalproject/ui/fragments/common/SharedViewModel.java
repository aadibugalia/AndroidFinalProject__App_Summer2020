package ab.lasalle.androidfinalproject.ui.fragments.common;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

import ab.lasalle.androidfinalproject.server.callbacks.ApiResponseCallback;


public class SharedViewModel extends ViewModel implements ApiResponseCallback {


    private MutableLiveData<APIResult> apiResult = new MutableLiveData<>();
    private DataRepository mRepository;
    private Constants.API_REQUEST mRequestType;

    public SharedViewModel(DataRepository instance) {
        this.mRepository = instance;

    }


    public LiveData<APIResult> getAPIResult() {
        return apiResult;
    }

    public void fetchAllIdeas(Constants.API_REQUEST requestType) {

        mRepository.fetchAllIdeas(requestType, this);
        this.mRequestType= requestType;

    }


    public void searchUser(Constants.API_REQUEST requestType, String userName) {

        mRepository.searchUser(requestType, userName,this);
        this.mRequestType= requestType;

    }

    public void updateToDo(Constants.API_REQUEST requestType, String userName, String ideaId) {

        mRepository.updateToDo(requestType, userName, ideaId, this);
        this.mRequestType= requestType;

    }

    public void updateFollowingList(Constants.API_REQUEST requestType, String userName, String userNametoFollow) {

        mRepository.updateToDo(requestType, userName, userNametoFollow, this);
        this.mRequestType= requestType;

    }

    public void registerIdea(Constants.API_REQUEST requestType, String userName, String title, String content, String context) {

        mRepository.registerIdea(requestType, userName, title, content,context,this);
        this.mRequestType= requestType;

    }


    @Override
    public void onApiResponseRecieved(JSONObject responseObject) {

        try {
            if (responseObject.get("status").toString().equalsIgnoreCase("0")) {

                apiResult.postValue(new APIResult(responseObject,mRequestType));

            }else {
                apiResult.postValue(new APIResult(responseObject.getString("message"),mRequestType));

            }
        }catch (Exception e){
            apiResult.postValue(new APIResult("Please Try Again Later",mRequestType));
        }
    }
}
