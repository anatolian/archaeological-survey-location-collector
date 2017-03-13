package edu.upenn.sas.archaeologyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        // Initialise the database helper class object, and read in the records from the local databse
        dataBaseHandler = new DataBaseHandler(this);
        elementsToUpload = dataBaseHandler.getRows();
        totalItems = elementsToUpload.size();
        uploadIndex = 0;

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

                try {

                    // Make a call to upload the current record
                    Toast.makeText(SyncActivity.this, String.format("Uploading item %2d out of %2d", uploadIndex+1, totalItems), Toast.LENGTH_SHORT).show();
                    new MultipartUploadRequest(SyncActivity.this, ConstantsAndHelpers.UPLOAD_URL)
                        .addParameter("secret_key", ConstantsAndHelpers.APP_SECRET)
                        .addParameter("record_id", elementsToUpload.get(uploadIndex).getID())
                        .addParameter("latitude", Double.toString(elementsToUpload.get(uploadIndex).getLatitude()))
                        .addParameter("longitude", Double.toString(elementsToUpload.get(uploadIndex).getLongitude()))
                        .addParameter("material", elementsToUpload.get(uploadIndex).getMaterial())
                        .addParameter("comment", elementsToUpload.get(uploadIndex).getComments())
                        .addParameter("created_ts", Long.toString(elementsToUpload.get(uploadIndex).getCreatedTimestamp()))
                        .addParameter("updated_ts", Long.toString(elementsToUpload.get(uploadIndex).getUpdateTimestamp()))
                        .addFileToUpload(elementsToUpload.get(uploadIndex).getImagePath(), "image")
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload();

                } catch (Exception ex) {
                    Toast.makeText(SyncActivity.this, "Unexpected error while trying to upload record.", Toast.LENGTH_SHORT).show();
                    syncButton.setEnabled(true);
                    ex.printStackTrace();
                }

            }
        });

    }

    @Override
    public void onResume() {

        super.onResume();

        // Register a listener that is triggered when a sync operation completes
        uploadReceiver.register(SyncActivity.this);

    }

    @Override
    public void onPause() {

        super.onPause();

        // Unregister the listener if this activity is going into the background
        uploadReceiver.unregister(SyncActivity.this);

    }

    private final UploadServiceBroadcastReceiver uploadReceiver =
        new UploadServiceBroadcastReceiver() {

            // you can override this progress method if you want to get
            // the completion progress in percent (0 to 100)
            // or if you need to know exactly how many bytes have been transferred
            // override the method below this one
            @Override
            public void onProgress(String uploadId, int progress) {

            }

            @Override
            public void onProgress(final String uploadId,
                                   final long uploadedBytes,
                                   final long totalBytes) {

            }

            @Override
            public void onError(String uploadId, Exception exception) {

                syncButton.setEnabled(true);
                Toast.makeText(SyncActivity.this, "Unexpected error uploading photos, please try again after some time.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCompleted(String uploadId,
                                    int serverResponseCode,
                                    byte[] serverResponseBody) {

                // Check if a response exists
                if (serverResponseBody.length > 0) {
                    try {

                        // Process JSON result
                        JSONObject jObj = new JSONObject(new String(serverResponseBody));

                        // Check if errors or success
                        if (jObj.isNull("errors")) {

                            // Server returns record_id if successfully uploaded
                            String recordId = jObj.getString("results");

                            // Remove local copy
                            dataBaseHandler.removeRow(recordId);

                            // Move to the next record to be uploaded
                            uploadIndex++;

                            // Check if all records have been uploaded
                            if (uploadIndex >= totalItems) {

                                Toast.makeText(SyncActivity.this, "All records have been uploaded.", Toast.LENGTH_SHORT).show();
                                syncButton.setEnabled(true);
                                return;

                            }

                            // Make a call to upload the next record
                            Toast.makeText(SyncActivity.this, String.format("Uploading item %2d out of %2d", uploadIndex+1, totalItems), Toast.LENGTH_SHORT).show();
                            new MultipartUploadRequest(SyncActivity.this, ConstantsAndHelpers.UPLOAD_URL)
                                .addParameter("secret_key", ConstantsAndHelpers.APP_SECRET)
                                .addParameter("record_id", elementsToUpload.get(uploadIndex).getID())
                                .addParameter("latitude", Double.toString(elementsToUpload.get(uploadIndex).getLatitude()))
                                .addParameter("longitude", Double.toString(elementsToUpload.get(uploadIndex).getLongitude()))
                                .addParameter("material", elementsToUpload.get(uploadIndex).getMaterial())
                                .addParameter("comment", elementsToUpload.get(uploadIndex).getComments())
                                .addParameter("created_ts", Long.toString(elementsToUpload.get(uploadIndex).getCreatedTimestamp()))
                                .addParameter("updated_ts", Long.toString(elementsToUpload.get(uploadIndex).getUpdateTimestamp()))
                                .addFileToUpload(elementsToUpload.get(uploadIndex).getImagePath(), "image")
                                .setNotificationConfig(new UploadNotificationConfig())
                                .setMaxRetries(2)
                                .startUpload();

                        } else {

                            // Display the error code sent by the server
                            JSONArray jArr = jObj.getJSONArray("errors");
                            for (int i = 0; i < jArr.length(); i++) {
                                int errorCode = jArr.getJSONObject(i).getInt("error_code");
                                //Process error code
                                Toast.makeText(SyncActivity.this, String.format("Error code %2d while uploading image.", errorCode), Toast.LENGTH_SHORT).show();
                                syncButton.setEnabled(true);
                            }

                        }
                    } catch (Exception e) {
                        Toast.makeText(SyncActivity.this, "Unexpected error while uploading record. Please try again.", Toast.LENGTH_SHORT).show();
                        syncButton.setEnabled(true);
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(String uploadId) {

                syncButton.setEnabled(true);
                Toast.makeText(SyncActivity.this, "Upload cancelled. If this was unexpected, please try again after some time.", Toast.LENGTH_SHORT).show();

            }
        };

}
