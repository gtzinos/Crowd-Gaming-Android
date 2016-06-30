package geotzinos.crowdgaming.Controller.Request;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import geotzinos.crowdgaming.Controller.MyQuestionnairesActiviry;
import geotzinos.crowdgaming.General.Config;
import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.Model.Domain.User;

/**
 * Created by George on 2016-05-29.
 */
public class LoginPageRequest {

    /*
       Login on platform. Get user personal key.
    */
    public JsonObjectRequest Login(final Context context, String email, String password) {
        final String URL = Config.WEB_ROOT + "/rest_api/authenticate";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);

        JsonObjectRequest request = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int code = response.getInt("code");

                            if (code == 200) {
                                Effect.CloseSpinner();

                                JSONObject userJSON = response.getJSONObject("user");

                                String name = userJSON.getString("name");
                                String surname = userJSON.getString("surname");
                                String api_token = userJSON.getString("api-token");

                                User user = new User(name, surname, api_token);
                                Effect.CloseSpinner();
                                Intent intent = new Intent(context, MyQuestionnairesActiviry.class);
                                intent.putExtra("user", user);

                                context.startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Effect.Log("Class LoginPageRequest", error.getMessage());
                Effect.CloseSpinner();
                Effect.Alert(context, "Wrong username or password.", "Okay");
            }
        });
        return request;
    }
}
