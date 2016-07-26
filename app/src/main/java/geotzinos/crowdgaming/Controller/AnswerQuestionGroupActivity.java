package geotzinos.crowdgaming.Controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import geotzinos.crowdgaming.Controller.Adapter.AnswerQuestionGroupAdapter;
import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.Model.Domain.Question;
import geotzinos.crowdgaming.Model.Domain.Questionnaire;
import geotzinos.crowdgaming.Model.Domain.User;
import geotzinos.crowdgaming.R;

public class AnswerQuestionGroupActivity extends AppCompatActivity {
    ListView listView;
    TextView questionTextView;
    TextView questionTimeTextView;
    User user;
    Questionnaire questionnaire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_question_group_view);
        listView = (ListView) findViewById(R.id.AnswersListView);
        questionTimeTextView = (TextView) findViewById(R.id.QuestionTimeTextView);
        questionTextView = (TextView) findViewById(R.id.QuestionNameTextView);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        questionnaire = (Questionnaire) intent.getSerializableExtra("questionnaire");
        Question question = (Question) intent.getSerializableExtra("question");
        questionTextView.setText(question.getText());
        SetQuestionTimer(this, user, question);
        listView.setAdapter(new AnswerQuestionGroupAdapter(this, question));

        //JsonObjectRequest request = new MyQuestionnairesPageRequest().GetQuestionnaires(this, user, listView);
        //RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        //mRequestQueue.add(request);
    }

    private void SetQuestionTimer(final Context context, final User user, Question question) {
        if (question.getTimeToAnswer() == -1) {
            questionTimeTextView.setText(String.valueOf("Full time to answer."));
            return;
        }

        final long milliseconds = (long) question.getTimeToAnswer() * (long) 60000;
        CountDownTimer timer = new CountDownTimer(milliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                questionTimeTextView.setText(Html.fromHtml("<div><font color='#d9534f'>" + String.valueOf(String.format(Locale.getDefault(),
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
                if (context == null) {
                    return;
                }

                try {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Question time expired.")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(context, PlayQuestionnaireActivity.class);
                                    intent.putExtra("user", user);
                                    intent.putExtra("questionnaire", questionnaire);
                                    context.startActivity(intent);
                                    finish();
                                }
                            })
                            .create()
                            .show();
                } catch (Exception e) {
                    Effect.Log("AnswerQuestionGroupActivity", e.getMessage());
                }
            }
        }.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            GoBack();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        GoBack();
    }

    private void GoBack() {
        try {
            Intent intent = new Intent(this, PlayQuestionnaireActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("questionnaire", questionnaire);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);
            finish();
            return;
        } catch (Exception e) {
            Effect.Log("PlayQuestionnairesActivity", e.getMessage());
        }
    }

}
