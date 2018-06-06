package edu.upenn.sas.archaeologyapp;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Task;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static edu.upenn.sas.archaeologyapp.util.StateStatic.GOOGLE_PLAY_SIGN_IN;
import static edu.upenn.sas.archaeologyapp.util.StateStatic.REQUEST_CODE_CREATE_FILE;
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
    private HashMap<String, Integer> imageNumbers = new HashMap<>();

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
    private DriveClient mDriveClient = null;
    private DriveResourceClient mDriveResourceClient = null;
    private boolean lock = false;
    HashMap<String, DriveFolder> folderCache = new HashMap<>();
    /**
     * Sign into google
     * @return Returns the sign in client
     */
    private GoogleSignInClient buildGoogleSignInClient()
    {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Drive.SCOPE_FILE).build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        GoogleSignInClient mGoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), GOOGLE_PLAY_SIGN_IN);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_PLAY_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try
        {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            mDriveClient = Drive.getDriveClient(getApplicationContext(), account);
            mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), account);
        }
        catch (ApiException e)
        {
            Log.w("Drive", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private synchronized void uploadFind() {
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
                                ArrayList<String> paths = elementsToUpload.get(uploadIndex).getImagePaths();
                                String key = hemisphere + "." + zone + "." + easting + "." + northing + "." + find;
                                if (imageNumbers.get(key) == null)
                                {
                                    imageNumbers.put(key, 0);
                                }
                                for (String path: paths)
                                {
                                    imageNumbers.put(key, imageNumbers.get(key) + 1);
                                    try
                                    {
                                        // File upload is asynchronous on Google's end, need to make sure the last file was uploaded
                                        // before trying another.
                                        try
                                        {
                                            Thread.sleep(5000);
                                        }
                                        catch (InterruptedException e)
                                        {
                                            e.printStackTrace();
                                        }
                                        uploadToDrive(hemisphere, find.getZone(), find.getEasting(),
                                                find.getNorthing(), find.getSample(), imageNumbers.get(key),
                                                MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(path))));
                                    }
                                    catch (IOException e)
                                    {
                                        Log.e("Drive", "could not open file");
                                    }
                                }
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
            finish();
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

    /**
     * Upload an image to Google Drive
     * @param hemisphere - find hemisphere
     * @param zone - find zone
     * @param easting - find easting
     * @param northing - find northing
     * @param find - find number
     * @param imageNumber - image number
     * @param bmp file to upload
     */
    private synchronized void uploadToDrive(String hemisphere, int zone, int easting, int northing, int find, int imageNumber, Bitmap bmp)
    {
        String key = hemisphere + "." + zone;
        String key2 = key + "." + easting;
        String key3 = key2 + "." + northing;
        String key4 = key3 + "." + find;
        // This blows tho
        mDriveResourceClient.getRootFolder().continueWithTask(task -> {
            DriveFolder rootFolder = task.getResult();
            Query query = new Query.Builder().addFilter(Filters.and(Filters.eq(SearchableField.TITLE, hemisphere),
                    Filters.eq(SearchableField.TRASHED, false))).build();
            return mDriveResourceClient.queryChildren(rootFolder, query);
        }).addOnSuccessListener(this, hemisphereBuffer -> {
            try
            {
                DriveFolder hemisphereFolder = folderCache.containsKey(hemisphere) ? folderCache.get(hemisphere)
                        : hemisphereBuffer.get(0).getDriveId().asDriveFolder();
                folderCache.put(hemisphere, hemisphereFolder);
                Log.v("Hemisphere", hemisphereFolder.getDriveId().toString());
                Query query = new Query.Builder().addFilter(Filters.and(Filters.eq(SearchableField.TITLE, "" + zone),
                        Filters.eq(SearchableField.TRASHED, false))).build();
                mDriveResourceClient.queryChildren(hemisphereFolder, query).addOnSuccessListener(this, zoneBuffer -> {
                    try
                    {
                        DriveFolder zoneFolder = folderCache.containsKey(key) ? folderCache.get(key)
                                : zoneBuffer.get(0).getDriveId().asDriveFolder();
                        folderCache.put(key, zoneFolder);
                        Query query2 = new Query.Builder().addFilter(Filters.and(Filters.eq(SearchableField.TITLE, "" + easting),
                                Filters.eq(SearchableField.TRASHED, false))).build();
                        mDriveResourceClient.queryChildren(zoneFolder, query2).addOnSuccessListener(this, eastingBuffer -> {
                            try
                            {
                                DriveFolder eastingFolder = folderCache.containsKey(key2) ? folderCache.get(key2)
                                        : eastingBuffer.get(0).getDriveId().asDriveFolder();
                                folderCache.put(key2, eastingFolder);
                                Query query3 = new Query.Builder().addFilter(Filters.and(Filters.eq(SearchableField.TITLE, "" + northing),
                                        Filters.eq(SearchableField.TRASHED, false))).build();
                                mDriveResourceClient.queryChildren(eastingFolder, query3).addOnSuccessListener(this, northingBuffer -> {
                                    try
                                    {
                                        DriveFolder northingFolder = folderCache.containsKey(key3) ? folderCache.get(key3)
                                                : northingBuffer.get(0).getDriveId().asDriveFolder();
                                        folderCache.put(key3, northingFolder);
                                        Query query4 = new Query.Builder().addFilter(Filters.and(
                                                Filters.eq(SearchableField.TITLE, "" + find),
                                                Filters.eq(SearchableField.TRASHED, false))).build();
                                        mDriveResourceClient.queryChildren(northingFolder, query4).addOnSuccessListener(this, findBuffer -> {
                                            try
                                            {
                                                DriveFolder findFolder = folderCache.containsKey(key4) ? folderCache.get(key4)
                                                        : findBuffer.get(0).getDriveId().asDriveFolder();
                                                folderCache.put(key4, northingFolder);
                                                Query query5 = new Query.Builder().addFilter(
                                                        Filters.eq(SearchableField.TRASHED, false)).build();
                                                mDriveResourceClient.queryChildren(findFolder, query5).addOnSuccessListener(this, imageBuffer -> {
                                                    Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
                                                    createContentsTask.continueWithTask(task -> {
                                                        DriveContents contents = task.getResult();
                                                        OutputStream outputStream = contents.getOutputStream();
                                                        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                                        MetadataChangeSet changeSet = new MetadataChangeSet
                                                                .Builder().setTitle(imageNumber + ".png")
                                                                .setMimeType("image/png").setStarred(false).build();
                                                        CreateFileActivityOptions createOptions = new CreateFileActivityOptions.Builder()
                                                                .setInitialDriveContents(contents).setInitialMetadata(changeSet)
                                                                .setActivityStartFolder(findFolder.getDriveId()).build();
                                                        return mDriveClient.newCreateFileActivityIntentSender(createOptions);
                                                    }).addOnSuccessListener(this, intentSender -> {
                                                        try
                                                        {
                                                            startIntentSenderForResult(intentSender, REQUEST_CODE_CREATE_FILE,
                                                                    null, 0, 0, 0);
                                                        }
                                                        catch (IntentSender.SendIntentException e2)
                                                        {
                                                            Toast.makeText(getApplicationContext(), "Error uploading to Drive",
                                                                    Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        }
                                                    });
                                                });
                                            }
                                            // Find folder not found
                                            catch (Exception e)
                                            {
                                                MetadataChangeSet set3 = new MetadataChangeSet.Builder().setTitle("" + find)
                                                        .setMimeType(DriveFolder.MIME_TYPE).setStarred(true).build();
                                                mDriveResourceClient.createFolder(northingFolder, set3).addOnSuccessListener(this, findFolder -> {
                                                    folderCache.put(key4, findFolder);
                                                    Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
                                                    createContentsTask.continueWithTask(task -> {
                                                        DriveContents contents = task.getResult();
                                                        OutputStream outputStream = contents.getOutputStream();
                                                        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle("1.png")
                                                                .setMimeType("image/png").setStarred(false).build();
                                                        CreateFileActivityOptions createOptions = new CreateFileActivityOptions.Builder()
                                                                .setInitialDriveContents(contents).setInitialMetadata(changeSet)
                                                                .setActivityStartFolder(findFolder.getDriveId()).build();
                                                        return mDriveClient.newCreateFileActivityIntentSender(createOptions);
                                                    }).addOnSuccessListener(this, intentSender -> {
                                                        try
                                                        {
                                                            startIntentSenderForResult(intentSender, REQUEST_CODE_CREATE_FILE,
                                                                    null, 0, 0, 0);
                                                        }
                                                        catch (IntentSender.SendIntentException e2)
                                                        {
                                                            Toast.makeText(getApplicationContext(), "Error uploading to Drive",
                                                                    Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        }
                                                    });
                                                });
                                            }
                                        });
                                    }
                                    // Northing folder not found
                                    catch (Exception e)
                                    {
                                        MetadataChangeSet set2 = new MetadataChangeSet.Builder().setTitle("" + northing)
                                                .setMimeType(DriveFolder.MIME_TYPE).setStarred(true).build();
                                        mDriveResourceClient.createFolder(eastingFolder, set2).addOnSuccessListener(this, northingFolder -> {
                                            folderCache.put(key3, northingFolder);
                                            MetadataChangeSet set3 = new MetadataChangeSet.Builder().setTitle("" + find)
                                                    .setMimeType(DriveFolder.MIME_TYPE).setStarred(true).build();
                                            mDriveResourceClient.createFolder(northingFolder, set3).addOnSuccessListener(this, findFolder -> {
                                                folderCache.put(key4, findFolder);
                                                Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
                                                createContentsTask.continueWithTask(task -> {
                                                    DriveContents contents = task.getResult();
                                                    OutputStream outputStream = contents.getOutputStream();
                                                    bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle("1.png")
                                                            .setMimeType("image/png").setStarred(false).build();
                                                    CreateFileActivityOptions createOptions = new CreateFileActivityOptions.Builder()
                                                            .setInitialDriveContents(contents).setInitialMetadata(changeSet)
                                                            .setActivityStartFolder(findFolder.getDriveId()).build();
                                                    return mDriveClient.newCreateFileActivityIntentSender(createOptions);
                                                }).addOnSuccessListener(this, intentSender -> {
                                                    try
                                                    {
                                                        startIntentSenderForResult(intentSender, REQUEST_CODE_CREATE_FILE,
                                                                null, 0, 0, 0);
                                                    }
                                                    catch (IntentSender.SendIntentException e2)
                                                    {
                                                        Toast.makeText(getApplicationContext(), "Error uploading to Drive",
                                                                Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                });
                                            });
                                        });
                                    }
                                });
                            }
                            // Easting not found
                            catch (Exception e)
                            {
                                MetadataChangeSet set1 = new MetadataChangeSet.Builder().setTitle("" + easting)
                                        .setMimeType(DriveFolder.MIME_TYPE).setStarred(true).build();
                                mDriveResourceClient.createFolder(zoneFolder, set1).addOnSuccessListener(this, eastingFolder -> {
                                    folderCache.put(key2, eastingFolder);
                                    MetadataChangeSet set2 = new MetadataChangeSet.Builder().setTitle("" + northing)
                                            .setMimeType(DriveFolder.MIME_TYPE).setStarred(true).build();
                                    mDriveResourceClient.createFolder(eastingFolder, set2).addOnSuccessListener(this, northingFolder -> {
                                        folderCache.put(key3, northingFolder);
                                        MetadataChangeSet set3 = new MetadataChangeSet.Builder().setTitle("" + find)
                                                .setMimeType(DriveFolder.MIME_TYPE).setStarred(true).build();
                                        mDriveResourceClient.createFolder(northingFolder, set3).addOnSuccessListener(this, findFolder -> {
                                            folderCache.put(key4, findFolder);
                                            Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
                                            createContentsTask.continueWithTask(task -> {
                                                DriveContents contents = task.getResult();
                                                OutputStream outputStream = contents.getOutputStream();
                                                bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle("1.png")
                                                        .setMimeType("image/png").setStarred(false).build();
                                                CreateFileActivityOptions createOptions = new CreateFileActivityOptions.Builder()
                                                        .setInitialDriveContents(contents).setInitialMetadata(changeSet)
                                                        .setActivityStartFolder(findFolder.getDriveId()).build();
                                                return mDriveClient.newCreateFileActivityIntentSender(createOptions);
                                            }).addOnSuccessListener(this, intentSender -> {
                                                try
                                                {
                                                    startIntentSenderForResult(intentSender, REQUEST_CODE_CREATE_FILE,
                                                            null, 0, 0, 0);
                                                }
                                                catch (IntentSender.SendIntentException e2)
                                                {
                                                    Toast.makeText(getApplicationContext(), "Error uploading to Drive",
                                                            Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            });
                                        });
                                    });
                                });
                            }
                        });
                    }
                    // Zone folder not found
                    catch (Exception e)
                    {
                        MetadataChangeSet set0 = new MetadataChangeSet.Builder().setTitle("" + zone)
                                .setMimeType(DriveFolder.MIME_TYPE).setStarred(true).build();
                        mDriveResourceClient.createFolder(hemisphereFolder, set0).addOnSuccessListener(this, zoneFolder -> {
                            folderCache.put(key, zoneFolder);
                            MetadataChangeSet set1 = new MetadataChangeSet.Builder().setTitle("" + easting)
                                    .setMimeType(DriveFolder.MIME_TYPE).setStarred(true).build();
                            mDriveResourceClient.createFolder(zoneFolder, set1).addOnSuccessListener(this, eastingFolder -> {
                                folderCache.put(key2, eastingFolder);
                                MetadataChangeSet set2 = new MetadataChangeSet.Builder().setTitle("" + northing)
                                        .setMimeType(DriveFolder.MIME_TYPE).setStarred(true).build();
                                mDriveResourceClient.createFolder(eastingFolder, set2).addOnSuccessListener(this, northingFolder -> {
                                    folderCache.put(key3, northingFolder);
                                    MetadataChangeSet set3 = new MetadataChangeSet.Builder().setTitle("" + find)
                                            .setMimeType(DriveFolder.MIME_TYPE).setStarred(true).build();
                                    mDriveResourceClient.createFolder(northingFolder, set3).addOnSuccessListener(this, findFolder -> {
                                        folderCache.put(key4, findFolder);
                                        Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
                                        createContentsTask.continueWithTask(task -> {
                                            DriveContents contents = task.getResult();
                                            OutputStream outputStream = contents.getOutputStream();
                                            bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle("1.png")
                                                    .setMimeType("image/png").setStarred(false).build();
                                            CreateFileActivityOptions createOptions = new CreateFileActivityOptions.Builder()
                                                    .setInitialDriveContents(contents).setInitialMetadata(changeSet)
                                                    .setActivityStartFolder(findFolder.getDriveId()).build();
                                            return mDriveClient.newCreateFileActivityIntentSender(createOptions);
                                        }).addOnSuccessListener(this, intentSender -> {
                                            try
                                            {
                                                startIntentSenderForResult(intentSender, REQUEST_CODE_CREATE_FILE,
                                                        null, 0, 0, 0);
                                            }
                                            catch (IntentSender.SendIntentException e2)
                                            {
                                                Toast.makeText(getApplicationContext(), "Error uploading to Drive",
                                                        Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                                    });
                                });
                            });
                        });
                    }
                }).addOnFailureListener(this, e2 -> {
                    Toast.makeText(getApplicationContext(), "Error retrieving files", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
            // Hemisphere was not found
            catch (Exception e)
            {
                mDriveResourceClient.getRootFolder().continueWithTask(task -> {
                    DriveFolder rootFolder = task.getResult();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(hemisphere)
                            .setMimeType(DriveFolder.MIME_TYPE).setStarred(true).build();
                    return mDriveResourceClient.createFolder(rootFolder, changeSet);
                }).addOnSuccessListener(this, hemisphereFolder -> {
                    folderCache.put(hemisphere, hemisphereFolder);
                    Log.v("New Hemisphere", hemisphereFolder.getDriveId().toString());
                    MetadataChangeSet set0 = new MetadataChangeSet.Builder().setTitle("" + zone)
                            .setMimeType(DriveFolder.MIME_TYPE).setStarred(true).build();
                    mDriveResourceClient.createFolder(hemisphereFolder, set0).addOnSuccessListener(this, zoneFolder -> {
                        folderCache.put(key, zoneFolder);
                        MetadataChangeSet set1 = new MetadataChangeSet.Builder().setTitle("" + easting)
                                .setMimeType(DriveFolder.MIME_TYPE).setStarred(true).build();
                        mDriveResourceClient.createFolder(zoneFolder, set1).addOnSuccessListener(this, eastingFolder -> {
                            folderCache.put(key2, eastingFolder);
                            MetadataChangeSet set2 = new MetadataChangeSet.Builder().setTitle("" + northing)
                                    .setMimeType(DriveFolder.MIME_TYPE).setStarred(true).build();
                            mDriveResourceClient.createFolder(eastingFolder, set2).addOnSuccessListener(this, northingFolder -> {
                                folderCache.put(key3, northingFolder);
                                MetadataChangeSet set3 = new MetadataChangeSet.Builder().setTitle("" + find)
                                        .setMimeType(DriveFolder.MIME_TYPE).setStarred(true).build();
                                mDriveResourceClient.createFolder(northingFolder, set3).addOnSuccessListener(this, findFolder -> {
                                    folderCache.put(key4, findFolder);
                                    Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
                                    createContentsTask.continueWithTask(task -> {
                                        DriveContents contents = task.getResult();
                                        OutputStream outputStream = contents.getOutputStream();
                                        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle("1.png")
                                                .setMimeType("image/png").setStarred(false).build();
                                        CreateFileActivityOptions createOptions = new CreateFileActivityOptions.Builder()
                                                .setInitialDriveContents(contents).setInitialMetadata(changeSet)
                                                .setActivityStartFolder(findFolder.getDriveId()).build();
                                        return mDriveClient.newCreateFileActivityIntentSender(createOptions);
                                    }).addOnSuccessListener(this, intentSender -> {
                                        try
                                        {
                                            startIntentSenderForResult(intentSender, REQUEST_CODE_CREATE_FILE,
                                                    null, 0, 0, 0);
                                        }
                                        catch (IntentSender.SendIntentException e2)
                                        {
                                            Toast.makeText(getApplicationContext(), "Error uploading to Drive",
                                                    Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                });
                            });
                        });
                    });
                });
            }
        });
    }
}
