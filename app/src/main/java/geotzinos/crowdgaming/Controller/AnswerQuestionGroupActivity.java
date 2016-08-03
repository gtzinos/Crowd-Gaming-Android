package geotzinos.crowdgaming.Controller;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import geotzinos.crowdgaming.Controller.Adapter.PlayQuestionnairesAdapter;
import geotzinos.crowdgaming.Controller.Request.AnswerQuestionGroupPageRequest;
import geotzinos.crowdgaming.Controller.Request.MyQuestionnairesPageRequest;
import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.Model.Domain.Question;
import geotzinos.crowdgaming.Model.Domain.Questionnaire;
import geotzinos.crowdgaming.Model.Domain.User;
import geotzinos.crowdgaming.R;

public class AnswerQuestionGroupActivity  extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    TextView questionTextView;
    TextView questionTimeTextView;
    RadioButton answer1RadioButton;
    RadioButton answer2RadioButton;
    RadioButton answer3RadioButton;
    RadioButton answer4RadioButton;
    RadioGroup answerGroup;
    Button answerButton;
    CountDownTimer timer;

    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    LocationManager locationManager;
    LocationRequest mLocationRequest;

    User user;
    Questionnaire questionnaire;
    Question question;
    long group_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_question_group_view);

        //Initialization
        questionTimeTextView = (TextView) findViewById(R.id.QuestionTimeTextView);
        questionTextView = (TextView) findViewById(R.id.QuestionNameTextView);
        answer1RadioButton = (RadioButton) findViewById(R.id.answer1_radio_button);
        answer2RadioButton = (RadioButton) findViewById(R.id.answer2_radio_button);
        answer3RadioButton = (RadioButton) findViewById(R.id.answer3_radio_button);
        answer4RadioButton = (RadioButton) findViewById(R.id.answer4_radio_button);
        answerGroup = (RadioGroup) findViewById(R.id.answers_radio_group);
        answerButton = (Button) findViewById(R.id.confirm_answer_button);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        questionnaire = (Questionnaire) intent.getSerializableExtra("questionnaire");
        question = (Question) intent.getSerializableExtra("question");
        group_id = intent.getLongExtra("group_id", -1);

        //GPS Initialization
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        questionTextView.setText(question.getText());
        SetQuestionTimer(this, user, question);
        SetAnswersText(question);
        SetAnswerButtonListener(this, question, user);
    }

    private void SetAnswerButtonListener(final Context context, final Question question, final User user) {
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerButton.setFocusable(false);
                answerButton.setClickable(false);
                answerButton.setActivated(false);

                int radioButtonID = answerGroup.getCheckedRadioButtonId();
                if(radioButtonID == -1)
                {
                    answerButton.setFocusable(true);
                    answerButton.setClickable(true);
                    answerButton.setActivated(true);
                    Effect.Alert(context,"Please select an answer.","Got it");
                }
                else {
                    View radioButton = answerGroup.findViewById(radioButtonID);
                    final int index = answerGroup.indexOfChild(radioButton);
                    if(timer != null){
                        timer.cancel();
                    }
                    JsonObjectRequest request = new AnswerQuestionGroupPageRequest().ConfirmAnswer(context, index, question, user, mLocation,
                            questionnaire, group_id, answerButton);
                    RequestQueue mRequestQueue = Volley.newRequestQueue(context);
                    mRequestQueue.add(request);
                }
            }
        });
    }

    private void SetAnswersText(Question question) {
        if (question.getAnswersList().size() == 4) {
            answer1RadioButton.setText(question.getAnswersList().get(0).getText());
            answer2RadioButton.setText(question.getAnswersList().get(1).getText());
            answer3RadioButton.setText(question.getAnswersList().get(2).getText());
            answer4RadioButton.setText(question.getAnswersList().get(3).getText());
        } else if (question.getAnswersList().size() == 3) {
            answer1RadioButton.setText(question.getAnswersList().get(0).getText());
            answer2RadioButton.setText(question.getAnswersList().get(1).getText());
            answer3RadioButton.setText(question.getAnswersList().get(2).getText());
            answer4RadioButton.setVisibility(View.GONE);
        } else if (question.getAnswersList().size() == 2) {
            answer1RadioButton.setText(question.getAnswersList().get(0).getText());
            answer2RadioButton.setText(question.getAnswersList().get(1).getText());
            answer3RadioButton.setVisibility(View.GONE);
            answer4RadioButton.setVisibility(View.GONE);
        }
    }

    private void SetQuestionTimer(final Context context, final User user, Question question) {
        if (question.getTimeToAnswer() == -1) {
            questionTimeTextView.setText(String.valueOf("Full time to answer."));
            return;
        }

        final long milliseconds = (long) question.getTimeToAnswer() * (long) 60000;
        timer = new CountDownTimer(milliseconds, 1000) {
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
                            .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    GoBack();
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
            //Send request to get groups
            JsonObjectRequest request = new MyQuestionnairesPageRequest().GetQuestionGroups(this, user, questionnaire,null);
            RequestQueue mRequestQueue = Volley.newRequestQueue(this);
            mRequestQueue.add(request);
        } catch (Exception e) {
            Effect.Log("PlayQuestionnairesActivity", e.getMessage());
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(3000)
                .setFastestInterval(3000);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Effect.Log("PlayQuestionnaireActivity", "Location updates.");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {
            //TODO Something else
        } else {
            Effect.Log("PlayQuestionnaireActivity", "Location is null.");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        //Something else
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}