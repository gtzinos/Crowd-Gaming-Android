package geotzinos.crowdgaming.Controller.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import geotzinos.crowdgaming.Controller.Request.MyQuestionnairesPageRequest;
import geotzinos.crowdgaming.Controller.Request.PlayQuestionnairePageRequest;
import geotzinos.crowdgaming.General.Calculation;
import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.Model.Domain.QuestionGroup;
import geotzinos.crowdgaming.Model.Domain.Questionnaire;
import geotzinos.crowdgaming.Model.Domain.User;
import geotzinos.crowdgaming.R;

/**
 * Created by George on 2016-07-02.
 */
public class PlayQuestionnairesAdapter extends BaseAdapter {
    private final Context context;
    private Questionnaire questionnaire;
    private User user;
    private Location location;
    private static LayoutInflater inflater = null;

    public PlayQuestionnairesAdapter(Context context, Questionnaire questionnaire, Location location,User user) {
        this.context = context;
        this.questionnaire = questionnaire;
        this.user = user;
        this.location = location;
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
        TextView directionsTextView;
        Button resetButton;
        Button playButton;
    }

    private void SetTimerValue(QuestionGroup questionGroup, Holder holder,int index) {
        String time_to_complete = questionGroup.getTime_to_complete();
        //With time
        if(time_to_complete != null && !time_to_complete.equals("-1")) {
            String time_left = questionGroup.getTime_left();
            //Dont started
            if(time_left != null && time_left.equals(time_to_complete)) {
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
                StartTimer(questionGroup.getTime_left(),questionGroup.getTime_to_complete(), holder,index);
            }
        }
        //No time
        else {
            holder.groupTimeLeftTextView.setText(String.valueOf("Without time."));
        }
    }

    private void StartTimer(String time_left, String time_to_complete, final Holder holder, final int index) {
        if(time_left == null || time_left.equals("-1")) {
            long time_to_complete_ms = Long.parseLong(time_to_complete) * 60000;
            holder.groupTimeLeftTextView.setText(Html.fromHtml("<div><font color='#5cb85c'>" + String.valueOf(String.format(Locale.getDefault(),
                    "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(time_to_complete_ms),
                    TimeUnit.MILLISECONDS.toMinutes(time_to_complete_ms)
                            - TimeUnit.HOURS
                            .toMinutes(TimeUnit.MILLISECONDS
                                    .toHours(time_to_complete_ms)),
                    TimeUnit.MILLISECONDS.toSeconds(time_to_complete_ms)
                            - TimeUnit.MINUTES
                            .toSeconds(TimeUnit.MILLISECONDS
                                    .toMinutes(time_to_complete_ms))) + "</font></div>")));
            return;
        }
        else if(time_left.equals("0") || Long.parseLong(time_left) < 0)
        {
            holder.groupTimeLeftTextView.setText(Html.fromHtml("<div><font color='#d9534f'> Time expired. </font> </div>"));
            return;
        }

        final long milliseconds = Long.parseLong(time_left) * 60000;
        new CountDownTimer(milliseconds, 1000) {
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
                try {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Question group time expired.")
                            .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    JsonObjectRequest request = new MyQuestionnairesPageRequest().GetQuestionGroups(context, user, questionnaire,null);
                                    RequestQueue mRequestQueue = Volley.newRequestQueue(context);
                                    mRequestQueue.add(request);
                                }
                            })
                            .create()
                            .show();
                } catch (Exception e) {
                    Effect.Log("Class MyQuestionnairesAdapter", e.getMessage());
                }
                holder.playButton.setText("Completed");
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

    private void SetAddress(QuestionGroup questionGroup, final Holder holder) {
        if (questionGroup.getLatitude() != null && questionGroup.getLongitude() != null) {
            holder.directionsTextView.setText(Html.fromHtml(String.valueOf("<a href=\"https://www.google.com/maps/dir//"
                    + questionGroup.getLatitude() + "," + questionGroup.getLongitude() +"\">Get directions</a>")));
            holder.directionsTextView.setMovementMethod(LinkMovementMethod.getInstance());

            double distance = Double.parseDouble(Calculation.calculateDistance(questionGroup,location));
            holder.addressTextView.setText(String.valueOf("Distance: " + distance + "m"));
            if (distance > 0 || questionGroup.getIs_completed()) {
                holder.playButton.setEnabled(false);
            } else {
                holder.playButton.setEnabled(true);
            }
        } else {
            holder.directionsTextView.setText("Without coordinates");
            holder.addressTextView.setText(String.valueOf("Available everywhere."));
            if (!questionGroup.getIs_completed()) {
                holder.playButton.setEnabled(true);
            }
        }
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

        if (answered < total && !questionGroup.getIs_completed()) {
            holder.resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.resetButton.setFocusable(false);
                    holder.resetButton.setClickable(false);
                    holder.resetButton.setActivated(false);

                    JsonObjectRequest request = new PlayQuestionnairePageRequest()
                            .ResetQuestionGroup(context, questionnaire.getId(), questionGroup.getId(), answered, total, holder.answersTextView, holder.resetButton,questionnaire,user);

                    RequestQueue mRequestQueue = Volley.newRequestQueue(context);
                    mRequestQueue.add(request);
                }
            });
        } else {
            holder.resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Effect.Alert(context, "You can't reset a completed question group.", "Okay");
                }
            });
        }
    }

    public void SetPlayButtonListener(final Holder holder, final int position) {
        final User user = (User) ((Activity) context).getIntent().getSerializableExtra("user");
        final Questionnaire questionnaire = (Questionnaire) ((Activity) context).getIntent().getSerializableExtra("questionnaire");

        if (holder.playButton.isEnabled()) {
            holder.playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.playButton.setFocusable(false);
                    holder.playButton.setClickable(false);
                    holder.playButton.setActivated(false);
                    JsonObjectRequest request;
                    long group_id = questionnaire.getQuestionGroupsList().get(position).getId();
                    if (questionnaire.getQuestionGroupsList().get(position).getLatitude() != null && questionnaire.getQuestionGroupsList().get(position).getLongitude() != null) {
                        request = new PlayQuestionnairePageRequest().
                                GetNextQuestion(context, user, questionnaire, group_id, location,holder.playButton);
                    } else {
                        request = new PlayQuestionnairePageRequest()
                                .GetNextQuestion(context, user, questionnaire, group_id, null,holder.playButton);
                    }
                    RequestQueue mRequestQueue = Volley.newRequestQueue(context);
                    mRequestQueue.add(request);
                }
            });
        } else if (questionnaire.getQuestionGroupsList().get(position).getIs_completed()) {
            holder.playButton.setText(String.valueOf("Completed"));
        } else {
            holder.playButton.setText(String.valueOf("Play now"));
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
        holder.directionsTextView = (TextView) rowView.findViewById(R.id.DirectionsTextView);
        holder.resetButton = (Button) rowView.findViewById(R.id.ResetQuestionGroup);
        holder.playButton = (Button) rowView.findViewById(R.id.PlayQuestionGroupButton);

        SetTimerValue(questionnaire.getQuestionGroupsList().get(position), holder,position);
        SetName(questionnaire.getQuestionGroupsList().get(position).getName(),holder);
        SetAnswers(questionnaire.getQuestionGroupsList().get(position).getAnswered_questions(),questionnaire.getQuestionGroupsList().get(position).getTotal_questions(),holder);
        SetPriority(questionnaire.getQuestionGroupsList().get(position).getPriority(),holder);
        SetAddress(questionnaire.getQuestionGroupsList().get(position), holder);
        SetResetButtonListener(holder, position);
        SetPlayButtonListener(holder, position);
        return rowView;
    }
}
