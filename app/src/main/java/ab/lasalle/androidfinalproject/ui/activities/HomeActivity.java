package ab.lasalle.androidfinalproject.ui.activities;

import android.content.Context;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.melnykov.fab.FloatingActionButton;
import com.nikhilpanju.recyclerviewenhanced.OnActivityTouchListener;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ab.lasalle.androidfinalproject.R;
import ab.lasalle.androidfinalproject.server.callbacks.ApiResponseCallback;
import ab.lasalle.androidfinalproject.server.threads.HttpServiceThread;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.LoggedInUser;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.servermodel.Idea;
import ab.lasalle.androidfinalproject.ui.fragments.callbacks.MessageFromActivity;
import ab.lasalle.androidfinalproject.ui.fragments.callbacks.MessageToActivity;
import ab.lasalle.androidfinalproject.ui.fragments.common.APIResult;
import ab.lasalle.androidfinalproject.ui.fragments.common.Constants;
import ab.lasalle.androidfinalproject.ui.fragments.common.SharedViewModel;
import ab.lasalle.androidfinalproject.ui.fragments.common.ViewModelFactory;
import ab.lasalle.androidfinalproject.ui.fragments.ideas.IdeasFragment;
import ab.lasalle.androidfinalproject.ui.fragments.people.PeopleFragment;
import ab.lasalle.androidfinalproject.ui.fragments.profile.ProfileFragment;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class HomeActivity extends AppCompatActivity implements MessageToActivity, RecyclerTouchListener.RecyclerTouchListenerHelper {


    private SharedViewModel viewModel;
    FloatingActionButton fab;
    Fragment mChildFragment;
    MessageFromActivity sendMessageToChildFragment;
    LoggedInUser loggedInUser;
    private AlertDialog dialog;
    private boolean isAdd;


    private EditText userResultEditText;

    CircularProgressButton searchButton, followButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loggedInUser = (LoggedInUser) getIntent().getSerializableExtra("user");
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdd) {

                    showDialogCreateIdea(HomeActivity.this, loggedInUser);
                } else {

                    showDialogSearchUser(HomeActivity.this, loggedInUser);
                }
            }
        });
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_idea, R.id.navigation_favorites, R.id.navigation_profile)
                .build();
        navView.setItemIconTintList(null);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        viewModel = ViewModelProviders.of(this, new ViewModelFactory())
                .get(SharedViewModel.class);


    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.getAPIResult().observe(this, new Observer<APIResult>() {
            @Override
            public void onChanged(@Nullable APIResult apiResult) {
                if (apiResult == null) {
                    return;
                }

                if (apiResult.getError() != null) {

                }
                if (apiResult.getSuccess() != null) {

                    switch (apiResult.getRequestType()) {
                        case SEARCH_USER:

                            try {
                                final JSONObject person = apiResult.getSuccess().getJSONObject("person");
                                HomeActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            searchButton.stopAnimation();
                                            userResultEditText.setText(person.getString("email"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (Exception e) {


                            }
                            break;

                        case FOLLOW_USER:
                            dialog.cancel();
                            break;

                        case REGISTER_IDEA:
                            dialog.cancel();
                            break;


                    }


                }


            }
        });


    }

    @Override
    public void OnFragmentReady(Fragment mFragment) {
        this.mChildFragment = mFragment;

        if (mFragment instanceof ProfileFragment) {
            sendMessageToChildFragment = (ProfileFragment) mChildFragment;
        } else if (mFragment instanceof IdeasFragment) {
            sendMessageToChildFragment = (IdeasFragment) mChildFragment;
        } else if (mFragment instanceof PeopleFragment) {
            sendMessageToChildFragment = (PeopleFragment) mChildFragment;
        }

        sendMessageToChildFragment.OnDataRecieved(loggedInUser);

    }


    @Override
    public void toggleFabVisibility(int mVisibility) {
        fab.setVisibility(mVisibility);
    }

    @Override
    public void toggleAddSearch(boolean isAdd) {
        this.isAdd = isAdd;
        if (isAdd) {
            fab.setImageResource(R.drawable.add);

        } else {
            fab.setImageResource(R.drawable.search);
        }

    }


    public void showDialogCreateIdea(Context activity, final LoggedInUser loggedInUser) {

        View root = getLayoutInflater().inflate(R.layout.idea_layout, null, false);
        dialog = new AlertDialog.Builder(activity)
                .setView(root)

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


        final CircularProgressButton registerButton = root.findViewById(R.id.registerButton);
        final EditText titleEditText, contextEditText, contentEditText;

        titleEditText = root.findViewById(R.id.ideaTitleEditText);
        contextEditText = root.findViewById(R.id.ideaContextEditText);
        contentEditText = root.findViewById(R.id.ideaContentEditText);


        registerButton.setText("update");
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerButton.startAnimation();


                viewModel.registerIdea(Constants.API_REQUEST.REGISTER_IDEA, loggedInUser.getUserName(), titleEditText.getText().toString(),
                        contextEditText.getText().toString(), contentEditText.getText().toString());


            }
        });


    }

    public void showDialogSearchUser(Context activity, final LoggedInUser loggedInUser) {

        View root = getLayoutInflater().inflate(R.layout.search_layout, null, false);
        dialog = new AlertDialog.Builder(activity)
                .setView(root)

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


        searchButton = root.findViewById(R.id.searchButton);
        followButton = root.findViewById(R.id.followButton);
        final EditText usernameEdittext;

        usernameEdittext = root.findViewById(R.id.usernameToSearchTextView);
        userResultEditText = root.findViewById(R.id.userResultEditText);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchButton.startAnimation();

                viewModel.searchUser(Constants.API_REQUEST.SEARCH_USER, usernameEdittext.getText().toString());


            }
        });

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followButton.startAnimation();

                viewModel.updateFollowingList(Constants.API_REQUEST.FOLLOW_USER, loggedInUser.getUserName(), usernameEdittext.getText().toString());


            }
        });

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        sendMessageToChildFragment.onDispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void setOnActivityTouchListener(OnActivityTouchListener listener) {
        sendMessageToChildFragment.setOnActivityTouchListener(listener);
    }

}