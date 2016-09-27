package tzinos.crowdgaming.General;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by George on 2016-06-26.
 */
public class Validate {

    /*
        Verify email address
    */
    public static boolean EmailAddress(String email) {
        String expression = "^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }

    /*
        Verify password length
    */
    public static boolean Password(String password) {
        if (password.length() < 8) {
            return false;
        }
        return true;
    }

    /**
     * Validate user credentials before login.
     *
     * @return int 0 (Correct) -1(Wrong email) -2(Wrong password)
     */
    public static int LoginCredentials(String email, String password) {
        //Validate email
        if (!EmailAddress(email)) {
            Effect.Log("Class Validate", "Wrong email address value.");
            return -1;
        }
        //Validate password
        else if (!Password(password)) {
            Effect.Log("Class Validate", "Wrong password value.");
            return -2;
        }

        //Correct values
        return 0;
    }
}
