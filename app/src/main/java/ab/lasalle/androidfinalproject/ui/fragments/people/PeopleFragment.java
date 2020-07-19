package ab.lasalle.androidfinalproject.ui.fragments.people;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ab.lasalle.androidfinalproject.R;
import ab.lasalle.androidfinalproject.server.callbacks.ApiResponseCallback;
import ab.lasalle.androidfinalproject.server.threads.HttpServiceThread;
import ab.lasalle.androidfinalproject.ui.activities.HomeActivity;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.LoggedInUser;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.servermodel.Idea;
import ab.lasalle.androidfinalproject.ui.fragments.callbacks.MessageFromActivity;
import ab.lasalle.androidfinalproject.ui.fragments.callbacks.MessageToActivity;
import ab.lasalle.androidfinalproject.ui.fragments.ideas.IdeasFragment;
import ab.lasalle.androidfinalproject.ui.fragments.ideas.IdeasRecyclerViewAdapter;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class PeopleFragment extends Fragment implements MessageFromActivity, RecyclerTouchListener.RecyclerTouchListenerHelper, ApiResponseCallback {





    MessageToActivity sendMessageToActivity;
    private AlertDialog dialog;

    RecyclerView mRecyclerView;
    PeopleRecyclerViewAdapter mAdapter;
    String[] dialogItems;
    private LoggedInUser mLoggedInUser;
    List<Integer> unclickableRows, unswipeableRows;
    private RecyclerTouchListener onTouchListener;
    private int openOptionsPosition;
    private OnActivityTouchListener touchListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.idea_listview_layout, container, false);

        sendMessageToActivity = (HomeActivity)getActivity();
        sendMessageToActivity.OnFragmentReady(this);
        sendMessageToActivity.toggleFabVisibility(View.VISIBLE);
        sendMessageToActivity.toggleAddSearch(false);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.ideaListView);
        return root;
    }

    @Override
    public void OnDataRecieved(Object mObject) {
this.mLoggedInUser=(LoggedInUser) mObject;
    }

    @Override
    public void onDispatchTouchEvent(MotionEvent ev) {
        if (touchListener != null) touchListener.getTouchCoordinates(ev);

    }

    @Override
    public void onResume() {
        super.onResume();

        JSONObject object = null;
        try {


            object = new JSONObject();
            object.put("dummy", "");


        } catch (Exception e) {


        }
        HttpServiceThread thread = new HttpServiceThread("/getDetails/"+ mLoggedInUser.getUserName()+"/todos", object.toString(), PeopleFragment.this);
        thread.start();
    }

    public void showDialogCreateIdea(Context activity, final LoggedInUser loggedInUser) {

        View root = getLayoutInflater().inflate(R.layout.idea_listview_layout, null, false);
        dialog = new AlertDialog.Builder(activity)
                .setView(root)

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        mRecyclerView = (RecyclerView) root.findViewById(R.id.ideaListView);
        mAdapter = new PeopleRecyclerViewAdapter(getActivity(), loggedInUser.getPersonalIdeas());
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
    public void setOnActivityTouchListener(OnActivityTouchListener listener) {
        this.touchListener = listener;
    }

    @Override
    public void onApiResponseRecieved(JSONObject responseObject) {
        try {
            if (responseObject.get("status").toString().equalsIgnoreCase("0")) {
                //LoggedInUser loggedInUser = new LoggedInUser();
                JSONArray jsonArray  = responseObject.getJSONArray("ideas");
//                loggedInUser.setCountry(personObject.getString("country"));
//                loggedInUser.setDob(personObject.getString("dob"));
//                loggedInUser.setEmail(personObject.getString("email"));
//                loggedInUser.setFirstName(personObject.getString("firstName"));
//                loggedInUser.setLastName(personObject.getString("lastName"));
//                loggedInUser.setUserName(personObject.getString("userName"));
//                loggedInUser.setUserId(personObject.getString("id"));
//                loggedInUser.setDisplayName(loggedInUser.getFirstName() + " " + loggedInUser.getLastName());

                final List list = new ArrayList<Idea>();

                if (jsonArray != null) {
                    int len = jsonArray.length();
                    for (int i=0;i<len;i++){
                        Idea idea = new Idea();
                        JSONObject obj = jsonArray.getJSONObject(i);
                        idea.setContent(obj.getString("content"));
                        idea.setContext(obj.getString("context"));
                        idea.setTitle(obj.getString("title"));

                        list.add(idea);
                    }
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setUpRecylerviewData(list);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();


        }

    }

    void setUpRecylerviewData(List<Idea> ideaList){
        mAdapter = new PeopleRecyclerViewAdapter(getActivity(), ideaList);
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

}