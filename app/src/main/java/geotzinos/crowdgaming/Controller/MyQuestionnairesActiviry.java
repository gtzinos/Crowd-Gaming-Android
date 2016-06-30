package geotzinos.crowdgaming.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import geotzinos.crowdgaming.Controller.Adapter.MyQuestionnairesAdapter;
import geotzinos.crowdgaming.Controller.Request.MyQuestionnairesPageRequest;
import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.Model.Domain.Questionnaire;
import geotzinos.crowdgaming.Model.Domain.User;
import geotzinos.crowdgaming.R;

public class MyQuestionnairesActiviry extends AppCompatActivity {
    /* Variables */
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_questionnaires_view);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");
        Effect.Alert(this, "Welcome " + user.getName() + " " + user.getSurname(), "Okay");
        JsonObjectRequest request = new MyQuestionnairesPageRequest().GetQuestionnaires(this, user);
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.add(request);

        ArrayList<Questionnaire> questionnaires = null;
        boolean found = false;

        while (!found) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                if (extras.containsKey("questionnaires")) {
                    questionnaires = (ArrayList<Questionnaire>) intent.getSerializableExtra("questionnaires");
                    found = true;
                    break;
                } else {
                    // Execute some code after 100 ms have passed
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Effect.Log("Class MyQuestionnairesActivity", "Wait for request.");
                        }
                    }, 100);
                }
            }
        }
        listView = (ListView) findViewById(R.id.MyQuestionnairesListView);
        listView.setAdapter(new MyQuestionnairesAdapter(this, questionnaires));


    }
}
