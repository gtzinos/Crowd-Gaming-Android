package geotzinos.crowdgaming.General;

/**
 * Created by George on 2016-06-26.
 */
public class Config {

    //Debug (Default=false) *Change to false before production
    public final static boolean Debug = true;

    //Server domain name or ip address
    public final static String WEB_ROOT = "http://83.212.118.212/Crowd-Gaming/public";

    //Mysqli database name
    public final static String LOCAL_DATABASE_NAME = "CrowdGaming";

    //Permissions for INTERNET code
    public final static int MY_PERMISSIONS_INTERNET_REQUEST_CODE = 750;

    //Permissions for ACCESS_FINE_LOCATION code
    public final static int MY_PERMISSIONS_ACCESS_FINE_LOCATION_REQUEST_CODE = 751;

    //Permissions for ACCESS_COARSE_LOCATION code
    public final static int MY_PERMISSIONS_ACCESS_COARSE_LOCATION_REQUEST_CODE = 752;

    //Application name
    public final static String SHORT_APP_NAME = "Crowd Game";

    //Full application name
    public final static String FULL_APP_NAME = "Crowding Game";

    //Date format for application requests
    public final static String DATE_FORMAT = "DD-MM-YYYY";

    //Google api key
    public final static String GOOGLE_API_KEY = "AIzaSyCclvgjNy2vp9rD8TrAnbNs4wvXft7hKiY";
}
