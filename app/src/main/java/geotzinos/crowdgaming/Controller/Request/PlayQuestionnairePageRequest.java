package geotzinos.crowdgaming.Controller.Request;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import geotzinos.crowdgaming.Controller.AnswerQuestionGroupActivity;
import geotzinos.crowdgaming.General.Calculation;
import geotzinos.crowdgaming.General.Config;
import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.Model.Domain.Answer;
import geotzinos.crowdgaming.Model.Domain.Question;
import geotzinos.crowdgaming.Model.Domain.Questionnaire;
import geotzinos.crowdgaming.Model.Domain.User;

/**
 * Created by George on 2016-07-02.
 */
public class PlayQuestionnairePageRequest {

    public JsonObjectRequest ResetQuestionGroup(final Context context, final long questionnaire_id, final long group_id,
                                                final long answered, final long total, final TextView answersTextView,
                                                final Button resetButton, final Questionnaire questionnaire, final User user) {
        final String URL = Config.WEB_ROOT + "/rest_api/questionnaire/" + questionnaire_id + "/group/" + group_id + "/reset";
        Effect.ShowSpinner(context);
        JsonObjectRequest request = new JsonObjectRequest(URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int code = response.getInt("code");

                            if (code == 200) {
                                Effect.CloseSpinner();
                                String message = response.getString("message");
                                answersTextView.setText(String.valueOf("Answered: " + answered + "/" + total));
                                if (answered == total) {
                                    resetButton.setEnabled(false);
                                }

                                try {
                                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                    alert.setMessage("Question group reset completed.")
                                            .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    JsonObjectRequest request = new MyQuestionnairesPageRequest().GetQuestionGroups(context, user, questionnaire);
                                                    RequestQueue mRequestQueue = Volley.newRequestQueue(context);
                                                    mRequestQueue.add(request);
                                                }
                                            })
                                            .create()
                                            .show();
                                } catch (Exception e) {
                                    Effect.Log("Class PlayQuestionnairePageRequest", e.getMessage());
                                }

                                Effect.Log("Class PlayQuestionnairePageRequest", "Get question groups request completed.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Effect.CloseSpinner();
                Effect.Alert(context, "You can't reset this questionnaire.", "Got it");
            }

        }) {
            //In your extended request class
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError){
                if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    volleyError = error;
                }

                return volleyError;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                User user = (User) ((Activity) context).getIntent().getSerializableExtra("user");
                headers.put("Authorization", user.getApiTaken());

                return headers;
            }
        };

        return request;
    }

    public JsonObjectRequest GetNextQuestion(final Context context, final User user, final Questionnaire questionnaire, final long group_id, final Location location) {
        final long questionnaire_id = questionnaire.getId();
        final String URL = Config.WEB_ROOT + "/rest_api/questionnaire/" + questionnaire_id + "/group/" + group_id + "/question";

        Effect.ShowSpinner(context);
        JsonObjectRequest request = new JsonObjectRequest(URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int code = response.getInt("code");
                            Effect.CloseSpinner();
                            if (code == 200) {
                                JSONObject questionJObject = response.getJSONObject("question");
                                long id = Calculation.getLongJsonValue(questionJObject, "id");
                                String name = Calculation.getStringJsonValue(questionJObject, "question-text");
                                double multiplier = Calculation.getDoubleJsonValue(questionJObject, "multiplier");
                                String creation_date = Calculation.getStringJsonValue(questionJObject, "creation_date");
                                double time_to_answer = Calculation.getDoubleJsonValue(questionJObject, "time-to-answer");
                                Question question = new Question(id, name, multiplier, creation_date,time_to_answer);

                                JSONArray answerJArray = response.getJSONArray("answer");
                                ArrayList<Answer> answersArrayList = new ArrayList<Answer>();
                                for (int i = 0; i < answerJArray.length(); i++) {
                                    JSONObject answerJObject = answerJArray.getJSONObject(i);
                                    long answer_id = Calculation.getLongJsonValue(answerJObject, "id");
                                    String answer_name = Calculation.getStringJsonValue(answerJObject, "answer-text");
                                    String answer_creation_date = Calculation.getStringJsonValue(answerJObject, "creation_date");
                                    Answer answer = new Answer(answer_id, answer_name, answer_creation_date);
                                    question.getAnswersList().add(answer);
                                }
                                Intent intent = new Intent(context, AnswerQuestionGroupActivity.class);
                                intent.putExtra("user", user);
                                intent.putExtra("question", question);
                                intent.putExtra("questionnaire", questionnaire);
                                intent.putExtra("group_id",group_id);
                                context.startActivity(intent);
                                ((Activity) context).finish();
                                Effect.Log("Class PlayQuestionnairePageRequest", "Get question request completed.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Question group completed.")
                            .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    JsonObjectRequest request = new MyQuestionnairesPageRequest().GetQuestionGroups(context, user, questionnaire);
                                    RequestQueue mRequestQueue = Volley.newRequestQueue(context);
                                    mRequestQueue.add(request);
                                }
                            })
                            .create()
                            .show();
                } catch (Exception e) {
                    Effect.Log("Class PlayQuestionnairePageRequest", e.getMessage());
                }
                Effect.CloseSpinner();
            }

        }) {
            //In your extended request class
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError){
                if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    volleyError = error;
                }

                return volleyError;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", user.getApiTaken());
                if (location != null) {
                    headers.put("X-Coordinates", String.valueOf(location.getLatitude() + ";" + location.getLongitude()));
                }
                return headers;
            }
        };

        return request;
    }
}
