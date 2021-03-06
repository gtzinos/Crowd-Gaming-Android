package tzinos.crowdgaming.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import tzinos.crowdgaming.Model.Domain.Questionnaire;
import tzinos.crowdgaming.Model.Domain.User;
import tzinos.crowdgaming.R;
import tzinos.crowdgaming.Request.MyQuestionnairesPageRequest;

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
            if (questionnaires.get(position).getIs_completed()) {
                holder.playQuestionnaireButton.setFocusable(false);
                holder.playQuestionnaireButton.setClickable(false);
                holder.playQuestionnaireButton.setActivated(false);
                holder.timeLeftTextView.setText(Html.fromHtml("<div><font color='#5cb85c'>Completed</font></div>"));
            } else {
                holder.playQuestionnaireButton.setClickable(true);
                holder.playQuestionnaireButton.setActivated(true);
                holder.timeLeftTextView.setText(Html.fromHtml("<div><font color='#5cb85c'>Running</font></div>"));
                /*
                    Go to play questionnaire activity
                */
                holder.playQuestionnaireButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.playQuestionnaireButton.setFocusable(false);
                        holder.playQuestionnaireButton.setClickable(false);
                        holder.playQuestionnaireButton.setActivated(false);
                        User user = (User) ((Activity) context).getIntent().getSerializableExtra("user");
                        //Send request to get groups
                        JsonObjectRequest request = new MyQuestionnairesPageRequest().GetQuestionGroups(context, user, questionnaires.get(position),holder.playQuestionnaireButton);
                        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
                        mRequestQueue.add(request);
                    }
                });
            }
        } else if (questionnaires.get(position).getTime_left() == -1) {
            holder.playQuestionnaireButton.setFocusable(false);
            holder.playQuestionnaireButton.setClickable(false);
            holder.playQuestionnaireButton.setActivated(false);
            holder.timeLeftTextView.setText(Html.fromHtml("<div><font color='#f0ad4e'>Not scheduled yet.</font></div>"));
        } else {
            final long milliseconds = questionnaires.get(position).getTime_left() * 60000;
            CountDownTimer timer = new CountDownTimer(milliseconds, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    holder.timeLeftTextView.setText(Html.fromHtml("<div><font color='#d9534f'>" + String.valueOf(String.format(Locale.getDefault(),
                            "%02d:%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                                    - TimeUnit.HOURS
                                    .toMinutes(TimeUnit.MILLISECONDS
                                            .toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                                    - TimeUnit.MINUTES
                                    .toSeconds(TimeUnit.MILLISECONDS
                                            .toMinutes(millisUntilFinished))) + "</font></div>")));
                }

                @Override
                public void onFinish() {
                    if (questionnaires.get(position).getAnswered_questions() == questionnaires.get(position).getTotal_questions() || questionnaires.get(position).getIs_completed()) {
                        holder.playQuestionnaireButton.setFocusable(false);
                        holder.playQuestionnaireButton.setClickable(false);
                        holder.playQuestionnaireButton.setActivated(false);
                        holder.timeLeftTextView.setText(Html.fromHtml("<div><font color='#5cb85c'>Completed</font></div>"));
                    } else {
                        holder.playQuestionnaireButton.setClickable(true);
                        holder.playQuestionnaireButton.setActivated(true);
                        holder.timeLeftTextView.setText(Html.fromHtml("<div><font color='#5cb85c'>Running</font></div>"));
                        /*
                            Go to play questionnaire activity
                        */
                        holder.playQuestionnaireButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.playQuestionnaireButton.setFocusable(false);
                                holder.playQuestionnaireButton.setClickable(false);
                                holder.playQuestionnaireButton.setActivated(false);

                                User user = (User) ((Activity) context).getIntent().getSerializableExtra("user");
                                //Send request to get groups
                                JsonObjectRequest request = new MyQuestionnairesPageRequest().GetQuestionGroups(context, user, questionnaires.get(position),holder.playQuestionnaireButton);
                                RequestQueue mRequestQueue = Volley.newRequestQueue(context);
                                mRequestQueue.add(request);
                            }
                        });
                    }
                }
            }.start();
        }

        holder.questionnaireNameTextView.setText(questionnaires.get(position).getName());
        holder.questionnaireDescriptionTextView.setText(Html.fromHtml(questionnaires.get(position).getDescription()));
        return rowView;
    }
}
