package geotzinos.crowdgaming.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import geotzinos.crowdgaming.Controller.Adapter.AnswerQuestionGroupAdapter;
import geotzinos.crowdgaming.Model.Domain.Question;
import geotzinos.crowdgaming.Model.Domain.User;
import geotzinos.crowdgaming.R;

public class AnswerQuestionGroupActivity extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_question_group_view);
        listView = (ListView) findViewById(R.id.AnswersListView);

        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");
        Question question = (Question) intent.getSerializableExtra("question");
        listView.setAdapter(new AnswerQuestionGroupAdapter(this, question));

        //JsonObjectRequest request = new MyQuestionnairesPageRequest().GetQuestionnaires(this, user, listView);
        //RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        //mRequestQueue.add(request);
    }
}
