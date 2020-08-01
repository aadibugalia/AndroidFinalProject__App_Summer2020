package ab.lasalle.androidfinalproject.ui.fragments.ideas;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.Observer;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nikhilpanju.recyclerviewenhanced.OnActivityTouchListener;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import org.json.JSONArray;

import org.json.JSONObject;

import java.util.ArrayList;

import java.util.List;

import ab.lasalle.androidfinalproject.R;


import ab.lasalle.androidfinalproject.ui.activities.HomeActivity;

import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.LoggedInUser;

import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.servermodel.Idea;

import ab.lasalle.androidfinalproject.ui.fragments.callbacks.MessageFromActivity;
import ab.lasalle.androidfinalproject.ui.fragments.callbacks.MessageToActivity;
import ab.lasalle.androidfinalproject.ui.fragments.common.APIResult;
import ab.lasalle.androidfinalproject.ui.fragments.common.Constants;
import ab.lasalle.androidfinalproject.ui.fragments.common.SharedViewModel;
import ab.lasalle.androidfinalproject.ui.fragments.common.ViewModelFactory;

public class IdeasFragment extends Fragment implements MessageFromActivity {


    private MessageToActivity sendMessageToActivity;
    private LoggedInUser mLoggedInUser;
    private RecyclerView mRecyclerView;
    private IdeasRecyclerViewAdapter mAdapter;
    private String[] dialogItems;
    private List<Integer> unclickableRows, unswipeableRows;
    private RecyclerTouchListener onTouchListener;
    private int openOptionsPosition;
    private OnActivityTouchListener touchListener;

    private SharedViewModel viewModel;
    private SwipeRefreshLayout pullToRefereshLayout;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.idea_listview_layout, container, false);

        sendMessageToActivity = (HomeActivity) getActivity();
        sendMessageToActivity.OnFragmentReady(this);
        sendMessageToActivity.toggleFabVisibility(View.VISIBLE);
        sendMessageToActivity.toggleAddSearch(true);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.ideaListView);
        pullToRefereshLayout=(SwipeRefreshLayout) root.findViewById(R.id.swipeContainer);

        return root;
    }

    @Override
    public void OnDataRecieved(Object mObject) {
        this.mLoggedInUser = (LoggedInUser) mObject;
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
        viewModel = ViewModelProviders.of(this, new ViewModelFactory())
                .get(SharedViewModel.class);
//implement paging: app side and server

        pullToRefereshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.fetchAllIdeas(Constants.API_REQUEST.FETCH_IDEAS);
            }
        });
        // Configuring the refreshing colors
        pullToRefereshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



        viewModel.getAPIResult().observe(this, new Observer<APIResult>() {
            @Override
            public void onChanged(@Nullable APIResult apiResult) {
                pullToRefereshLayout.setRefreshing(false);
                if (apiResult == null) {
                    return;
                }

                if (apiResult.getError() != null) {

                    Toast.makeText(getActivity().getApplicationContext(), "", Toast.LENGTH_LONG).show();
                }
                if (apiResult.getSuccess() != null) {

                    switch (apiResult.getRequestType()) {
                        case FETCH_IDEAS:

                            try {
                                JSONArray jsonArray = apiResult.getSuccess().getJSONArray("ideas");


                                final List list = new ArrayList<Idea>();

                                if (jsonArray != null) {
                                    int len = jsonArray.length();
                                    for (int i = 0; i < len; i++) {
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
                            } catch (Exception e) {


                            }
                            break;


                    }


                }


            }
        });



    }

    private void setUpRecylerviewData(final List<Idea> ideaList) {
        if (mAdapter == null) {

            mAdapter = new IdeasRecyclerViewAdapter(getActivity(), ideaList);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setItemViewCacheSize(5);

            mRecyclerView.setAdapter(mAdapter);

        } else {

            mAdapter.setModelList(ideaList);
            mAdapter.notifyDataSetChanged();

        }


        onTouchListener = new RecyclerTouchListener(getActivity(), mRecyclerView);
        onTouchListener
                .setIndependentViews(R.id.rowButton)
                .setViewsToFade(R.id.rowButton)
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {


                        viewModel.updateToDo(Constants.API_REQUEST.UPDATE_TODO, mLoggedInUser.getUserName(), ideaList.get(position).getId());


                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {
                        viewModel.updateToDo(Constants.API_REQUEST.UPDATE_TODO, mLoggedInUser.getUserName(), ideaList.get(position).getId());

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


}
