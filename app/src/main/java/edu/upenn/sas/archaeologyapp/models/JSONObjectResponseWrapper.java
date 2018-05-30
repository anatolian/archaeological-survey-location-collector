// JSONObject response
// @author: msenol
package edu.upenn.sas.archaeologyapp.models;
import android.content.Context;
import com.android.volley.VolleyError;
import org.json.JSONObject;
abstract public class JSONObjectResponseWrapper
{
    public Context currentContext;
    /**
     * Constructor
     * @param aContext - calling context
     */
    protected JSONObjectResponseWrapper(Context aContext)
    {
        this.currentContext = aContext;
    }

    /**
     * This will be overwritten in many of the api calls that will be used to interact with the camera
     * @param response - camera response
     */
    public abstract void responseMethod(JSONObject response);

    /**
     * Connection failed
     * @param error - failure
     */
    public abstract void errorMethod(VolleyError error);
}