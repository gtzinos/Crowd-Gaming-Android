package tzinos.crowdgaming.General;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import tzinos.crowdgaming.Model.Domain.QuestionGroup;

/**
 * Created by George on 2016-07-03.
 */
public class Calculation {

    public static boolean getBooleanJsonValue(JSONObject jsonObject, String parameterName) {
        String string_value = getJsonValue(jsonObject, parameterName);

        if (string_value == null || string_value.equals("0") || string_value.equals("false"))
        {
            return false;
        }
        else {
            return true;
        }
    }

    public static int getIntJsonValue(JSONObject jsonObject, String parameterName) {
        return Integer.parseInt(getJsonValue(jsonObject, parameterName));
    }

    public static String getStringJsonValue(JSONObject jsonObject, String parameterName) {
        return getJsonValue(jsonObject, parameterName) != null ? getJsonValue(jsonObject, parameterName) : null;
    }

    public static double getDoubleJsonValue(JSONObject jsonObject, String parameterName) {
        return Double.parseDouble(getJsonValue(jsonObject, parameterName));
    }

    public static long getLongJsonValue(JSONObject jsonObject, String parameterName) {
        return Long.parseLong(getJsonValue(jsonObject, parameterName));
    }

    private static String getJsonValue(JSONObject jsonObject, String parameterName) {
        String value = null;
        try {
            value = ((jsonObject.has(parameterName) && !jsonObject.isNull(parameterName))) ? jsonObject.getString(parameterName) : null;
        } catch (JSONException e) {
            e.printStackTrace();
            e.printStackTrace();
        }
        return value;
    }

    //calculate client distance from question group
    public static String calculateDistance(QuestionGroup questionGroup, Location location) {
        long R = 6371; // Radius of the earth in km
        double dLat = Math.toRadians(Double.parseDouble(questionGroup.getLatitude()) - location.getLatitude());
        double dLon = Math.toRadians(Double.parseDouble(questionGroup.getLongitude()) - location.getLongitude());
        double lat1 = Math.toRadians(Double.parseDouble(questionGroup.getLatitude()));
        double lat2 = Math.toRadians(location.getLatitude());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;

        //Convert km to meters
        d = d * 1000;

        //Check the final distance
        if (d - Double.parseDouble(questionGroup.getRadius()) >= 0) {
            d = d - Double.parseDouble(questionGroup.getRadius());
        } else {
            d = 0;
        }
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#.##", otherSymbols);
        df.setRoundingMode(RoundingMode.CEILING);

        df.format(d);

        //return value formatted with 2 decimals
        return df.format(d);
    }
}
