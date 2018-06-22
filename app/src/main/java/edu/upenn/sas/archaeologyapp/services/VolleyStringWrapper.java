// String response
// @author: msenol
package edu.upenn.sas.archaeologyapp.services;
import com.android.volley.RequestQueue;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import edu.upenn.sas.archaeologyapp.models.StringObjectResponseWrapper;
import static edu.upenn.sas.archaeologyapp.util.Constants.DEFAULT_VOLLEY_TIMEOUT;
public class VolleyStringWrapper
{
    /**
     * Request string response
     * @param URL - URL to query
     * @param queue - request queue
     * @param LAMBDA_WRAPPER - response wrapper
     */
    public static void makeVolleyStringObjectRequest(final String URL, RequestQueue queue,
                                                     final StringObjectResponseWrapper LAMBDA_WRAPPER)
    {
        // creating the listener to respond to object request
        StringRequest myRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            /**
             * Response received
             * @param response - camera response
             */
            @Override
            public void onResponse(String response)
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
        // Add the request to the RequestQueue.
        myRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // request has been added to the queue
        queue.add(myRequest);
    }
}