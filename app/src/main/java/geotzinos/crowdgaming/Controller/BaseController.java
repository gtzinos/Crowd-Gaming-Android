package geotzinos.crowdgaming.Controller;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import geotzinos.crowdgaming.General.Config;
import geotzinos.crowdgaming.General.Effect;

/**
 * Created by George Tzinos on 12/8/2016.
 */
public class BaseController extends AppCompatActivity {

    //Ask before close application
    public void AskToCloseApplication(final Context context)
    {
        AlertDialog.Builder exit_alert = new AlertDialog.Builder(this);
        exit_alert.setMessage("Do you really want to close this application ?");
        exit_alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CloseApplication(context);
            }
        });
        exit_alert.setNegativeButton("Cancel",null);
        exit_alert.show();
    }

    //Close app
    public void CloseApplication(Context context)
    {
        ((Activity)context).finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
    /*
        Check for Internet permissions (Android >= 6.0)
     */
    public void CheckInternetPermissions()
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Effect.Alert(this,"Report this error Internet!!!","Got It");

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        Config.MY_PERMISSIONS_INTERNET_REQUEST_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    /*
        Check for Access Fine Location permissions (Android >= 6.0)
     */
    public void CheckAccessFineLocationPermissions()
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Effect.Alert(this,"Report this error FINE!!!","Got It");
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Config.MY_PERMISSIONS_ACCESS_FINE_LOCATION_REQUEST_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    /*
        Check for Access Coarse Location permissions (Android >= 6.0)
     */
    public void CheckAccessCoarseLocationPermissions()
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Effect.Alert(this,"Report this error Coarse!!!","Got It");
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        Config.MY_PERMISSIONS_ACCESS_COARSE_LOCATION_REQUEST_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        final Context context = this;
        switch (requestCode) {
            case Config.MY_PERMISSIONS_INTERNET_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    AlertDialog.Builder exit_alert = new AlertDialog.Builder(this);
                    exit_alert.setMessage("Sorry, we can't set Internet permissions on your device..");
                    exit_alert.setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CloseApplication(context);
                        }
                    });
                    exit_alert.show();
                }
                return;
            }
            case Config.MY_PERMISSIONS_ACCESS_FINE_LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    AlertDialog.Builder exit_alert = new AlertDialog.Builder(this);
                    exit_alert.setMessage("Sorry, we can't set Access Fine Location permissions on your device..");
                    exit_alert.setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CloseApplication(context);
                        }
                    });
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            case Config.MY_PERMISSIONS_ACCESS_COARSE_LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    AlertDialog.Builder exit_alert = new AlertDialog.Builder(this);
                    exit_alert.setMessage("Sorry, we can't set Access Coarse Location permissions on your device..");
                    exit_alert.setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CloseApplication(context);
                        }
                    });
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
