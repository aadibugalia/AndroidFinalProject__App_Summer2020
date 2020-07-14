package ab.lasalle.androidfinalproject.ui.fragments.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ab.lasalle.androidfinalproject.R;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.servermodel.Idea;
import ab.lasalle.androidfinalproject.ui.fragments.ideas.IdeasRecyclerViewAdapter;

public class PersonalIdeasRecyclerViewAdapter extends RecyclerView.Adapter<PersonalIdeasRecyclerViewAdapter.MainViewHolder> {
    LayoutInflater inflater;
    List<Idea> modelList;

    public PersonalIdeasRecyclerViewAdapter(Context context, List<Idea> list) {
        inflater = LayoutInflater.from(context);
        modelList = new ArrayList<>(list);
    }

    @Override
    public PersonalIdeasRecyclerViewAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.newidea_listitem_structure, parent, false);
        return new PersonalIdeasRecyclerViewAdapter.MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PersonalIdeasRecyclerViewAdapter.MainViewHolder holder, int position) {
        holder.bindData(modelList.get(position));
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    class MainViewHolder extends RecyclerView.ViewHolder {

        TextView mainText, subText;
        Button rowButton;

        public MainViewHolder(View itemView) {
            super(itemView);
            mainText = (TextView) itemView.findViewById(R.id.mainText);
            subText = (TextView) itemView.findViewById(R.id.subText);
            rowButton = (Button) itemView.findViewById(R.id.rowButton);
        }

        public void bindData(Idea idea) {
            mainText.setText(idea.getTitle());
            subText.setText(idea.getContent());
            rowButton.setText("Delete");
        }
    }

}