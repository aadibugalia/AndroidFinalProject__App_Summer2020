package ab.lasalle.androidfinalproject.ui.activities.useraccess.viewmodel;

import androidx.annotation.Nullable;

import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.LoggedInUser;

/**
 * Authentication result : success (user details) or error message.
 */
public class LoginResult {
    @Nullable
    private LoggedInUser success;
    @Nullable
    private String error;

    LoginResult(@Nullable String error) {
        this.error = error;
    }

    LoginResult(@Nullable LoggedInUser success) {
        this.success = success;
    }

    @Nullable
    public  LoggedInUser getSuccess() {
        return success;
    }

    @Nullable
    public  String getError() {
        return error;
    }
}