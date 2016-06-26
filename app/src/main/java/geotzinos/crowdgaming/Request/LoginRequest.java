package geotzinos.crowdgaming.Request;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by George on 2016-05-29.
 */
public class LoginRequest extends JsonObjectRequest {
    private static final String LOGIN_URL = "http://83.212.118.212/Treasure-Thess-Website/public/rest_api/authenticate";

    //private JSONObject credentialsData;
    public LoginRequest(JSONObject body, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super(Method.POST, LOGIN_URL, body, responseListener, errorListener);
    }
}
