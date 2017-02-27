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

public class SyncActivity extends AppCompatActivity {

    Button syncButton;

    int uploadIndex, totalItems;

    ArrayList<DataEntryElement> elementsToUpload;

    DataBaseHandler dataBaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        dataBaseHandler = new DataBaseHandler(this);

        elementsToUpload = dataBaseHandler.getRows();
        totalItems = elementsToUpload.size();
        uploadIndex = 0;

        syncButton = (Button) findViewById(R.id.sync_button_sync_activity);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (uploadIndex >= totalItems) {

                    Toast.makeText(SyncActivity.this, "There are no records to sync.", Toast.LENGTH_SHORT).show();

                }

                syncButton.setEnabled(false);
                try {

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

        uploadReceiver.register(SyncActivity.this);

    }

    @Override
    public void onPause() {

        super.onPause();

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

                if (serverResponseBody.length > 0) {
                    try {

                        //Process JSON result
                        JSONObject jObj = new JSONObject(new String(serverResponseBody));

                        //Check if errors or success
                        if (jObj.isNull("errors")) {

                            dataBaseHandler.removeRow(elementsToUpload.get(uploadIndex).getID());
                            uploadIndex++;

                            if (uploadIndex >= totalItems) {

                                Toast.makeText(SyncActivity.this, "All records have been uploaded.", Toast.LENGTH_SHORT).show();
                                syncButton.setEnabled(true);
                                return;

                            }

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
