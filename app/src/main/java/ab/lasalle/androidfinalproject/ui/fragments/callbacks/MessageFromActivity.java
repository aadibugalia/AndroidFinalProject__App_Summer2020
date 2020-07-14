package ab.lasalle.androidfinalproject.ui.fragments.callbacks;

import android.view.MotionEvent;

import androidx.fragment.app.Fragment;

import com.nikhilpanju.recyclerviewenhanced.OnActivityTouchListener;

public interface MessageFromActivity {

void OnDataRecieved(Object mObject);
void onDispatchTouchEvent(MotionEvent ev);
void setOnActivityTouchListener(OnActivityTouchListener listener);

}
