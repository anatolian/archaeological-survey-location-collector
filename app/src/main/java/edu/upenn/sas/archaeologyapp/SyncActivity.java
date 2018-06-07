package edu.upenn.sas.archaeologyapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

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
     * The index of the find currently being uploaded
     */
    int uploadIndex;

    /**
     * Total number of finds to upload
     */
    int totalItems;

    /**
     * A list of paths populated from the local database, that need to be uploaded onto the server
     */
    ArrayList<PathElement> pathsToUpload;

    /**
     * The index of the path currently being uploaded
     */
    int pathUploadIndex;

    /**
     * Total number of paths to upload
     */
    int totalPaths;

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
        queue = Volley.newRequestQueue(this);

        // Initialise the database helper class object, and read in the records from the local database
        dataBaseHandler = new DataBaseHandler(this);

        elementsToUpload = dataBaseHandler.getUnsyncedFindsRows();
        pathsToUpload = dataBaseHandler.getUnsyncedPathsRows();

        totalItems = elementsToUpload.size();
        uploadIndex = 0;

        totalPaths = pathsToUpload.size();
        pathUploadIndex = 0;


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

                // Start uploading unsynced paths
                uploadPath();

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
            String ARratio = Double.toString(find.getARRatio());
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

    private void uploadPath() {
        if (pathUploadIndex < totalPaths) {
            final PathElement path = pathsToUpload.get(pathUploadIndex);

            String teamMember = path.getTeamMember();

            String beginLatitude = Double.toString(path.getBeginLatitude());
            String beginLongitude = Double.toString(path.getBeginLongitude());
            String beginAltitude = Double.toString(path.getBeginAltitude());
            String beginStatus = path.getBeginStatus();
            String beginARRatio =  Double.toString(path.getBeginARRatio());

            String endLatitude =  Double.toString(path.getEndLatitude());
            String endLongitude = Double.toString(path.getEndLongitude());
            String endAltitude = Double.toString(path.getEndAltitude());
            String endStatus =  path.getEndStatus();
            String endARRatio = Double.toString(path.getEndARRatio());

            String hemisphere = path.getHemisphere();
            String zone = Integer.toString(path.getZone());
            String beginNorthing = Double.toString(path.getBeginNorthing());
            String beginEasting = Double.toString(path.getBeginEasting());
            String endNorthing = Double.toString(path.getEndNorthing());
            String endEasting = Double.toString(path.getEndEasting());
            String beginTime = Double.toString(path.getBeginTime());
            String endTime = Double.toString(path.getEndTime());

            edu.upenn.sas.archaeologyapp.services.VolleyStringWrapper.makeVolleyStringObjectRequest(globalWebServerURL + "/insert_find?teamMember=" + teamMember
                            + "&hemisphere=" + hemisphere + "&zone=" + zone + "&beginEasting=" + beginEasting + "&beginNorthing=" + beginNorthing
                            + "&endEasting=" + endEasting + "&endNorthing=" + endNorthing + "&beginLatitude=" + beginLatitude + "&beginLongitude=" + beginLongitude
                            + "&beginAltitude=" + beginAltitude + "&beginStatus=" + beginStatus + "&beginARRatio=" + beginARRatio + "&endLatitude=" + endLatitude
                            + "&endLongitude=" + endLongitude + "&endAltitude=" + endAltitude + "&endStatus=" + endStatus + "&endARRatio=" + endARRatio, queue,
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
                                dataBaseHandler.setPathSynced(path);

                                // Upload the next find
                                pathUploadIndex++;
                                uploadPath();

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
