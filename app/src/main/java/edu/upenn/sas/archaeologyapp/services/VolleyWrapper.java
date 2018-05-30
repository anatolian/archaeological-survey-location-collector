// URL communication wrapper
// @author: msenol
package edu.upenn.sas.archaeologyapp.services;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import edu.upenn.sas.archaeologyapp.models.JSONObjectResponseWrapper;
import static com.android.volley.Request.Method;
public class VolleyWrapper
{
    /**
     * Send photo request
     * @param URL - camera URL
     * @param queue - request queue
     * @param ID - request id
     * @param LAMBDA_WRAPPER - request wrapper
     * @throws JSONException if the JSON is malformed
     */
    public static void makeVolleySonyAPITakePhotoRequest(final String URL, RequestQueue queue, final int ID,
                                                         final JSONObjectResponseWrapper LAMBDA_WRAPPER)
            throws JSONException
    {
        final String POST_BODY = new JSONObject().put("method", "awaitTakePicture")
                .put("params", new JSONArray()).put("id", ID).put("version", "1.0").toString();
        JSONObject JSONPOSTBody = new JSONObject(POST_BODY);
        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, URL, JSONPOSTBody,
                new Response.Listener<JSONObject>() {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void onResponse(JSONObject response)
            {
                LAMBDA_WRAPPER.responseMethod(response);
            }
        }, new Response.ErrorListener() {
            /**
             * Connection error
             * @param error - failure
             */
            @Override
            public void onErrorResponse(VolleyError error)
            {
                LAMBDA_WRAPPER.errorMethod(error);
            }
        });
        myRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
    }

    /**
     * Request camera feed
     * @param URL - camera URL
     * @param queue - request queue
     * @param ID - request id
     * @param LAMBDA_WRAPPER - request wrapper
     * @throws JSONException if the JSON is malformed
     */
    public static void makeVolleySonyAPIStartLiveViewRequest(final String URL, RequestQueue queue, final int ID,
                                                             final JSONObjectResponseWrapper LAMBDA_WRAPPER)
            throws JSONException
    {
        // setting up with params for JSON object
        final String POST_BODY = new JSONObject().put("method", "startLiveview")
                .put("params", new JSONArray()).put("id", ID).put("version","1.0").toString();
        JSONObject JSONPOSTBody = new JSONObject(POST_BODY);
        // making request
        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, URL, JSONPOSTBody,
                new Response.Listener<JSONObject>() {
            /**
             * Response received
             * @param response - camera response
             */
            @Override
            public void onResponse(JSONObject response)
            {
                LAMBDA_WRAPPER.responseMethod(response);
            }
        }, new Response.ErrorListener() {
            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void onErrorResponse(VolleyError error)
            {
                LAMBDA_WRAPPER.errorMethod(error);
            }
        });
        // setting up retry policy in case of failure
        myRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
    }

    /**
     * Stopping live view usually in case of error or when image has been captured
     * @param URL - camera URL
     * @param queue - request queue
     * @param ID - request id
     * @param LAMBDA_WRAPPER - request wrapper
     */
    public static void makeVolleySonyAPIStopLiveViewRequest(final String URL, RequestQueue queue, final int ID,
                                                            final JSONObjectResponseWrapper LAMBDA_WRAPPER)
            throws JSONException
    {
        // adding params for JSON object
        final String POST_BODY = new JSONObject().put("method", "stopLiveview")
                .put("params", new JSONArray()).put("id", ID).put("version", "1.0").toString();
        JSONObject JSONPOSTBody = new JSONObject(POST_BODY);
        // making request
        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, URL, JSONPOSTBody,
                new Response.Listener<JSONObject>() {
            /**
             * Response received
             * @param response - camera response
             */
            @Override
            public void onResponse(JSONObject response)
            {
                LAMBDA_WRAPPER.responseMethod(response);
            }
        }, new Response.ErrorListener() {
            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void onErrorResponse(VolleyError error)
            {
                LAMBDA_WRAPPER.errorMethod(error);
            }
        });
        myRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
    }

    /**
     * Calls actZoom API to the target server. Request JSON data is such like as below.
     * {
     *   "method": "actZoom",
     *   "params": ["in","stop"],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * @param direction - direction of zoom ("in" or "out")
     * @param queue - request queue
     * @param URL - camera URL
     * @param ID - request id
     * @param LAMBDA_WRAPPER - request wrapper
     * @throws JSONException if the JSON is malformed
     */
    public static void makeVolleySonyAPIActZoomRequest(String direction, RequestQueue queue,
                                                       final String URL, final int ID,
                                                       final JSONObjectResponseWrapper LAMBDA_WRAPPER)
            throws JSONException
    {
        final String POST_BODY = new JSONObject().put("method", "actZoom")
                .put("params", new JSONArray().put(direction).put("1shot"))
                .put("id", ID).put("version", "1.0").toString();
        JSONObject JSONPOSTBody = new JSONObject(POST_BODY);
        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, URL, JSONPOSTBody,
                new Response.Listener<JSONObject>() {
            /**
             * Response received
             * @param response - camera response
             */
            @Override
            public void onResponse(JSONObject response)
            {
                LAMBDA_WRAPPER.responseMethod(response);
            }
        }, new Response.ErrorListener() {
            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void onErrorResponse(VolleyError error)
            {
                LAMBDA_WRAPPER.errorMethod(error);
            }
        });
        myRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
    }

    /**
     * Cancel camera requests
     * @param aQueue - request queue
     */
    public static void cancelAllVolleyRequests(RequestQueue aQueue)
    {
        aQueue.cancelAll(new RequestQueue.RequestFilter() {
            /**
             * Cancel the request
             * @param request - request to cancel
             * @return Returns true
             */
            @Override
            public boolean apply(Request<?> request)
            {
                return true;
            }
        });
    }
}