package geotzinos.crowdgaming.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;

import geotzinos.crowdgaming.Model.Domain.User;
import geotzinos.crowdgaming.R;
import geotzinos.crowdgaming.Request.MyQuestionnairesPageRequest;

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
        listView = (ListView) findViewById(R.id.MyQuestionnairesListView);
        JsonObjectRequest request = new MyQuestionnairesPageRequest().GetQuestionnaires(this, user, listView);
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.logged_in_action_bar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Context context = this;

        switch(item.getItemId())
        {
            case R.id.user_sign_out:
                AlertDialog.Builder signout_alert = new AlertDialog.Builder(this);
                signout_alert.setMessage("Do you really want to log out ?");
                signout_alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File dir = getFilesDir();
                                File file = new File(dir, "user.txt");
                                if(file.exists())
                                {
                                    file.delete();
                                }

                                Intent intent = new Intent(context,LoginPageActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        });
                signout_alert.setNegativeButton("Cancel",null);
                signout_alert.show();
                break;
            case R.id.user_close:
                AlertDialog.Builder exit_alert = new AlertDialog.Builder(this);
                exit_alert.setMessage("Do you really want to close this application ?");
                exit_alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity)context).finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                });
                exit_alert.setNegativeButton("Cancel",null);
                exit_alert.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
