package geotzinos.crowdgaming;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import geotzinos.crowdgaming.Requests.LoginRequest;

public class LoginPageController extends AppCompatActivity {
    //UI Elements
    private EditText etEmail;
    private EditText etPassword;
    private Button loginButton;

    //Saved values
    private String emailText;
    private String passwordText;

    //Spinner refference
    private ProgressDialog spinner;

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

    /**
     * Validate user credentials before login.
     *
     * @return boolean true or false
     */
    public boolean ValidateLoginCredentials() {
        //Validate
        if (etEmail.getText().toString().equals("") || etPassword.getText().toString().equals("")) {
            //Wrong values
            return false;
        }

        //Correct values
        return true;
    }

    public void Login() {
        if (!ValidateLoginCredentials()) {
            AlertDialog.Builder alert = new AlertDialog.Builder(LoginPageController.this);
            alert.setMessage("Login failed. Fill in email and password.")
                    .setPositiveButton("Okay", null)
                    .create()
                    .show();
            return;
        }

        //Loader
        ShowSpinner();

        //Store credentials
        emailText = etEmail.getText().toString();
        passwordText = etPassword.getText().toString();

        //Send credentials
        Response.Listener<JSONObject> responseLogin = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    loginButton.setEnabled(true);

                    int code = response.getInt("code");

                    JSONObject userJSON = response.getJSONObject("user");

                    if (code == 200) {
                        CloseSpinner();
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

    /*
        Display spinner on Login page
    */
    public void ShowSpinner() {
        spinner = ProgressDialog.show(LoginPageController.this, "Loading", "Please wait while trying to login...", true);
    }

    /*
        Remove spinner from login page
    */
    public void CloseSpinner() {
        spinner.dismiss();
    }
}
