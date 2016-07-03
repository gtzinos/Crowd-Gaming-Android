package geotzinos.crowdgaming.General;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by George on 2016-07-03.
 */
public class Calculation {

    public static int getIntJsonValue(JSONObject jsonObject, String parameterName) {
        return (int) getJsonValue(jsonObject, parameterName);
    }

    public static String getStringJsonValue(JSONObject jsonObject, String parameterName) {
        return getJsonValue(jsonObject, parameterName) != null ? String.valueOf(getJsonValue(jsonObject, parameterName)) : null;
    }

    public static double getDoubleJsonValue(JSONObject jsonObject, String parameterName) {
        return (double) getJsonValue(jsonObject, parameterName);
    }

    public static long getLongJsonValue(JSONObject jsonObject, String parameterName) {
        return (long) getJsonValue(jsonObject, parameterName);
    }

    private static Object getJsonValue(JSONObject jsonObject, String parameterName) {
        Object value = null;
        try {
            value = ((jsonObject.has(parameterName) && !jsonObject.isNull(parameterName))) ? jsonObject.get(parameterName) : null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }
}
