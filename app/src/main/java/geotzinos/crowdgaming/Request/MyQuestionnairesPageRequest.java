package geotzinos.crowdgaming.Request;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import geotzinos.crowdgaming.General.Config;
import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.Model.Domain.Questionnaire;
import geotzinos.crowdgaming.Model.Domain.User;

/**
 * Created by George on 2016-06-26.
 */
public class MyQuestionnairesPageRequest {

    public JsonObjectRequest GetQuestionnaires(final Context context, final User user) {
        final String URL = Config.WEB_ROOT + "/rest_api/questionnaire/";

        JsonObjectRequest request = new JsonObjectRequest(URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int code = response.getInt("code");

                            if (code == 200) {
                                Effect.Log("Class MyQuestionnairesPageRequest", "Get questionnaires request completed.");
                                JSONArray questionnairesJArray = response.getJSONArray("questionnaire");
                                ArrayList<Questionnaire> questionnairesList = new ArrayList<Questionnaire>();
                                for (int i = 0; i < questionnairesJArray.length(); i++) {
                                    JSONObject questionnaireJObject = questionnairesJArray.getJSONObject(i);
                                    String name = questionnaireJObject.getString("name");
                                    String description = questionnaireJObject.getString("description");
                                    String creation_date = questionnaireJObject.getString("creation-date");
                                    int time_left = questionnaireJObject.getInt("time-left");
                                    int time_left_to_end = questionnaireJObject.getInt("time-left-to-end");
                                    int total_questions = questionnaireJObject.getInt("total-questions");
                                    int answered_questions = questionnaireJObject.getInt("answered-questions");
                                    int allow_multiple_groups_playthrough = questionnaireJObject.getInt("allow-multiple-groups-playthrough");

                                    Questionnaire questionnaire = new Questionnaire(name, description, creation_date, time_left, time_left_to_end, total_questions, answered_questions, allow_multiple_groups_playthrough);
                                    questionnairesList.add(i, questionnaire);
                                }
                                //Database database = new Database(context);
                                Intent intent = ((Activity) context).getIntent();
                                intent.putExtra("questionnaires", questionnairesList);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Effect.Log("Class MyQuestionnairesPageRequest", error.getMessage());
                Effect.CloseSpinner();
                Effect.Alert(context, "Wrong username or password.", "Okay");
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", user.getApiTaken());

                return headers;
            }
        };

        return request;
    }

}
