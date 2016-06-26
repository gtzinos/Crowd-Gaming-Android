package geotzinos.crowdgaming.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.Model.User;
import geotzinos.crowdgaming.R;
import geotzinos.crowdgaming.Request.MyQuestionnairesPageRequest;

public class MyQuestionnairesActiviry extends AppCompatActivity {

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
    }
}
