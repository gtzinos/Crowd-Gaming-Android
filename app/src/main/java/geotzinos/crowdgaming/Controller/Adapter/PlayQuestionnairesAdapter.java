package geotzinos.crowdgaming.Controller.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import geotzinos.crowdgaming.Model.Domain.Questionnaire;

/**
 * Created by George on 2016-07-02.
 */
public class PlayQuestionnairesAdapter extends BaseAdapter {
    private Context context;
    private Questionnaire questionnaire;
    private static LayoutInflater inflater = null;

    public PlayQuestionnairesAdapter(Context context, Questionnaire questionnaire) {
        this.context = context;
        this.questionnaire = questionnaire;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return questionnaire.getQuestionGroupsList().size();
    }

    @Override
    public Object getItem(int position) {
        return questionnaire.getQuestionGroupsList().get(position);
    }

    @Override
    public long getItemId(int position) {
        //No question group id
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
