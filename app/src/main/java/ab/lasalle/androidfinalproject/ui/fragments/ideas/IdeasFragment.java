package ab.lasalle.androidfinalproject.ui.fragments.ideas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.Result;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.servermodel.Idea;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.servermodel.Person;
import ab.lasalle.androidfinalproject.ui.fragments.callbacks.MessageFromActivity;
import ab.lasalle.androidfinalproject.ui.fragments.callbacks.MessageToActivity;

public class IdeasFragment extends Fragment implements MessageFromActivity, ApiResponseCallback {


    MessageToActivity sendMessageToActivity;
    private LoggedInUser mLoggedInUser;
    RecyclerView mRecyclerView;
    IdeasRecyclerViewAdapter mAdapter;
    String[] dialogItems;
    List<Integer> unclickableRows, unswipeableRows;
    private RecyclerTouchListener onTouchListener;
    private int openOptionsPosition;
    private OnActivityTouchListener touchListener;
    private enum RequestType {
        FETCH_IDEA,
        UPDATE_TODO,

    }
    private RequestType mRequestType;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.idea_listview_layout, container, false);

        sendMessageToActivity = (HomeActivity)getActivity();
        sendMessageToActivity.OnFragmentReady(this);
        sendMessageToActivity.toggleFabVisibility(View.VISIBLE);
        sendMessageToActivity.toggleAddSearch(true);

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
    public void onPause() {
        super.onPause();
        mRecyclerView.removeOnItemTouchListener(onTouchListener);
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
        mRequestType= RequestType.FETCH_IDEA;
        HttpServiceThread thread = new HttpServiceThread("getAllIdeas", object.toString(), IdeasFragment.this);
        thread.start();
    }

    void setUpRecylerviewData(final List<Idea> ideaList){
        mAdapter = new IdeasRecyclerViewAdapter(getActivity(), ideaList);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        onTouchListener = new RecyclerTouchListener(getActivity(), mRecyclerView);
        onTouchListener
                .setIndependentViews(R.id.rowButton)
                .setViewsToFade(R.id.rowButton)
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                        JSONObject object = null;
                        try {


                            object = new JSONObject();
                            object.put("userName",mLoggedInUser.getUserName() );
                            object.put("ideaID", ideaList.get(position).getId());


                        } catch (Exception e) {


                        }
                        mRequestType= RequestType.UPDATE_TODO;
                        HttpServiceThread thread = new HttpServiceThread("operations/addToPersonalToDo", object.toString(), IdeasFragment.this);
                        thread.start();


                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {

                        JSONObject object = null;
                        try {


                            object = new JSONObject();
                            object.put("userName",mLoggedInUser.getUserName() );
                            object.put("ideaID", ideaList.get(position).getId());


                        } catch (Exception e) {


                        }
                        HttpServiceThread thread = new HttpServiceThread("operations/addToPersonalToDo", object.toString(), IdeasFragment.this);
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
    public void setOnActivityTouchListener(OnActivityTouchListener listener) {
        this.touchListener = listener;
        mRecyclerView.addOnItemTouchListener(onTouchListener);
    }

    @Override
    public void onApiResponseRecieved(JSONObject responseObject) {



        try {
            if (responseObject.get("status").toString().equalsIgnoreCase("0")) {
                switch (mRequestType){

                    case FETCH_IDEA:
                        JSONArray jsonArray  = responseObject.getJSONArray("ideas");



                        final List list = new ArrayList<Idea>();

                        if (jsonArray != null) {
                            int len = jsonArray.length();
                            for (int i=0;i<len;i++){
                                Idea idea = new Idea();
                                JSONObject obj = jsonArray.getJSONObject(i);
                                idea.setContent(obj.getString("content"));
                                idea.setContext(obj.getString("context"));
                                idea.setTitle(obj.getString("title"));
                                idea.setId(obj.getString("id"));

                                list.add(idea);
                            }
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setUpRecylerviewData(list);
                            }
                        });
                        break;
                    case UPDATE_TODO:
                        break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();


        }
    }


}
