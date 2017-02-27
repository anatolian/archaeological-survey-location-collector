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
     * Bundle key for latitude
     */
    public static String PARAM_KEY_LATITUDE = "latitude";

    /**
     * Bundle key for longitude
     */
    public static String PARAM_KEY_LONGITUDE = "longitude";

    /**
     * Bundle key for material
     */
    public static String PARAM_KEY_MATERIAL = "material";

    /**
     * Bundle key for image
     */
    public static String PARAM_KEY_IMAGE = "image";

    /**
     * Bundle key for comments
     */
    public static String PARAM_KEY_COMMENTS = "comments";

    /**
     * The REST API endpoint for syncing data
     */
    public static String UPLOAD_URL = "https://pennmuseum2017.pythonanywhere.com/index/m_upload_record/";

    /**
     * The secret code expected by the server to authenticate the request
     */
    public static String APP_SECRET = "6de72eea-251a-45e6-96b3-8f8fa7a2a2de";

}
