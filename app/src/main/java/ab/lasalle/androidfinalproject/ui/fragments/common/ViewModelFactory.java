package ab.lasalle.androidfinalproject.ui.fragments.common;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.LoginDataSource;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.LoginRepository;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.viewmodel.UserAccessViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SharedViewModel.class)) {
            return (T) new SharedViewModel(DataRepository.getInstance(new APIDataSource()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}