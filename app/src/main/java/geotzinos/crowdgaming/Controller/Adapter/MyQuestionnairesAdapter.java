package geotzinos.crowdgaming.Controller.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import geotzinos.crowdgaming.Model.Domain.Questionnaire;
import geotzinos.crowdgaming.R;

/**
 * Created by George on 2016-06-30.
 */
public class MyQuestionnairesAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Questionnaire> questionnaires;
    private static LayoutInflater inflater = null;

    public MyQuestionnairesAdapter(Context context, ArrayList<Questionnaire> questionnaires) {
        this.context = context;
        this.questionnaires = questionnaires;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return questionnaires.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView timeLeftTextView;
        TextView questionnaireNameTextView;
        TextView questionnaireDescriptionTextView;
        Button playQuestionnaireButton;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.my_questionnaires_adapter_view, null);
        holder.timeLeftTextView = (TextView) rowView.findViewById(R.id.TimeTextView);
        holder.questionnaireNameTextView = (TextView) rowView.findViewById(R.id.QuestionnaireNameTextView);
        holder.questionnaireDescriptionTextView = (TextView) rowView.findViewById(R.id.DescriptionTextView);
        holder.playQuestionnaireButton = (Button) rowView.findViewById(R.id.PlayButton);
        return rowView;
    }
}
