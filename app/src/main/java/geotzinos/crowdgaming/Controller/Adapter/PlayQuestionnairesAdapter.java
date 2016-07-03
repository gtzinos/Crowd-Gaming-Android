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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import geotzinos.crowdgaming.Controller.Request.PlayQuestionnairePageRequest;
import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.Model.Domain.QuestionGroup;
import geotzinos.crowdgaming.Model.Domain.Questionnaire;
import geotzinos.crowdgaming.R;

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
        return questionnaire.getQuestionGroupsList().get(position).getId();
    }

    public class Holder {
        TextView groupTimeLeftTextView;
        TextView questionGroupNameTextView;
        TextView answersTextView;
        TextView priorityTextView;
        TextView addressTextView;
        Button resetButton;
        Button playButton;
    }

    private void SetTimerValue(QuestionGroup questionGroup, Holder holder) {
        String time_to_complete = questionGroup.getTime_to_complete();
        //With time
        if(time_to_complete != null && !time_to_complete.equals("-1")) {
            String time_left = questionGroup.getTime_left();
            //Dont started
            if(time_left != null) {
                long millisecondsParsed = Long.parseLong(time_to_complete) * 1000;
                holder.groupTimeLeftTextView.setText(Html.fromHtml("<div><font color='#5cb85c'>" + String.valueOf(String.format(Locale.getDefault(),
                        "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisecondsParsed),
                        TimeUnit.MILLISECONDS.toMinutes(millisecondsParsed)
                                - TimeUnit.HOURS
                                .toMinutes(TimeUnit.MILLISECONDS
                                        .toHours(millisecondsParsed)),
                        TimeUnit.MILLISECONDS.toSeconds(millisecondsParsed)
                                - TimeUnit.MINUTES
                                .toSeconds(TimeUnit.MILLISECONDS
                                        .toMinutes(millisecondsParsed))) + "</font></div>")));
            }
            //Started
            else {
                StartTimer(questionGroup.getTime_left(),holder);
            }
        }
        //No time
        else {
            holder.groupTimeLeftTextView.setText("Without time.");
        }
    }

    private void StartTimer(String time_left,final Holder holder) {
        if(time_left == null) {
            return;
        }
        if(time_left.equals("-1")) {
            return;
        }

        final long milliseconds = Long.parseLong(time_left) * 60000;
        CountDownTimer timer = new CountDownTimer(milliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                holder.groupTimeLeftTextView.setText(Html.fromHtml("<div><font color='#d9534f'>" + String.valueOf(String.format(Locale.getDefault(),
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
                holder.playButton.setEnabled(false);
            }
        }.start();
    }

    private void SetName(String name,final Holder holder)
    {
        holder.questionGroupNameTextView.setText(name);
    }

    private void SetPriority(long priority,final Holder holder) {
        holder.priorityTextView.setText(String.valueOf("Priority: " + priority));
    }

    private void SetAnswers(long answered,long total,final Holder holder) {
        holder.answersTextView.setText(String.valueOf("Answered: " + answered + "/" + total));
    }

    private void SetAddress(String latitude,String longitude,final Holder holder) {
        //TODO Set a link to navigate users to google maps
    }

    private void SetResetButtonListener(final Holder holder, final int position) {
        final QuestionGroup questionGroup = questionnaire.getQuestionGroupsList().get(position);
        String answeredText = holder.answersTextView.getText().toString();
        int startPosition = answeredText.indexOf(" ") + 1;
        int endPosition = answeredText.indexOf("/");
        final long answered = Long.parseLong(answeredText.substring(startPosition, endPosition));
        startPosition = endPosition + 1;
        endPosition = answeredText.length();
        final long total = Long.parseLong(answeredText.substring(startPosition, endPosition));

        if (answered < total) {
            holder.resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JsonObjectRequest request = new PlayQuestionnairePageRequest()
                            .ResetQuestionGroup(context, questionnaire.getId(), questionGroup.getId(), answered, total, holder.answersTextView, holder.resetButton);

                    RequestQueue mRequestQueue = Volley.newRequestQueue(context);
                    mRequestQueue.add(request);
                }
            });
        } else {
            Effect.Alert(context, "You can't reset a completed question group.", "Okay");
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.play_questionnaire_adapter_view, null);
        holder.groupTimeLeftTextView = (TextView) rowView.findViewById(R.id.GroupTimeLeftTextView);
        holder.questionGroupNameTextView = (TextView) rowView.findViewById(R.id.NameTextView);
        holder.answersTextView = (TextView) rowView.findViewById(R.id.AnswersTextView);
        holder.priorityTextView = (TextView) rowView.findViewById(R.id.PriorityTextView);
        holder.addressTextView = (TextView) rowView.findViewById(R.id.AddressTextView);
        holder.resetButton = (Button) rowView.findViewById(R.id.ResetQuestionGroup);
        holder.playButton = (Button) rowView.findViewById(R.id.PlayQuestionGroupButton);

        SetTimerValue(questionnaire.getQuestionGroupsList().get(position), holder);
        SetName(questionnaire.getQuestionGroupsList().get(position).getName(),holder);
        SetAnswers(questionnaire.getQuestionGroupsList().get(position).getAnswered_questions(),questionnaire.getQuestionGroupsList().get(position).getTotal_questions(),holder);
        SetPriority(questionnaire.getQuestionGroupsList().get(position).getPriority(),holder);
        SetAddress(questionnaire.getQuestionGroupsList().get(position).getLatitude(),questionnaire.getQuestionGroupsList().get(position).getLongitude(),holder);
        SetResetButtonListener(holder, position);
        return rowView;
    }
}
