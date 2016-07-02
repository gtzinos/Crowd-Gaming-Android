package geotzinos.crowdgaming.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import geotzinos.crowdgaming.Model.Domain.Questionnaire;
import geotzinos.crowdgaming.R;

public class PlayQuestionnaireActivity extends AppCompatActivity {
    ListView listView;
    TextView timeLeftTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_questionnaire_view);

        //Element initialization
        listView = (ListView) findViewById(R.id.GroupsListView);
        timeLeftTextView = (TextView) findViewById(R.id.GroupTimeLeftTextView);
        //Get extra values
        Intent intent = getIntent();
        Questionnaire questionnaire = (Questionnaire) intent.getSerializableExtra("questionnaire");
        //Send request to get groups
        // JsonObjectRequest request = new MyQuestionnairesPageRequest().GetQuestionnaires(this, questionnaire, listView);
        // RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        // mRequestQueue.add(request);
    }
}
