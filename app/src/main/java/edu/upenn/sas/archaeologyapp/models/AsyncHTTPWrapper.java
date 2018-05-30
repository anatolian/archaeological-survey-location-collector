// Async HTTP wrapper
// @author: msenol
package edu.upenn.sas.archaeologyapp.models;
import android.net.Uri;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import java.io.File;
import java.io.FileNotFoundException;
public class AsyncHTTPWrapper
{
    /**
     * Upload image
     * @param URL - destination URL
     * @param imageURI - image location
     * @param easting - find easting
     * @param northing - find northing
     * @param find - find number
     * @param CALLBACK_WRAPPER - callback function
     */
    public static void makeImageUpload(String URL, Uri imageURI, String easting, String northing,
                                       String find, final edu.upenn.sas.archaeologyapp.models.AsyncHTTPCallbackWrapper CALLBACK_WRAPPER)
    {
        // setting up variables to establish connection with server
        AsyncHttpClient client = new AsyncHttpClient();
        File myFile = new File(imageURI.getPath());
        RequestParams params = new RequestParams();
        try
        {
            params.put("myFile", myFile);
            params.put("file_name", myFile.getPath());
            params.put("easting", easting);
            params.put("northing", northing);
            params.put("find", find);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        // send to database
        client.post(URL, params, new TextHttpResponseHandler() {
            /**
             * Post request failed
             * @param statusCode - HTTP status
             * @param headers - HTTP headers
             * @param responseString - HTTP response
             * @param throwable - error
             */
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers,
                                  String responseString, Throwable throwable)
            {
                CALLBACK_WRAPPER.onFailureCallback();
            }

            /**
             * HTTP success
             * @param statusCode - HTTP status
             * @param headers - response headers
             * @param responseString - response body
             */
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers,
                                  String responseString)
            {
                CALLBACK_WRAPPER.onSuccessCallback(responseString);
            }

            /**
             * Response started
             */
            @Override
            public void onStart()
            {
                super.onStart();
                CALLBACK_WRAPPER.onStartCallback();
            }

            /**
             * Retry request
             * @param retryNo - attempt number
             */
            @Override
            public void onRetry(int retryNo)
            {
                super.onRetry(retryNo);
                CALLBACK_WRAPPER.onRetryCallback();
            }
        });
    }
}