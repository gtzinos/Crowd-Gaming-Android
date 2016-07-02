package geotzinos.crowdgaming.Controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import geotzinos.crowdgaming.Controller.Request.PlayQuestionnairePageRequest;
import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.Model.Domain.Questionnaire;
import geotzinos.crowdgaming.Model.Domain.User;
import geotzinos.crowdgaming.R;

public class PlayQuestionnaireActivity extends AppCompatActivity {
    ListView listView;
    TextView timeLeftTextView;
    static boolean active = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_questionnaire_view);
        Effect.Log("PlayQuestionnaireActivity", "Activity created.");

    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
        //Get extra values
        Intent intent = getIntent();
        Questionnaire questionnaire = (Questionnaire) intent.getSerializableExtra("questionnaire");
        questionnaire.getQuestionGroupsList().clear();
        User user = (User) intent.getSerializableExtra("user");
        //Element initialization
        listView = (ListView) findViewById(R.id.GroupsListView);
        timeLeftTextView = (TextView) findViewById(R.id.QuestionnaireTimeTextView);
        //Start questionnaire timer
        StartQuestionnaireTimer(this, questionnaire.getTime_left_to_end(), user);
        //Send request to get groups
        JsonObjectRequest request = new PlayQuestionnairePageRequest().GetQuestionGroups(this, user, questionnaire, listView);
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.add(request);
    }

    private void StartQuestionnaireTimer(final Context context, long time_left, final User user) {
        final long milliseconds = 5000;
        CountDownTimer timer = new CountDownTimer(milliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftTextView.setText(Html.fromHtml("<div><font color='#d9534f'>" + String.valueOf(String.format(Locale.getDefault(),
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
                if (context == null || !active) {
                    return;
                }

                try {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Questionnaire completed.")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (context != null && active) {
                                        Intent intent = new Intent(context, MyQuestionnairesActiviry.class);
                                        intent.putExtra("user", user);
                                        context.startActivity(intent);
                                        finish();
                                    }
                                }
                            })
                            .create()
                            .show();
                } catch (Exception e) {
                    Effect.Log("PlayQuestionnairesActivity", e.getMessage());
                }
            }
        }.start();
    }
}
