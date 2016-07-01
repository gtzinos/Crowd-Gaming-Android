package geotzinos.crowdgaming.Controller.Adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.Html;
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
        return questionnaires.get(position);
    }

    @Override
    public long getItemId(int position) {
        return questionnaires.get(position).getId();
    }

    public class Holder {
        TextView timeLeftTextView;
        TextView questionnaireNameTextView;
        TextView questionnaireDescriptionTextView;
        Button playQuestionnaireButton;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.my_questionnaires_adapter_view, null);
        holder.timeLeftTextView = (TextView) rowView.findViewById(R.id.TimeTextView);
        holder.questionnaireNameTextView = (TextView) rowView.findViewById(R.id.QuestionnaireNameTextView);
        holder.questionnaireDescriptionTextView = (TextView) rowView.findViewById(R.id.DescriptionTextView);
        holder.playQuestionnaireButton = (Button) rowView.findViewById(R.id.PlayButton);

        if (questionnaires.get(position).getTime_left() == 0) {
            if (questionnaires.get(position).getAnswered_questions() == questionnaires.get(position).getAnswered_questions()) {
                holder.playQuestionnaireButton.setFocusable(false);
                holder.playQuestionnaireButton.setClickable(false);
                holder.playQuestionnaireButton.setActivated(false);
                holder.timeLeftTextView.setText(Html.fromHtml("<div><font color='#5cb85c'>Completed</font></div>"));
            } else {
                holder.playQuestionnaireButton.setClickable(true);
                holder.playQuestionnaireButton.setActivated(true);
                holder.timeLeftTextView.setText(Html.fromHtml("<div><font color='#5cb85c'>Running</font></div>"));
            }
        } else if (questionnaires.get(position).getTime_left() == -1) {
            holder.playQuestionnaireButton.setFocusable(false);
            holder.playQuestionnaireButton.setClickable(false);
            holder.playQuestionnaireButton.setActivated(false);
            holder.timeLeftTextView.setText(Html.fromHtml("<div><font color='#f0ad4e'>Not scheduled yet.</font></div>"));
        } else {
            long milliseconds = questionnaires.get(position).getTime_left() * 60000;
            CountDownTimer timer = new CountDownTimer(milliseconds, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long days = 0, hours = 0, minutes = 0, seconds = 0;
                    String timerValue = "";

                    seconds = millisUntilFinished / 1000;

                    days = seconds / 86400;
                    if (days > 0) {
                        seconds = seconds % 86400;
                        timerValue += days + "d ";
                    }

                    hours = seconds / 3600;
                    if (hours > 0) {
                        seconds = seconds % 3600;
                    }
                    timerValue += hours + ":";

                    minutes = seconds / 60;
                    if (minutes > 0) {
                        seconds = seconds % 60;
                    }
                    timerValue += minutes + ":";

                    timerValue += seconds;

                    holder.timeLeftTextView.setText(Html.fromHtml("<div><font color='#d9534f'>" + timerValue + "</font></div>"));
                }

                @Override
                public void onFinish() {
                    holder.playQuestionnaireButton.setEnabled(true);
                }
            }.start();
        }

        holder.questionnaireNameTextView.setText(questionnaires.get(position).getName());
        holder.questionnaireDescriptionTextView.setText(Html.fromHtml(questionnaires.get(position).getDescription()));
        return rowView;
    }
}
