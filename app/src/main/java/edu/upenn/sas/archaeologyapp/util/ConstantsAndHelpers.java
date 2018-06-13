package edu.upenn.sas.archaeologyapp.util;
/**
 * Class containing any constants and helpers required throughout the app
 * Created by eanvith on 24/12/16.
 */
public class ConstantsAndHelpers
{
    // Time for which the splash activity must pause before proceeding to the main activity
    public static int SPLASH_TIME_OUT = 2000;
    public static final String PARAM_KEY_ID = "entry_id", PARAM_KEY_ZONE = "zone", PARAM_KEY_HEMISPHERE = "hemisphere";
    public static final String PARAM_KEY_NORTHING = "northing", PARAM_KEY_EASTING = "easting", PARAM_KEY_SAMPLE = "sample";
    public static final String PARAM_KEY_LATITUDE = "latitude", PARAM_KEY_PRECISE_EASTING = "precise easting";
    public static final String PARAM_KEY_LONGITUDE = "longitude", PARAM_KEY_PRECISE_NORTHING = "precise northing";
    public static final String PARAM_KEY_ALTITUDE = "altitude", PARAM_KEY_STATUS = "status", PARAM_KEY_AR_RATIO = "AR_ratio";
    public static final String PARAM_KEY_MATERIAL = "material", PARAM_KEY_IMAGES = "images", PARAM_KEY_COMMENTS = "comments";
    public static final String PARAM_KEY_TEAM_MEMBER = "team_member", PARAM_KEY_BEGIN_LATITUDE = "begin_latitude";
    public static final String PARAM_KEY_END_LATITUDE = "end_latitude", PARAM_KEY_BEGIN_LONGITUDE = "begin_longitude";
    public static final String PARAM_KEY_END_LONGITUDE = "end_longitude", PARAM_KEY_BEGIN_ALTITUDE = "begin_altitude";
    public static final String PARAM_KEY_END_ALTITUDE = "end_altitude", PARAM_KEY_BEGIN_EASTING = "begin_easting";
    public static final String PARAM_KEY_BEGIN_NORTHING = "begin_northing", PARAM_KEY_END_EASTING = "end_easting";
    public static final String PARAM_KEY_END_NORTHING = "end_northing", PARAM_KEY_BEGIN_TIME = "begin_time";
    public static final String PARAM_KEY_END_TIME = "end_time", PARAM_KEY_BEGIN_STATUS = "begin_status";
    public static final String PARAM_KEY_END_STATUS = "end_status", PARAM_KEY_BEGIN_AR_RATIO = "begin_AR_ratio";
    public static final String PARAM_KEY_END_AR_RATIO = "end_AR_ratio";
    // Default time interval for updating the position
    public static final int DEFAULT_POSITION_UPDATE_INTERVAL = 2;
    // Default host and port for an Emlid Reach position output server
    public static final String DEFAULT_REACH_HOST = "192.168.43.162", DEFAULT_REACH_PORT = "9001";
    private static final String DEFAULT_WEB_SERVER_URL = "https://object-data-collector-service.herokuapp.com";
    public static final int DEFAULT_VOLLEY_TIMEOUT = 7000;
    public static String globalWebServerURL = DEFAULT_WEB_SERVER_URL;

}
