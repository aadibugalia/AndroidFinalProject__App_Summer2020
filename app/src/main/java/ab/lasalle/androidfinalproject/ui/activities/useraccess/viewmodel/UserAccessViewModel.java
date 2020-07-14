package ab.lasalle.androidfinalproject.ui.activities.useraccess.viewmodel;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ab.lasalle.androidfinalproject.R;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.LoggedInUser;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.LoginRepository;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.LoginResultCallback;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.Result;

public class UserAccessViewModel extends ViewModel implements LoginResultCallback {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    UserAccessViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        loginRepository.login(username, password, this);


    }
    public void register(String username, String password, String dob, String location, String email, String firstName, String lastName) {
        // can be launched in a separate asynchronous job
        loginRepository.register(username, password, dob,location,email,firstName,lastName,this);


    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 4;
    }

    @Override
    public void onLoginPerformed(Result<LoggedInUser> result) {
        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.postValue(new LoginResult(((Result.Success<LoggedInUser>) result).getData()));
        } else {
            loginResult.postValue(new LoginResult(result.toString()));
        }
    }
}