package ab.lasalle.androidfinalproject.ui.fragments.people;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ab.lasalle.androidfinalproject.R;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.servermodel.Idea;

public class PeopleRecyclerViewAdapter extends RecyclerView.Adapter<PeopleRecyclerViewAdapter.MainViewHolder> {
    LayoutInflater inflater;
    List<Idea> modelList;

    public PeopleRecyclerViewAdapter(Context context, List<Idea> list) {
        inflater = LayoutInflater.from(context);
        modelList = new ArrayList<>(list);
    }

    @Override
    public PeopleRecyclerViewAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.idea_listview_itemstructure, parent, false);
        return new PeopleRecyclerViewAdapter.MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PeopleRecyclerViewAdapter.MainViewHolder holder, int position) {
        holder.bindData(modelList.get(position));
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    class MainViewHolder extends RecyclerView.ViewHolder {

        TextView mainText, subText;

        public MainViewHolder(View itemView) {
            super(itemView);
            mainText = (TextView) itemView.findViewById(R.id.title);
            subText = (TextView) itemView.findViewById(R.id.content);
        }

        public void bindData(Idea idea) {
            mainText.setText(idea.getTitle());
            subText.setText(idea.getContent());
        }
    }
}
