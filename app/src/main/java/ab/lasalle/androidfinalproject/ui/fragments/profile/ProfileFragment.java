package ab.lasalle.androidfinalproject.ui.fragments.profile;

import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nikhilpanju.recyclerviewenhanced.OnActivityTouchListener;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

import ab.lasalle.androidfinalproject.R;
import ab.lasalle.androidfinalproject.server.callbacks.ApiResponseCallback;
import ab.lasalle.androidfinalproject.server.threads.HttpServiceThread;
import ab.lasalle.androidfinalproject.ui.activities.HomeActivity;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.LoginActivity;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.LoggedInUser;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.Result;
import ab.lasalle.androidfinalproject.ui.fragments.callbacks.MessageFromActivity;
import ab.lasalle.androidfinalproject.ui.fragments.callbacks.MessageToActivity;
import ab.lasalle.androidfinalproject.ui.fragments.ideas.IdeasFragment;
import ab.lasalle.androidfinalproject.ui.fragments.ideas.IdeasRecyclerViewAdapter;
import ab.lasalle.androidfinalproject.ui.fragments.people.PeopleRecyclerViewAdapter;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class ProfileFragment extends Fragment implements MessageFromActivity, ApiResponseCallback {


    private  MessageToActivity sendMessageToActivity;
    private LoggedInUser mLoggedInUser;
    private AlertDialog dialog;
    private TextView nameTextView, locationTextView, dobTextView, firstNameTextView,
            emailTextView, personalIdeasTextView, peopleFollwedTextView;



    RecyclerView mRecyclerView;
    PersonalIdeasRecyclerViewAdapter mAdapter;
    String[] dialogItems;
    List<Integer> unclickableRows, unswipeableRows;
    private RecyclerTouchListener onTouchListener;
    private int openOptionsPosition;
    private OnActivityTouchListener touchListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.layout_profile, container, false);

        nameTextView = root.findViewById(R.id.nameTextView);
        emailTextView = root.findViewById(R.id.emailTextView);
        locationTextView = root.findViewById(R.id.locationTextView);
        dobTextView = root.findViewById(R.id.dobTextView);
        firstNameTextView = root.findViewById(R.id.firstNameTextView);
        personalIdeasTextView = root.findViewById(R.id.personalIdeasTextView);
        personalIdeasTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogForIdeas(getActivity(), mLoggedInUser);
            }
        });
        peopleFollwedTextView = root.findViewById(R.id.peopleFollowedTextView);

        sendMessageToActivity = (HomeActivity) getActivity();
        sendMessageToActivity.OnFragmentReady(this);
        sendMessageToActivity.toggleFabVisibility(View.INVISIBLE);

        (  (ImageButton)root.findViewById(R.id.editProfile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(getActivity(), mLoggedInUser);

            }
        });
        (  (ImageButton)root.findViewById(R.id.logout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity ( new Intent(getActivity(), LoginActivity.class));
              getActivity().finish();

            }
        });
        return root;
    }

    void setProfile(LoggedInUser user) {
        mLoggedInUser=user;
        nameTextView.setText(user.getDisplayName());
        emailTextView.setText(user.getEmail());
        dobTextView.setText(user.getDob());
        firstNameTextView.setText(user.getFirstName());
        locationTextView.setText(user.getCountry());
        personalIdeasTextView.setText(user.getPersonalIdeas().size() + " Ideas");
        peopleFollwedTextView.setText(user.getPeopleFollowed().size() + " Following");


    }

    @Override
    public void OnDataRecieved(Object mObject) {
        setProfile((LoggedInUser) mObject);
    }

    @Override
    public void onDispatchTouchEvent(MotionEvent ev) {
        if (touchListener != null) touchListener.getTouchCoordinates(ev);

    }

    @Override
    public void onApiResponseRecieved(JSONObject responseObject) {

        try {
            dialog.cancel();
            if (responseObject.get("status").toString().equalsIgnoreCase("0")) {
                LoggedInUser loggedInUser = new LoggedInUser();
                JSONObject personObject = responseObject.getJSONObject("person");
                loggedInUser.setCountry(personObject.getString("country"));
                loggedInUser.setDob(personObject.getString("dob"));
                loggedInUser.setEmail(personObject.getString("email"));
                loggedInUser.setFirstName(personObject.getString("firstName"));
                loggedInUser.setLastName(personObject.getString("lastName"));
                loggedInUser.setUserName(personObject.getString("userName"));
                loggedInUser.setUserId(personObject.getString("id"));
                loggedInUser.setDisplayName(loggedInUser.getFirstName() + " " + loggedInUser.getLastName());

                setProfile(loggedInUser);

            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }



    public void showDialog(Context activity, LoggedInUser loggedInUser) {

        View root = getLayoutInflater().inflate(R.layout.registeration_layout, null, false);
        dialog =  new AlertDialog.Builder(activity)
                .setView(root)

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


        final CircularProgressButton registerButton = root.findViewById(R.id.registerButton);
        final EditText locationEditText, dobEditText, firstNameEditText, lastNameEditText, emailEditText, usernameEditText_D, passwordEditText_D;

        lastNameEditText = root.findViewById(R.id.lastNameEditText);
        emailEditText = root.findViewById(R.id.emailEditText);
        locationEditText = root.findViewById(R.id.locationEditText);
        dobEditText = root.findViewById(R.id.dobEditText);
        firstNameEditText = root.findViewById(R.id.firstNameEditText);
        usernameEditText_D = root.findViewById(R.id.username);
        passwordEditText_D = root.findViewById(R.id.password);


        firstNameEditText.setText(loggedInUser.getFirstName());

        emailEditText.setText(loggedInUser.getEmail());
        emailEditText.setEnabled(false);

        dobEditText.setText(loggedInUser.getDob());
        lastNameEditText.setText(loggedInUser.getLastName());
        locationEditText.setText(loggedInUser.getCountry());

        usernameEditText_D.setText(loggedInUser.getUserName());
        usernameEditText_D.setEnabled(false);



registerButton.setText("update");
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerButton.startAnimation();

                JSONObject object = null;
                try {
                    object = new JSONObject();
                    object.put("userName", usernameEditText_D.getText());
                    object.put("password", getMD5(usernameEditText_D.getText()+passwordEditText_D.getText().toString()));
                    object.put("email", emailEditText.getText().toString());
                    object.put("dob", dobEditText.getText().toString());
                    object.put("firstName", firstNameEditText.getText().toString());
                    object.put("lastName", lastNameEditText.getText().toString());
                    object.put("country",  locationEditText.getText().toString());


                } catch (Exception e) {


                }

                HttpServiceThread thread = new HttpServiceThread("updateUser", object.toString(), ProfileFragment.this);
                thread.start();


            }
        });


    }
    public   String getMD5(String input){
        String output="";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            output = number.toString(16);
            while (output.length() < 32)
                output = "0" + output;

        } catch (Exception e) {
            Log.e("MD5", e.getLocalizedMessage());
            output="";
        }
        return output.toUpperCase();

    }


    public void showDialogForIdeas(Context activity, final LoggedInUser loggedInUser) {

        View root = getLayoutInflater().inflate(R.layout.idea_listview_layout, null, false);
        dialog = new AlertDialog.Builder(activity)
                .setView(root)

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        mRecyclerView = (RecyclerView) root.findViewById(R.id.ideaListView);
        mAdapter = new PersonalIdeasRecyclerViewAdapter(getActivity(), loggedInUser.getPersonalIdeas());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        onTouchListener = new RecyclerTouchListener(getActivity(), mRecyclerView);
        onTouchListener
//                .setIndependentViews(R.id.rowButton)
//                .setViewsToFade(R.id.rowButton)
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {

                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {
                        JSONObject object = null;
                        try {


                            object = new JSONObject();

                            object.put("ideaID", loggedInUser.getPersonalIdeas().get(position).getId());


                        } catch (Exception e) {


                        }
                        HttpServiceThread thread = new HttpServiceThread(loggedInUser.getUserName()+"/deleteIdea", object.toString(), ProfileFragment.this);
                        thread.start();
                    }
                })
                .setLongClickable(true, new RecyclerTouchListener.OnRowLongClickListener() {
                    @Override
                    public void onRowLongClicked(int position) {

                    }
                })
                // .setSwipeOptionViews(R.id.add, R.id.delete, R.id.change)
                .setSwipeOptionViews(R.id.delete)
                .setSwipeable(R.id.rowFG, R.id.rowBG, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        String message = "";
                        if (viewID == R.id.add) {
                            message += "Add";
                        } else if (viewID == R.id.delete) {
                            message += "Edit";
                        } else if (viewID == R.id.change) {
                            message += "Change";
                        }
                        message += " clicked for row " + (position + 1);

                    }
                });

    }


    @Override
    public void onPause() {
        super.onPause();
        if(mRecyclerView!=null)
        mRecyclerView.removeOnItemTouchListener(onTouchListener);
    }


    @Override
    public void setOnActivityTouchListener(OnActivityTouchListener listener) {
        this.touchListener = listener;
        mRecyclerView.addOnItemTouchListener(onTouchListener);
    }
}