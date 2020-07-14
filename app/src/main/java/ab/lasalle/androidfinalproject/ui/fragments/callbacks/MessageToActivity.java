package ab.lasalle.androidfinalproject.ui.fragments.callbacks;

import android.opengl.Visibility;

import androidx.fragment.app.Fragment;

public interface MessageToActivity {
    void OnFragmentReady(Fragment mFragment);
    void toggleFabVisibility(int mVisibility);
    void toggleAddSearch(boolean isAdd);


}
