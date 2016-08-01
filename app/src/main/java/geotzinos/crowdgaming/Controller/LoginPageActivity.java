package geotzinos.crowdgaming.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
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
import java.io.OutputStreamWriter;

import geotzinos.crowdgaming.Controller.Request.LoginPageRequest;
import geotzinos.crowdgaming.General.Config;
import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.General.Validate;
import geotzinos.crowdgaming.Model.Domain.User;
import geotzinos.crowdgaming.R;

public class LoginPageActivity extends AppCompatActivity {
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

                        startActivity(intent);
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
        //Store credentials
        String emailText = etEmail.getText().toString();
        String passwordText = etPassword.getText().toString();

        int validationResults = Validate.LoginCredentials(emailText, passwordText);

        if (validationResults == -1) {
            Effect.Alert(LoginPageActivity.this, "Login failed. Fill a valid email address.", "Okay");
            return;
        } else if (validationResults == -2) {
            Effect.Alert(LoginPageActivity.this, "Login failed. Fill a valid password.", "Okay");
            return;
        }

        //Loader
        Effect.ShowSpinner(this, "Loading", "Please wait while trying to login.");

        JsonObjectRequest loginRequest = new LoginPageRequest().Login(this, emailText, passwordText);
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.add(loginRequest);
    }
}
