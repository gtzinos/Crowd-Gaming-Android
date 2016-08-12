package geotzinos.crowdgaming.Controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import geotzinos.crowdgaming.General.Config;
import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.General.Validate;
import geotzinos.crowdgaming.Model.Domain.User;
import geotzinos.crowdgaming.R;
import geotzinos.crowdgaming.Request.LoginPageRequest;

public class LoginPageActivity extends BaseController {
    //UI Elements
    private EditText etEmail;
    private EditText etPassword;
    private Button loginButton;
    private TextView tvRegisterLink;
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page_view);

        CheckInternetPermissions();
        CheckAccessFineLocationPermissions();
        CheckAccessCoarseLocationPermissions();

        User user = new User();
        try {
            InputStream inputStream = openFileInput("user.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                if(stringBuilder.length() > 0) {
                    String data[] = stringBuilder.toString().split("###");

                    user.setName(data[0].replace("name=",""));
                    user.setSurname(data[1].replace("surname=",""));
                    user.setApiTaken(data[2].replace("token=",""));
                    if(user.getApiTaken() != null)
                    {
                        Intent intent = new Intent(this, MyQuestionnairesActiviry.class);
                        intent.putExtra("user", user);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }
        catch (FileNotFoundException e) {
            Effect.Log("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Effect.Log("login activity", "Can not read file: " + e.toString());
        }

        //Initialize refferences
        etEmail = (EditText) findViewById(R.id.emailTextField);
        etPassword = (EditText) findViewById(R.id.passwordTextField);
        loginButton = (Button) findViewById(R.id.login_button);
        tvRegisterLink = (TextView) findViewById(R.id.RegisterLinkTextView);

        tvRegisterLink.setText(Html.fromHtml(String.valueOf("<a href=\"" + Config.WEB_ROOT + "\">Click here to register</a>")));
        tvRegisterLink.setMovementMethod(LinkMovementMethod.getInstance());

    }

    /**
     * All onclick events
     *
     * @param v
     */
    public void ClickEventManager(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                Login();
                break;
        }
    }

    /*
        Login using api
    */
    public void Login() {
        loginButton.setEnabled(false);
        //Store credentials
        String emailText = etEmail.getText().toString();
        String passwordText = etPassword.getText().toString();

        int validationResults = Validate.LoginCredentials(emailText, passwordText);

        if (validationResults == -1) {
            loginButton.setEnabled(true);
            Effect.Alert(LoginPageActivity.this, "Login failed. Fill a valid email address.", "Okay");
            return;
        } else if (validationResults == -2) {
            loginButton.setEnabled(true);
            Effect.Alert(LoginPageActivity.this, "Login failed. Fill a valid password.", "Okay");
            return;
        }

        //Loader
        Effect.ShowSpinner(this, "Loading", "Please wait while trying to login.");

        JsonObjectRequest loginRequest = new LoginPageRequest().Login(this, emailText, passwordText, loginButton);
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.add(loginRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.guest_action_bar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Context context = this;

        switch(item.getItemId())
        {
            case R.id.guest_close:
                AskToCloseApplication(context);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
