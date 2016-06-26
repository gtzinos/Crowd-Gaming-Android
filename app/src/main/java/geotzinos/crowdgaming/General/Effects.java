package geotzinos.crowdgaming.General;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by George on 2016-06-26.
 */
public class Effects {

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
        spinner.dismiss();
    }
}