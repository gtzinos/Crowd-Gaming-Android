package tzinos.crowdgaming.Request;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import tzinos.crowdgaming.Controller.MyQuestionnairesActiviry;
import tzinos.crowdgaming.General.Calculation;
import tzinos.crowdgaming.General.Config;
import tzinos.crowdgaming.General.Effect;
import tzinos.crowdgaming.General.ErrorParser;
import tzinos.crowdgaming.Model.Domain.User;

/**
 * Created by George on 2016-05-29.
 */
public class LoginPageRequest {

    /*
       Login on platform. Get user personal key.
    */
    public JsonObjectRequest Login(final Context context, String email, String password, final Button LoginButton) {
        final String URL = Config.WEB_ROOT + "/rest_api/authenticate";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);

        final JsonObjectRequest request = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int code = response.getInt("code");

                            if (code == 200) {
                                Effect.CloseSpinner();

                                JSONObject userJSON = response.getJSONObject("user");

                                String name = Calculation.getStringJsonValue(userJSON, "name");
                                String surname = Calculation.getStringJsonValue(userJSON, "surname");
                                String api_token = Calculation.getStringJsonValue(userJSON, "api-token");

                                User user = new User(name, surname, api_token);
                                Effect.CloseSpinner();

                                try {
                                    File dir = context.getFilesDir();
                                    File file = new File(dir, "user.txt");
                                    if(file.exists())
                                    {
                                        file.delete();
                                    }
                                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("user.txt", Context.MODE_PRIVATE));

                                    outputStreamWriter.write("name=" + name + "###surname=" + surname + "###token=" + api_token);
                                    outputStreamWriter.close();
                                }
                                catch (IOException e) {
                                    Effect.Log("Exception", "File write failed: " + e.toString());
                                }

                                //Effect.Alert(context, "Welcome " + user.getName() + " " + user.getSurname(), "Okay");
                                Intent intent = new Intent(context, MyQuestionnairesActiviry.class);
                                intent.putExtra("user", user);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                context.startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Effect.CloseSpinner();
                Effect.Alert(context,ErrorParser.ResponseError(error),"Got It");
                LoginButton.setEnabled(true);
            }
        })
        {

             //In your extended request class
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError){
                return ErrorParser.NetworkErrors(volleyError);
            }
        };
        return request;
    }
}
