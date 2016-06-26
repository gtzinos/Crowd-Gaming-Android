package geotzinos.crowdgaming;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import geotzinos.crowdgaming.General.Effects;
import geotzinos.crowdgaming.Requests.LoginRequest;

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

    /**
     * Validate user credentials before login.
     *
     * @return boolean true or false
     */
    public int ValidateLoginCredentials(String email, String password) {
        //Validate email
        if (!validateEmailAddress(email)) {
            return -1;
        }
        //Validate password
        else if (password.length() < 8) {
            return -2;
        }

        //Correct values
        return 0;
    }

    /*
        Login credentials validation
    */
    private boolean validateEmailAddress(String emailAddress) {
        String expression = "^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = emailAddress;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }

    public void Login() {
        //Store credentials
        String emailText = etEmail.getText().toString();
        String passwordText = etPassword.getText().toString();

        int validationResults = ValidateLoginCredentials(emailText, passwordText);

        if (validationResults == -1) {
            AlertDialog.Builder alert = new AlertDialog.Builder(LoginPageController.this);
            alert.setMessage("Login failed. Fill a valid email address.")
                    .setPositiveButton("Okay", null)
                    .create()
                    .show();
            return;
        } else if (validationResults == -2) {
            AlertDialog.Builder alert = new AlertDialog.Builder(LoginPageController.this);
            alert.setMessage("Login failed. Fill a valid password.")
                    .setPositiveButton("Okay", null)
                    .create()
                    .show();
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
