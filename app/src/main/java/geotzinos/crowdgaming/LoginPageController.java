package geotzinos.crowdgaming;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import geotzinos.crowdgaming.General.Effect;
import geotzinos.crowdgaming.General.Validate;
import geotzinos.crowdgaming.Request.LoginPageRequest;

public class LoginPageController extends AppCompatActivity {
    //UI Elements
    private EditText etEmail;
    private EditText etPassword;
    private Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page_view);

        //Initialize refferences
        etEmail = (EditText) findViewById(R.id.emailTextField);
        etPassword = (EditText) findViewById(R.id.passwordTextField);
        loginButton = (Button) findViewById(R.id.login_button);
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
            Effect.Alert(LoginPageController.this, "Login failed. Fill a valid email address.", "Okay");
            return;
        } else if (validationResults == -2) {
            Effect.Alert(LoginPageController.this, "Login failed. Fill a valid password.", "Okay");
            return;
        }

        //Loader
        Effect.ShowSpinner(this, "Loading", "Please wait while trying to login.");

        JsonObjectRequest loginRequest = new LoginPageRequest().Login(this, emailText, passwordText);
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.add(loginRequest);
        loginButton.setEnabled(false);

    }


}
