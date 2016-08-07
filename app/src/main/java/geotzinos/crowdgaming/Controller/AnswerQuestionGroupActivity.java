package geotzinos.crowdgaming.Controller;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.Model.Domain.Question;
import geotzinos.crowdgaming.Model.Domain.Questionnaire;
import geotzinos.crowdgaming.Model.Domain.User;
import geotzinos.crowdgaming.R;
import geotzinos.crowdgaming.Request.AnswerQuestionGroupPageRequest;
import geotzinos.crowdgaming.Request.MyQuestionnairesPageRequest;

public class AnswerQuestionGroupActivity  extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback<LocationSettingsResult> {

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
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;
    // Tag name
    protected static final String TAG = "AnswerQuestionGroupActivity";
    /**
     * Constant used in the location settings dialog.
     */
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest;
    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;
    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;

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

        // Kick off the process of building the GoogleApiClient, LocationRequest, and
        // LocationSettingsRequest objects.
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();

        questionTextView.setText(question.getText());
        SetQuestionTimer(this, user, question);
        SetAnswersText(question);
        SetAnswerButtonListener(this, question, user);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Effect.Log("AnswerQuestionGroupActivity", "GoogleApiClient build.");
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(3000);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(3000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    /**
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link com.google.android.gms.location.LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Effect.Log("AnswerQuestionGroupActivity", "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Effect.Log("AnswerQuestionGroupActivity", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(AnswerQuestionGroupActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setMessage("You should give us access on your location service to enter here.")
                            .setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    GoBack();
                                }
                            })
                            .create()
                            .show();
                    Effect.Log("AnswerQuestionGroupActivity", "Pending Intent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage("You should give us access on your location service to enter here.")
                        .setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GoBack();
                            }
                        })
                        .create()
                        .show();
                Effect.Log("AnswerQuestionGroupActivity", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Effect.Log("AnswerQuestionGroupActivity", "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Effect.Log("AnswerQuestionGroupActivity", "User chose not to make required location settings changes.");
                        AlertDialog.Builder alert = new AlertDialog.Builder(this);
                        alert.setMessage("You should give us access on your location service to enter here.")
                                .setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        GoBack();
                                    }
                                })
                                .create()
                                .show();
                        break;
                }
                break;
        }
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("You should give us access on your location service to enter here.")
                    .setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GoBack();
                        }
                    })
                    .create()
                    .show();
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = true;
            }
        });
    }

    private void SetAnswerButtonListener(final Context context, final Question question, final User user) {
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationProviders = new String(Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED));

                if (locationProviders == null || locationProviders.equals("")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("You should give us access on your location service to enter here.")
                            .setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    checkLocationSettings();
                                }
                            })
                            .create()
                            .show();
                    return;
                }
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

        final long milliseconds = (long) question.getTimeToAnswer() * (long) 1000;
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
            Effect.Log("AnswerQuestionGroupActivity", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.logged_in_action_bar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Context context = this;

        switch(item.getItemId())
        {
            case R.id.user_sign_out:
                android.support.v7.app.AlertDialog.Builder signout_alert = new android.support.v7.app.AlertDialog.Builder(this);
                signout_alert.setMessage("Do you really want to log out ?");
                signout_alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File dir = getFilesDir();
                        File file = new File(dir, "user.txt");
                        if(file.exists())
                        {
                            file.delete();
                        }

                        Intent intent = new Intent(context,LoginPageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        ((Activity)context).finish();
                    }
                });
                signout_alert.setNegativeButton("Cancel",null);
                signout_alert.show();
                break;
            case R.id.user_close:
                android.support.v7.app.AlertDialog.Builder exit_alert = new android.support.v7.app.AlertDialog.Builder(this);
                exit_alert.setMessage("Do you really want to close this application ?");
                exit_alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity)context).finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                });
                exit_alert.setNegativeButton("Cancel",null);
                exit_alert.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
            }
        });
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Effect.Log("AnswerQuestionGroupActivity", "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage("You should give us access on your location service to enter here.")
                        .setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GoBack();
                            }
                        })
                        .create()
                        .show();
                return;
            }

            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            startLocationUpdates();
            if(mLocation != null)
            {
                SetAnswerButtonListener(this,question,user);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        onConnectionSuspended(i);
        Effect.Log("AnswerQuestionGroupActivity", "Connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        onConnectionFailed(connectionResult);
        Effect.Log("AnswerQuestionGroupActivity", "Connection failed.");
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("You should give us access on your location service to answer a question.")
                .setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        SetAnswerButtonListener(this,question,user);
    }
}