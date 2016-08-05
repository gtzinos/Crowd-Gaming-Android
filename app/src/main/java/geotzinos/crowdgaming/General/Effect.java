package geotzinos.crowdgaming.General;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by George on 2016-06-26.
 */
public class Effect {

    //Spinner refference
    private static ProgressDialog spinner;
    private static String message = "Please wait while loading..";
    private static String headerMessage = "Loading";

    /*
        Display spinner on context page
        @param context to display spinner
        @param headerMessage (Title of progress dialog)
        @param message text to display
    */
    public static void ShowSpinner(Context context, String headerMessage, String message) {
        spinner = ProgressDialog.show(context, headerMessage, message, true);
    }

    /*
       Display spinner on context page
       @param context to display spinner
   */
    public static void ShowSpinner(Context context) {
        ShowSpinner(context, headerMessage, message);
    }

    /*
        Remove spinner from page
    */
    public static void CloseSpinner() {
        if(spinner != null) {
            spinner.dismiss();
        }
    }

    /*
        Show alert dialog message
    */
    public static void Alert(Context context, String message, String positiveButton) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(message)
                .setPositiveButton(positiveButton, null)
                .create()
                .show();
    }

    /*
        Append on log
    */
    public static void Log(String tag, String message) {
        if (Config.Debug) {
            Log.d(tag, message);
        }
    }

}