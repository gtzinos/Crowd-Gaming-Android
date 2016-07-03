package geotzinos.crowdgaming.General;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by George on 2016-07-03.
 */
public class Calculation {

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
}
