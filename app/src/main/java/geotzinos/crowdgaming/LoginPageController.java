package geotzinos.crowdgaming;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
                //Login();
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
