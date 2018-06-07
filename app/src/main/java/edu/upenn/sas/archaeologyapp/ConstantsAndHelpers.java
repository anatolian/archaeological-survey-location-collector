package edu.upenn.sas.archaeologyapp;

/**
 * Class containing any constants and helpers required throughout the app
 * Created by eanvith on 24/12/16.
 */

public class ConstantsAndHelpers {

    /**
     * Time for which the splash activity must pause before proceeding to the main activity
     */
    public static int SPLASH_TIME_OUT = 2000;

    /**
     * Time for which we wait for GPS listener to return a location
     */
    public static int GPS_TIME_OUT = 20000;

    /**
     * Bundle key for ID
     */
    public static String PARAM_KEY_ID = "entry_id";

    /**
     * Bundle key for zone
     */
    public static String PARAM_KEY_ZONE = "zone";

    /**
     * Bundle key for hemisphere
     */
    public static String PARAM_KEY_HEMISPHERE = "hemisphere";

    /**
     * Bundle key for northing
     */
    public static String PARAM_KEY_NORTHING = "northing";

    /**
     * Bundle key for easting
     */
    public static String PARAM_KEY_EASTING = "easting";

    /**
     * Bundle key for sample
     */
    public static String PARAM_KEY_SAMPLE = "sample";

    /**
     * Bundle key for latitude
     */
    public static String PARAM_KEY_LATITUDE = "latitude";

    /**
     * Bundle key for longitude
     */
    public static String PARAM_KEY_LONGITUDE = "longitude";

    /**
     * Bundle key for altitude
     */
    public static String PARAM_KEY_ALTITUDE = "altitude";

    /**
     * Bundle key for status
     */
    public static String PARAM_KEY_STATUS = "status";

    /**
     * Bundle key for AR ratio
     */
    public static String PARAM_KEY_AR_RATIO = "AR_ratio";

    /**
     * Bundle key for material
     */
    public static String PARAM_KEY_MATERIAL = "material";

    /**
     * Bundle key for image
     */
    public static String PARAM_KEY_IMAGES = "images";

    /**
     * Bundle key for comments
     */
    public static String PARAM_KEY_COMMENTS = "comments";

    /**
     * Bundle key for team member
     */
    public static String PARAM_KEY_TEAM_MEMBER = "team_member";

    /**
     * Bundle key for begin latitude
     */
    public static String PARAM_KEY_BEGIN_LATITUDE = "begin_latitude";

    /**
     * Bundle key for end latitude
     */
    public static String PARAM_KEY_END_LATITUDE = "end_latitude";

    /**
     * Bundle key for begin longitude
     */
    public static String PARAM_KEY_BEGIN_LONGITUDE = "begin_longitude";

    /**
     * Bundle key for end longitude
     */
    public static String PARAM_KEY_END_LONGITUDE = "end_longitude";

    /**
     * Bundle key for beign altitude
     */
    public static String PARAM_KEY_BEGIN_ALTITUDE = "begin_altitude";

    /**
     * Bundle key for end altitude
     */
    public static String PARAM_KEY_END_ALTITUDE = "end_altitude";

    /**
     * Bundle key for begin easting
     */
    public static String PARAM_KEY_BEGIN_EASTING = "begin_easting";

    /**
     * Bundle key for begin northing
     */
    public static String PARAM_KEY_BEGIN_NORTHING = "begin_northing";

    /**
     * Bundle key for end easting
     */
    public static String PARAM_KEY_END_EASTING = "end_easting";

    /**
     * Bundle key for end northing
     */
    public static String PARAM_KEY_END_NORTHING = "end_northing";

    /**
     * Bundle key for begin time
     */
    public static String PARAM_KEY_BEGIN_TIME = "begin_time";

    /**
     * Bundle key for end time
     */
    public static String PARAM_KEY_END_TIME = "end_time";

    /**
     * Bundle key for begin status
     */
    public static String PARAM_KEY_BEGIN_STATUS = "begin_status";

    /**
     * Bundle key for end status
     */
    public static String PARAM_KEY_END_STATUS = "end_status";

    /**
     * Bundle key for begin AR ratio
     */
    public static String PARAM_KEY_BEGIN_AR_RATIO = "begin_AR_ratio";

    /**
     * Bundle key for end AR ratio
     */
    public static String PARAM_KEY_END_AR_RATIO = "end_AR_ratio";

    /**
     * The REST API endpoint for syncing data
     */
    public static String UPLOAD_URL = "https://pennmuseum2017.pythonanywhere.com/index/m_upload_record/";

    /**
     * The secret code expected by the server to authenticate the request
     */
    public static String APP_SECRET = "6de72eea-251a-45e6-96b3-8f8fa7a2a2de";

    /**
     * Default time interval for updating the position
     */
    public static int DEFAULT_POSITION_UPDATE_INTERVAL = 2;

    /**
     * Default host for an Emlid Reach position output server
     */
    public static final String DEFAULT_REACH_HOST = "192.168.43.162";

    /**
     * Default port for an Emlid Reach position output server
     */
    public static final String DEFAULT_REACH_PORT = "9001";

}
