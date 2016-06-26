package geotzinos.crowdgaming;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import geotzinos.crowdgaming.General.Effects;
import geotzinos.crowdgaming.General.Validate;
import geotzinos.crowdgaming.Request.LoginRequest;

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
            Effects.Alert(LoginPageController.this, "Login failed. Fill a valid email address.", "Okay");
            return;
        } else if (validationResults == -2) {
            Effects.Alert(LoginPageController.this, "Login failed. Fill a valid password.", "Okay");
            return;
        }

        //Loader
        Effects.ShowSpinner(this, "Loading", "Please wait while trying to login.");

        //Send credentials
        Response.Listener<JSONObject> responseLogin = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    loginButton.setEnabled(true);

                    int code = response.getInt("code");

                    JSONObject userJSON = response.getJSONObject("user");

                    if (code == 200) {
                        Effects.CloseSpinner();
                        //startActivity(new Intent(LoginPageController.this,MyQuestionnairesActivity.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        String dataToConvert = "{\"email\":" + emailText + ",\"password\":" + passwordText + "}";
        try {
            loginButton.setEnabled(false);
            JSONObject loginData = new JSONObject(dataToConvert);
            LoginRequest loginRequest = new LoginRequest(loginData, responseLogin, null);
            RequestQueue requests = Volley.newRequestQueue(this);
            requests.add(loginRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
