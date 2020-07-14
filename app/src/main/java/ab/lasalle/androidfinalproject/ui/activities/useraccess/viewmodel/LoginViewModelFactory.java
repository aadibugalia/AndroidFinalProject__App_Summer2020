package ab.lasalle.androidfinalproject.ui.activities.useraccess.viewmodel;


import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.LoginDataSource;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.LoginRepository;


/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserAccessViewModel.class)) {
            return (T) new UserAccessViewModel(LoginRepository.getInstance(new LoginDataSource()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}