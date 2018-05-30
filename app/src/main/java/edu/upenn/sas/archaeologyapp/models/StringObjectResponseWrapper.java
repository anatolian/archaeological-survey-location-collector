// String JSON Object response
// @author: msenol
package edu.upenn.sas.archaeologyapp.models;
import com.android.volley.VolleyError;
public abstract class StringObjectResponseWrapper
{
    /**
     * Constructor
     */
    protected StringObjectResponseWrapper()
    {
    }

    /**
     * This will be overwritten in many of the API calls that will be used to interact with the camera
     * @param response - camera response
     */
    public abstract void responseMethod(String response);

    /**
     * Connection failed
     * @param error - failure
     */
    public abstract void errorMethod(VolleyError error);
}
