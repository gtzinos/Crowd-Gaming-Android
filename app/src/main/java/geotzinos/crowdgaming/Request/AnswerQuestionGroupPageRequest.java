package geotzinos.crowdgaming.Request;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import geotzinos.crowdgaming.Controller.LoginPageActivity;
import geotzinos.crowdgaming.Controller.PlayQuestionnaireActivity;
import geotzinos.crowdgaming.General.Calculation;
import geotzinos.crowdgaming.General.Config;
import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.General.ErrorParser;
import geotzinos.crowdgaming.Model.Domain.Answer;
import geotzinos.crowdgaming.Model.Domain.Question;
import geotzinos.crowdgaming.Model.Domain.Questionnaire;
import geotzinos.crowdgaming.Model.Domain.User;
import geotzinos.crowdgaming.R;

/**
 * Created by George on 2016-07-03.
 */
public class AnswerQuestionGroupPageRequest {
    public JsonObjectRequest ConfirmAnswer(final Context context, final int index, final Question question, final User user,
                                           final Location location, final Questionnaire questionnaire, final long group_id, final Button answerButton) {
        final String URL = Config.WEB_ROOT + "/rest_api/answer";
        Effect.ShowSpinner(context);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("question-id", question.getId() + "");
        params.put("answer-id", question.getAnswersList().get(index).getId() + "");

        JsonObjectRequest request = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(answerButton != null)
                            {
                                answerButton.setFocusable(true);
                                answerButton.setClickable(true);
                                answerButton.setActivated(true);
                            }
                            int code = response.getInt("code");
                            Effect.CloseSpinner();
                            Effect.Log("Class AnswerQuestionGroupPageRequest", "Answer question completed.");
                            if (code == 200) {
                                JsonObjectRequest request = GetNextQuestion(context,user,questionnaire,question,group_id,location);
                                RequestQueue mRequestQueue = Volley.newRequestQueue(context);
                                mRequestQueue.add(request);
                            }
                            else if(code == 201)
                            {
                                try {
                                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                    alert.setMessage("Question group completed.")
                                            .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //Send request to get groups
                                                    JsonObjectRequest request = new MyQuestionnairesPageRequest().GetQuestionGroups(context, user, questionnaire,null);
                                                    RequestQueue mRequestQueue = Volley.newRequestQueue(context);
                                                    mRequestQueue.add(request);
                                                }
                                            })
                                            .create()
                                            .show();
                                } catch (Exception e) {
                                    Effect.Log("Class AnswerQuestionGroupPageRequest", e.getMessage());
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(answerButton != null)
                {
                    answerButton.setFocusable(true);
                    answerButton.setClickable(true);
                    answerButton.setActivated(true);
                }
                Effect.CloseSpinner();
                try {
                    final String errorMessage = ErrorParser.ResponseError(error);

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage(errorMessage)
                            .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(errorMessage.equals("Please check your internet connection."))
                                    {
                                        File dir = context.getFilesDir();
                                        File file = new File(dir, "user.txt");
                                        if(file.exists())
                                        {
                                            file.delete();
                                        }
                                        Intent intent = new Intent(context, LoginPageActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                        ((Activity)context).startActivity(intent);
                                        ((Activity)context).finish();
                                        return;
                                    }

                                    //Send request to get groups
                                    JsonObjectRequest request = new MyQuestionnairesPageRequest().GetQuestionGroups(context, user, questionnaire,null);
                                    RequestQueue mRequestQueue = Volley.newRequestQueue(context);
                                    mRequestQueue.add(request);
                                }
                            })
                            .create()
                            .show();
                } catch (Exception e) {
                    Effect.Log("Class AnswerQuestionGroupPageRequest", e.getMessage());
                }
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
                if (location != null) {
                    headers.put("X-Coordinates", String.valueOf(location.getLatitude() + ";" + location.getLongitude()));
                }
                return headers;
            }
        };
        return request;
    }

    CountDownTimer timer;
    private JsonObjectRequest GetNextQuestion(final Context context, final User user, final Questionnaire questionnaire,
                                             final Question question, final long group_id, final Location location) {
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
                            Effect.Log("Class AnswerQuestionGroupPageRequest","Get next question completed");
                            if (code == 200) {
                                JSONObject questionJObject = response.getJSONObject("question");
                                long id = Calculation.getLongJsonValue(questionJObject, "id");
                                String name = Calculation.getStringJsonValue(questionJObject, "question-text");
                                double multiplier = Calculation.getDoubleJsonValue(questionJObject, "multiplier");
                                String creation_date = Calculation.getStringJsonValue(questionJObject, "creation_date");
                                double time_to_answer = Calculation.getDoubleJsonValue(questionJObject, "time-to-answer");
                                final Question question = new Question(id, name, multiplier, creation_date,time_to_answer);

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

                                ((RadioButton)((Activity)context).findViewById(R.id.answer1_radio_button)).setSelected(false);
                                ((RadioButton)((Activity)context).findViewById(R.id.answer2_radio_button)).setSelected(false);
                                ((RadioButton)((Activity)context).findViewById(R.id.answer3_radio_button)).setSelected(false);
                                ((RadioButton)((Activity)context).findViewById(R.id.answer4_radio_button)).setSelected(false);
                                ((RadioGroup)((Activity)context).findViewById(R.id.answers_radio_group)).clearCheck();

                                ((TextView)((Activity)context).findViewById(R.id.QuestionNameTextView)).setText(question.getText());

                                if(question.getAnswersList().size() == 4)
                                {
                                    ((RadioButton)((Activity)context).findViewById(R.id.answer1_radio_button)).setText(question.getAnswersList().get(0).getText());
                                    ((RadioButton)((Activity)context).findViewById(R.id.answer2_radio_button)).setText(question.getAnswersList().get(1).getText());
                                    ((RadioButton)((Activity)context).findViewById(R.id.answer3_radio_button)).setText(question.getAnswersList().get(2).getText());
                                    ((RadioButton)((Activity)context).findViewById(R.id.answer4_radio_button)).setText(question.getAnswersList().get(3).getText());
                                }
                                else if(question.getAnswersList().size() == 3)
                                {
                                    ((RadioButton)((Activity)context).findViewById(R.id.answer1_radio_button)).setText(question.getAnswersList().get(0).getText());
                                    ((RadioButton)((Activity)context).findViewById(R.id.answer2_radio_button)).setText(question.getAnswersList().get(1).getText());
                                    ((RadioButton)((Activity)context).findViewById(R.id.answer3_radio_button)).setText(question.getAnswersList().get(2).getText());
                                }
                                else if(question.getAnswersList().size() == 2){
                                    ((RadioButton)((Activity)context).findViewById(R.id.answer1_radio_button)).setText(question.getAnswersList().get(0).getText());
                                    ((RadioButton)((Activity)context).findViewById(R.id.answer2_radio_button)).setText(question.getAnswersList().get(1).getText());
                                }


                                //Timer
                                if (question.getTimeToAnswer() == -1) {
                                    ((TextView)((Activity)context).findViewById(R.id.QuestionTimeTextView)).setText(String.valueOf("Full time to answer."));
                                }
                                else {
                                    final long milliseconds = (long) question.getTimeToAnswer() * (long) 1000;
                                    timer = new CountDownTimer(milliseconds, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            ((TextView) ((Activity) context).findViewById(R.id.QuestionTimeTextView)).setText(Html.fromHtml("<div><font color='#d9534f'>" + String.valueOf(String.format(Locale.getDefault(),
                                                    "%02d:%02d:%02d",
                                                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                                                            - TimeUnit.HOURS
                                                            .toMinutes(TimeUnit.MILLISECONDS
                                                                    .toHours(millisUntilFinished)),
                                                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                                                            - TimeUnit.MINUTES
                                                            .toSeconds(TimeUnit.MILLISECONDS
                                                                    .toMinutes(millisUntilFinished))) + "</font></div>")));
                                        }

                                        @Override
                                        public void onFinish() {
                                            if (context == null) {
                                                return;
                                            }

                                            try {
                                                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                                alert.setMessage("Question time expired.")
                                                        .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Intent intent = new Intent(context, PlayQuestionnaireActivity.class);
                                                                intent.putExtra("user", user);
                                                                intent.putExtra("questionnaire", questionnaire);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                                                context.startActivity(intent);
                                                                ((Activity) context).finish();
                                                            }
                                                        })
                                                        .create()
                                                        .show();
                                            } catch (Exception e) {
                                                Effect.Log("AnswerQuestionGroupActivity", e.getMessage());
                                            }
                                        }
                                    }.start();
                                }

                                ((Button)((Activity)context).findViewById(R.id.confirm_answer_button)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int radioButtonID = ((RadioGroup)((Activity)context).findViewById(R.id.answers_radio_group)).getCheckedRadioButtonId();

                                        if(((Button)((Activity)context).findViewById(R.id.confirm_answer_button)) != null)
                                        {
                                            ((Button)((Activity)context).findViewById(R.id.confirm_answer_button)).setFocusable(false);
                                            ((Button)((Activity)context).findViewById(R.id.confirm_answer_button)).setClickable(false);
                                            ((Button)((Activity)context).findViewById(R.id.confirm_answer_button)).setActivated(false);
                                        }

                                        if(radioButtonID == -1)
                                        {
                                            if(((Button)((Activity)context).findViewById(R.id.confirm_answer_button)) != null)
                                            {
                                                ((Button)((Activity)context).findViewById(R.id.confirm_answer_button)).setFocusable(true);
                                                ((Button)((Activity)context).findViewById(R.id.confirm_answer_button)).setClickable(true);
                                                ((Button)((Activity)context).findViewById(R.id.confirm_answer_button)).setActivated(true);
                                            }
                                            Effect.Alert(context,"Please select an answer.","Got it");
                                        }
                                        else {
                                            View radioButton = ((RadioGroup) ((Activity) context).findViewById(R.id.answers_radio_group)).findViewById(radioButtonID);
                                            int index = ((RadioGroup) ((Activity) context).findViewById(R.id.answers_radio_group)).indexOfChild(radioButton);

                                            if(timer != null){
                                                timer.cancel();
                                            }
                                            JsonObjectRequest request = new AnswerQuestionGroupPageRequest().ConfirmAnswer(context, index, question, user, location
                                                    , questionnaire, group_id, ((Button)((Activity)context).findViewById(R.id.confirm_answer_button)));
                                            RequestQueue mRequestQueue = Volley.newRequestQueue(context);
                                            mRequestQueue.add(request);
                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Effect.CloseSpinner();
                final String errorMessage = ErrorParser.ResponseError(error);
                //Back to question groups
                try {
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setMessage(errorMessage)
                                .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(errorMessage.equals("Please check your internet connection."))
                                        {
                                            File dir = context.getFilesDir();
                                            File file = new File(dir, "user.txt");
                                            if(file.exists())
                                            {
                                                file.delete();
                                            }
                                            Intent intent = new Intent(context, LoginPageActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                            ((Activity)context).startActivity(intent);
                                            ((Activity)context).finish();
                                            return;
                                        }

                                        //Send request to get groups
                                        JsonObjectRequest request = new MyQuestionnairesPageRequest().GetQuestionGroups(context, user, questionnaire,null);
                                        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
                                        mRequestQueue.add(request);
                                    }
                                })
                                .create()
                                .show();
                } catch (Exception e) {
                    Effect.Log("Class AnswerQuestionGroupPageRequest", e.getMessage());
                }
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
                if (location != null) {
                    headers.put("X-Coordinates", String.valueOf(location.getLatitude() + ";" + location.getLongitude()));
                }
                return headers;
            }
        };

        return request;
    }
}
