package edu.upenn.sas.archaeologyapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static edu.upenn.sas.archaeologyapp.util.StateStatic.globalWebServerURL;

/**
 * This activity is responsible for uploading all the records from the local database onto a server.
 * Created by eanvith on 27/02/17.
 */

public class SyncActivity extends AppCompatActivity {

    /**
     * The button the user clicks to initiate the sync process
     */
    Button syncButton;

    /**
     * A list of records populated from the local database, that need to be uploaded onto the server
     */
    ArrayList<DataEntryElement> elementsToUpload;

    /**
     * The index of the element currently being uploaded in elementsToUpload list
     */
    int uploadIndex;

    /**
     * Total number of items contained in elementsToUpload list
     */
    int totalItems;

    /**
     * A database helper class object that enables fetching of records from the local database
     */
    DataBaseHandler dataBaseHandler;

    /**
     * A request queue to handle python requests
     */
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        // Initialise the database helper class object, and read in the records from the local database
        dataBaseHandler = new DataBaseHandler(this);
        elementsToUpload = dataBaseHandler.getUnsyncedFindsRows();
        totalItems = elementsToUpload.size();
        uploadIndex = 0;
        queue = Volley.newRequestQueue(this);

        // Attach a click listener to the sync button, and trigger the sync process on click of the button
        syncButton = (Button) findViewById(R.id.sync_button_sync_activity);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // totalItems is 0, so nothing to sync
                if (uploadIndex >= totalItems) {

                    Toast.makeText(SyncActivity.this, "There are no records to sync.", Toast.LENGTH_SHORT).show();

                }

                // Disable the sync button while the sync is in progress
                syncButton.setEnabled(false);

                // Start uploading unsynced items
                uploadFind();

            }
        });

    }

    private void uploadFind() {
        if (uploadIndex < totalItems) {
            final DataEntryElement find = elementsToUpload.get(uploadIndex);


            String zone = Integer.toString(find.getZone());
            String hemisphere = find.getHemisphere();
            String easting = Integer.toString(find.getEasting());
            String northing = Integer.toString(find.getNorthing());
            String sample = Integer.toString(find.getSample());

            String contextEasting = Integer.toString(find.getEasting());
            String contextNorthing = Integer.toString(find.getNorthing());

            String latitude = Double.toString(find.getLatitude());
            String longitude = Double.toString(find.getLongitude());
            String altitude = Double.toString(find.getAltitude());

            String status = find.getStatus();
            String material = find.getMaterial();
            String ARratio = "0";
            String locationTimestamp = "0";
            String comments = find.getComments();

            edu.upenn.sas.archaeologyapp.services.VolleyStringWrapper.makeVolleyStringObjectRequest(globalWebServerURL + "/insert_find?zone=" + zone
                            + "&hemisphere=" + hemisphere + "&easting=" + easting + "&northing=" + northing
                            + "&contextEasting=" + contextEasting + "&contextNorthing=" + contextNorthing
                            + "&find=" + sample + "&latitude=" + latitude + "&longitude=" + longitude + "&altitude=" + altitude + "&status=" + status + "&material=" + material + "&comments=" + comments + "&ARratio=" + ARratio, queue,
                    new edu.upenn.sas.archaeologyapp.models.StringObjectResponseWrapper() {
                        /**
                         * Response received
                         * @param response - database response
                         */
                        @Override
                        public void responseMethod(String response) {
                            System.out.println(response);
                            if (!response.contains("Error")) {

                                Toast.makeText(getApplicationContext(), "Successfully added find", Toast.LENGTH_SHORT).show();
                                dataBaseHandler.setFindSynced(find);

                                // Upload the next find
                                uploadIndex++;
                                uploadFind();

                            } else {

                                Toast.makeText(getApplicationContext(), "Upload unsuccessful: "+response, Toast.LENGTH_SHORT).show();

                            }
                        }

                        /**
                         * Connection failed
                         * @param error - failure
                         */
                        @Override
                        public void errorMethod(VolleyError error)
                        {
                            Toast.makeText(getApplicationContext(), "Upload unsuccessful (Communication error): "+error, Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    });

        } else {

            Toast.makeText(SyncActivity.this, "Done syncing finds", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onResume() {

        super.onResume();

    }

    @Override
    public void onPause() {

        super.onPause();

    }

}
