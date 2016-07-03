package geotzinos.crowdgaming.Controller;

import android.Manifest;
import android.app.Activity;
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
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import geotzinos.crowdgaming.Controller.Adapter.PlayQuestionnairesAdapter;
import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.Model.Domain.Questionnaire;
import geotzinos.crowdgaming.Model.Domain.User;
import geotzinos.crowdgaming.R;

public class PlayQuestionnaireActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    ListView listView;
    TextView timeLeftTextView;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager locationManager;
    private LocationRequest mLocationRequest;
    private Questionnaire questionnaire;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        //GPS Initialization
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
                    alert.setMessage("Questionnaire completed.")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (context != null) {
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
            double latitude = mLocation.getLatitude();
            double longitude = mLocation.getLongitude();
            listView.setAdapter(new PlayQuestionnairesAdapter(this, questionnaire, mLocation));
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
        listView.setAdapter(new PlayQuestionnairesAdapter(this, questionnaire, location));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
