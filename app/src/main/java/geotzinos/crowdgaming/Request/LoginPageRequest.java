package geotzinos.crowdgaming.Request;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.LoginPageController;
import geotzinos.crowdgaming.Model.User;

/**
 * Created by George on 2016-05-29.
 */
public class LoginPageRequest extends JsonObjectRequest {
    private static final String LOGIN_URL = "http://83.212.118.212/Treasure-Thess-Website/public/rest_api/authenticate";

    //private JSONObject credentialsData;
    public LoginPageRequest(JSONObject body, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super(Method.POST, LOGIN_URL, body, responseListener, errorListener);
    }

    public void Login(final Context context, String username, String password) {
        //Send credentials
        Response.Listener<JSONObject> responseLogin = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    int code = response.getInt("code");

                    JSONObject userJSON = response.getJSONObject("user");

                    if (code == 200) {
                        Effect.CloseSpinner();
                        User user = new User("fasd", "asfd", "AFsd", "AFsd");
                        Intent intent = new Intent(context, LoginPageController.class);
                        intent.putExtra("user", user);

                        context.startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        String dataToConvert = "{\"email\":" + username + ",\"password\":" + password + "}";
        try {
            JSONObject loginData = new JSONObject(dataToConvert);
            LoginPageRequest loginRequest = new LoginPageRequest(loginData, responseLogin, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
