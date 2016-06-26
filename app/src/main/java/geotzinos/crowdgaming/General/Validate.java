package geotzinos.crowdgaming.General;

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
}
