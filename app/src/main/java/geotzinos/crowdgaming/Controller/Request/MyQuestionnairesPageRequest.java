package geotzinos.crowdgaming.Controller.Request;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import geotzinos.crowdgaming.Controller.Adapter.MyQuestionnairesAdapter;
import geotzinos.crowdgaming.Controller.LoginPageActivity;
import geotzinos.crowdgaming.Controller.PlayQuestionnaireActivity;
import geotzinos.crowdgaming.General.Calculation;
import geotzinos.crowdgaming.General.Config;
import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.General.ErrorParser;
import geotzinos.crowdgaming.Model.Domain.QuestionGroup;
import geotzinos.crowdgaming.Model.Domain.Questionnaire;
import geotzinos.crowdgaming.Model.Domain.User;
import geotzinos.crowdgaming.R;

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
                                    int id = Calculation.getIntJsonValue(questionnaireJObject, "id");
                                    String name = Calculation.getStringJsonValue(questionnaireJObject, "name");
                                    String description = Calculation.getStringJsonValue(questionnaireJObject, "description");
                                    String creation_date = Calculation.getStringJsonValue(questionnaireJObject, "creation-date");
                                    int time_left = Calculation.getIntJsonValue(questionnaireJObject, "time-left");
                                    int time_left_to_end = Calculation.getIntJsonValue(questionnaireJObject, "time-left-to-end");
                                    int total_questions = Calculation.getIntJsonValue(questionnaireJObject, "total-questions");
                                    int answered_questions = Calculation.getIntJsonValue(questionnaireJObject, "answered-questions");
                                    int allow_multiple_groups_play_through = Calculation.getIntJsonValue(questionnaireJObject, "allow-multiple-groups-playthrough");
                                    boolean is_completed = Calculation.getBooleanJsonValue(questionnaireJObject, "is-completed");

                                    Questionnaire questionnaire = new Questionnaire(id,name, description, creation_date, time_left, time_left_to_end, total_questions, answered_questions, allow_multiple_groups_play_through, is_completed);
                                    questionnaireArrayList.add(questionnaire);
                                }
                                if(questionnaireArrayList.size() == 0)
                                {
                                    ((Activity) context).setContentView(R.layout.empty_my_questionnaires_view);
                                }
                                else
                                {
                                    listView.setAdapter(new MyQuestionnairesAdapter(context, questionnaireArrayList));
                                }

                                Effect.Log("Class MyQuestionnairesPageRequest", "Get questionnaires request completed.");
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
            }

        }) {
            //In your extended request class
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError){
                return ErrorParser.NetworkErrors(volleyError);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", user.getApiTaken());

                return headers;
            }
        };

        return request;
    }

    public JsonObjectRequest GetQuestionGroups(final Context context, final User user, final Questionnaire questionnaire,final Button playQuestionnaireButton) {
        final String URL = Config.WEB_ROOT + "/rest_api/questionnaire/" + questionnaire.getId() + "/group";
        questionnaire.getQuestionGroupsList().clear();

        JsonObjectRequest request = new JsonObjectRequest(URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(playQuestionnaireButton != null)
                            {
                                playQuestionnaireButton.setFocusable(true);
                                playQuestionnaireButton.setClickable(true);
                                playQuestionnaireButton.setActivated(true);
                            }

                            int code = response.getInt("code");

                            if (code == 200) {
                                JSONArray groupsJArray = response.getJSONArray("question-group");
                                for (int i = 0; i < groupsJArray.length(); i++) {
                                    JSONObject questionGroupJObject = groupsJArray.getJSONObject(i);

                                    long id = Calculation.getLongJsonValue(questionGroupJObject, "id");
                                    String name = Calculation.getStringJsonValue(questionGroupJObject, "name");
                                    String latitude = Calculation.getStringJsonValue(questionGroupJObject, "latitude");
                                    String longitude = Calculation.getStringJsonValue(questionGroupJObject, "longitude");
                                    String radius = Calculation.getStringJsonValue(questionGroupJObject, "radius");
                                    String creation_date = Calculation.getStringJsonValue(questionGroupJObject, "creation_date");
                                    long total_questions = Calculation.getLongJsonValue(questionGroupJObject, "total-questions");
                                    long answered_questions = Calculation.getLongJsonValue(questionGroupJObject, "answered-questions");
                                    long allowed_repeats = Calculation.getLongJsonValue(questionGroupJObject, "allowed-repeats");
                                    long current_repeats = Calculation.getLongJsonValue(questionGroupJObject, "current-repeats");
                                    String time_left = Calculation.getStringJsonValue(questionGroupJObject, "time-left");
                                    String time_to_complete = Calculation.getStringJsonValue(questionGroupJObject, "time-to-complete");
                                    long priority = Calculation.getLongJsonValue(questionGroupJObject, "priority");
                                    boolean is_completed = Calculation.getBooleanJsonValue(questionGroupJObject, "is-completed");

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
                if(playQuestionnaireButton != null)
                {
                    playQuestionnaireButton.setFocusable(true);
                    playQuestionnaireButton.setClickable(true);
                    playQuestionnaireButton.setActivated(true);
                }
                Effect.CloseSpinner();
                Effect.Alert(context,ErrorParser.ResponseError(error),"Got It");
            }
        }) {
            //In your extended request class
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError){
                return ErrorParser.NetworkErrors(volleyError);
            }

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
