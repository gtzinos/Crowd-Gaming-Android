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
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.ListView;
import android.widget.TextView;

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

import geotzinos.crowdgaming.Adapter.PlayQuestionnairesAdapter;
import geotzinos.crowdgaming.General.Calculation;
import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.Model.Domain.QuestionGroup;
import geotzinos.crowdgaming.Model.Domain.Questionnaire;
import geotzinos.crowdgaming.Model.Domain.User;
import geotzinos.crowdgaming.R;

public class PlayQuestionnaireActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback<LocationSettingsResult> {

    ListView listView;
    TextView timeLeftTextView;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager locationManager;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;
    private Questionnaire questionnaire;
    private User user;

    protected static final String TAG = "PlayQuestionnaireActivity";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Initialization
        setContentView(R.layout.play_questionnaire_view);
        Effect.Log("PlayQuestionnaireActivity", "Activity created.");
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_GLOBAL);

        //Get extra values
        Intent intent = getIntent();
        questionnaire = (Questionnaire) intent.getSerializableExtra("questionnaire");
        user = (User) intent.getSerializableExtra("user");

        //Element initialization
        listView = (ListView) findViewById(R.id.GroupsListView);
        timeLeftTextView = (TextView) findViewById(R.id.QuestionnaireTimeTextView);

        //Start questionnaire timer
        StartQuestionnaireTimer(this, Long.parseLong(String.valueOf(questionnaire.getTime_left_to_end())), user);

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Kick off the process of building the GoogleApiClient, LocationRequest, and
        // LocationSettingsRequest objects.
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();

        CheckIfCompleted();
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Effect.Log("PlayQuestionnaireActivity", "GoogleApiClient build.");
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
                Effect.Log("PlayQuestionnaireActivity", "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Effect.Log("PlayQuestionnaireActivity", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(PlayQuestionnaireActivity.this, REQUEST_CHECK_SETTINGS);
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
                    Effect.Log("PlayQuestionnaireActivity", "Pending Intent unable to execute request.");
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
                Effect.Log("PlayQuestionnaireActivity", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
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
                        Effect.Log("PlayQuestionnaireActivity", "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Effect.Log("PlayQuestionnaireActivity", "User chose not to make required location settings changes.");
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

    private void StartQuestionnaireTimer(final Context context, long time_left, final User user) {
        final long milliseconds = time_left * (long) 60000;
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
                if (context == null) {
                    return;
                }

                try {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Questionnaire time expired.")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (context != null) {
                                        Intent intent = new Intent(context, MyQuestionnairesActiviry.class);
                                        intent.putExtra("user", user);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
            // if (active) {
            User user = (User) ((Activity) this).getIntent().getSerializableExtra("user");
            Intent intent = new Intent(this, MyQuestionnairesActiviry.class);
            intent.putExtra("user", user);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);
            finish();
            return;
            //  }
        } catch (Exception e) {
            Effect.Log("PlayQuestionnairesActivity", e.getMessage());
        }
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
        Effect.Log("PlayQuestionnairesActivity", "Connected to GoogleApiClient");

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
            startLocationUpdates();
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

            if(mLocation != null)
            {
                if(listView.getAdapter() != null && listView.getAdapter().getItem(0) != null) {
                    questionnaire = (Questionnaire) listView.getAdapter().getItem(0);
                }
                listView.setAdapter(new PlayQuestionnairesAdapter(this, questionnaire, mLocation,user));
            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Effect.Log("PlayQuestionnaireActivity", "Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
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
    }

    @Override
    public void onLocationChanged(Location location) {
        try {

            if (listView.getAdapter() != null && listView.getAdapter().getItem(0) != null) {
                for (int i = 0; i < questionnaire.getQuestionGroupsList().size(); i++) {
                    View v = listView.getChildAt(i -
                            listView.getFirstVisiblePosition());

                    QuestionGroup questionGroup = (QuestionGroup) listView.getAdapter().getItem(i);

                    if (v == null || questionGroup == null)
                        continue;

                    TextView addressTextView = (TextView) v.findViewById(R.id.AddressTextView);
                    Button playButton = (Button) v.findViewById(R.id.PlayQuestionGroupButton);

                    if (questionGroup.getLatitude() != null && questionGroup.getLongitude() != null) {
                        double distance = Double.parseDouble(Calculation.calculateDistance(questionGroup, location));
                        addressTextView.setText(String.valueOf("Distance: " + distance + "m"));
                        if (distance > 0 || questionGroup.getIs_completed()) {
                            playButton.setEnabled(false);
                        } else {
                            playButton.setEnabled(true);
                        }
                    }
                }
            }
            else {
                listView.setAdapter(new PlayQuestionnairesAdapter(this,questionnaire,location,user));
            }
        }catch(Exception e)
        {
            return;
        }
    }

    //Check if a questionnaire is completed. Will redirect user to his questionnaires page
    public void CheckIfCompleted() {
        boolean completed = true;
        final Context context = this;

        for(int i=0;i<questionnaire.getQuestionGroupsList().size();i++)
        {
            if(!questionnaire.getQuestionGroupsList().get(i).getIs_completed())
            {
                completed = false;
                break;
            }
        }

        if(completed)
        {
            android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(this);
            alert.setMessage("Questionnaire completed. you will be redirected to your questionnaires page.")
                    .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(),MyQuestionnairesActiviry.class);
                            intent.putExtra("user", user);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            startActivity(intent);
                            ((Activity)context).finish();
                        }
                    })
                    .create()
                    .show();
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
}
