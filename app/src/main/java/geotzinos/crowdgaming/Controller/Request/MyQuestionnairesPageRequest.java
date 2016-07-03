package geotzinos.crowdgaming.Controller.Request;

import android.content.Context;
import android.content.Intent;
import android.widget.ListView;

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

import geotzinos.crowdgaming.Controller.Adapter.MyQuestionnairesAdapter;
import geotzinos.crowdgaming.Controller.PlayQuestionnaireActivity;
import geotzinos.crowdgaming.General.Config;
import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.Model.Domain.QuestionGroup;
import geotzinos.crowdgaming.Model.Domain.Questionnaire;
import geotzinos.crowdgaming.Model.Domain.User;

/**
 * Created by George on 2016-06-26.
 */
public class MyQuestionnairesPageRequest {

    public JsonObjectRequest GetQuestionnaires(final Context context, final User user, final ListView listView) {
        final String URL = Config.WEB_ROOT + "/rest_api/questionnaire/";

        JsonObjectRequest request = new JsonObjectRequest(URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int code = response.getInt("code");

                            if (code == 200) {
                                JSONArray questionnairesJArray = response.getJSONArray("questionnaire");
                                ArrayList<Questionnaire> questionnaireArrayList = new ArrayList<Questionnaire>();
                                for (int i = 0; i < questionnairesJArray.length(); i++) {
                                    JSONObject questionnaireJObject = questionnairesJArray.getJSONObject(i);
                                    int id = questionnaireJObject.getInt("id");
                                    String name = questionnaireJObject.getString("name");
                                    String description = questionnaireJObject.getString("description");
                                    String creation_date = questionnaireJObject.getString("creation-date");
                                    int time_left = questionnaireJObject.getInt("time-left");
                                    int time_left_to_end = questionnaireJObject.getInt("time-left-to-end");
                                    int total_questions = questionnaireJObject.getInt("total-questions");
                                    int answered_questions = questionnaireJObject.getInt("answered-questions");
                                    int allow_multiple_groups_play_through = questionnaireJObject.getInt("allow-multiple-groups-playthrough");
                                    String is_completed = questionnaireJObject.getString("is-completed");

                                    Questionnaire questionnaire = new Questionnaire(id,name, description, creation_date, time_left, time_left_to_end, total_questions, answered_questions, allow_multiple_groups_play_through, is_completed);
                                    questionnaireArrayList.add(questionnaire);
                                }
                                listView.setAdapter(new MyQuestionnairesAdapter(context, questionnaireArrayList));
                                Effect.Log("Class MyQuestionnairesPageRequest", "Get questionnaires request completed.");
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

    public JsonObjectRequest GetQuestionGroups(final Context context, final User user, final Questionnaire questionnaire) {
        final String URL = Config.WEB_ROOT + "/rest_api/questionnaire/" + questionnaire.getId() + "/group";
        questionnaire.getQuestionGroupsList().clear();

        JsonObjectRequest request = new JsonObjectRequest(URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int code = response.getInt("code");

                            if (code == 200) {
                                JSONArray groupsJArray = response.getJSONArray("question-group");
                                for (int i = 0; i < groupsJArray.length(); i++) {
                                    JSONObject questionGroupJObject = groupsJArray.getJSONObject(i);

                                    long id = questionGroupJObject.getLong("id");
                                    String name = questionGroupJObject.getString("name");
                                    String latitude = questionGroupJObject.getString("latitude");
                                    String longitude = questionGroupJObject.getString("longitude");
                                    String radius = questionGroupJObject.getString("radius");
                                    String creation_date = questionGroupJObject.getString("creation_date");
                                    long total_questions = questionGroupJObject.getLong("total-questions");
                                    long answered_questions = questionGroupJObject.getLong("answered-questions");
                                    long allowed_repeats = questionGroupJObject.getLong("allowed-repeats");
                                    long current_repeats = questionGroupJObject.getLong("current-repeats");
                                    String time_left = questionGroupJObject.getString("time-left");
                                    String time_to_complete = questionGroupJObject.getString("time-to-complete");
                                    long priority = questionGroupJObject.getLong("priority");
                                    String is_completed = questionGroupJObject.getString("is-completed");

                                    QuestionGroup questionGroup = new QuestionGroup(id, name, latitude, longitude, radius, creation_date, total_questions, answered_questions, allowed_repeats, current_repeats, time_left, time_to_complete, priority, is_completed);
                                    questionnaire.getQuestionGroupsList().add(questionGroup);
                                }

                                Intent intent = new Intent(context, PlayQuestionnaireActivity.class);
                                intent.putExtra("questionnaire", questionnaire);
                                intent.putExtra("user", user);
                                context.startActivity(intent);
                                Effect.Log("Class MyQuestionnairesPageRequest", "Get question groups request completed.");
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
                Effect.Alert(context, "You can't play this questionnaire.", "Okay");
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
