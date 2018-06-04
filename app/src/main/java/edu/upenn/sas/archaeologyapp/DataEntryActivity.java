package edu.upenn.sas.archaeologyapp;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.coords.UTMCoord;

import static android.R.attr.button;
import static edu.upenn.sas.archaeologyapp.ConstantsAndHelpers.DEFAULT_POSITION_UPDATE_INTERVAL;
import static java.lang.System.currentTimeMillis;

/**
 * The Activity where the user enters all the data
 * Created by eanvith on 01/01/17.
 */

public class DataEntryActivity extends BaseActivity {

    /**
     * Location manager for accessing the users location
     */
    private LocationManager locationManager;

    /**
     * Listener with callbacks to get the users location
     */
    private LocationListener locationListener;

    /**
     * A boolean acting as a toggle for whether or not the app should update
     */
    private boolean liveUpdatePosition = true;

    /**
     * Variables to store the users location data obtained from the Reach
     */
    private Double latitude, longitude, altitude;

    /**
     * Variables to store status of the position fetch
     */
    private String status;

    /**
     * Variables to store the users location data obtained from GPS, as a backup to the Reach data
     */
    private Double GPSlatitude, GPSlongitude, GPSaltitude;

    /**
     * A timestamp of the last time the GPS location variables were updated
     */
    private long lastKnownGPSupdate = System.currentTimeMillis();

    /**
     * Int constant used to determine if GPS permission was granted or denied
     */
    private final int MY_PERMISSION_ACCESS_FINE_LOCATION = 100;

    /**
     * Int constant used to determine if External Storage permission was granted or denied
     */
    private final int MY_PERMISSION_ACCESS_EXTERNAL_STORAGE = 200;


    /**
     * Int constant for activity result from gallery
     */
    private static final int SELECT_IMAGE = 1;

    /**
     * Int constant for activity result from camera
     */
    private static final int CAMERA_REQUEST = 2;

    /**
     * Corresponding strings for status codes in LLH format as defined in the RTKLIB manual v2.4.2, p.102
     */
    private static final String[] STATUS_CODES = {"Error", "Fixed", "Float", "Reserved", "DGPS", "Single"};

    /**
     * Default host for an Emlid Reach position output server
     */
    private static final String DEFAULT_REACH_HOST = "127.0.0.1";

    /**
     * Default port for an Emlid Reach position output server
     */
    private static final String DEFAULT_REACH_PORT = "9001";

    /**
     * The global string for the Emlid Reach host
     */
    private String reachHost = DEFAULT_REACH_HOST;

    /**
     * The global string for the Emlid Reach port
     */
    private String reachPort = DEFAULT_REACH_PORT;


    /**
     * The global variable used for the position update interval
     */
    private int positionUpdateInterval = DEFAULT_POSITION_UPDATE_INTERVAL;

    /**
     * The timer used to periodically update the position
     */
    private Timer positionUpdateTimer;

    /**
     * The shared preferences file name where we will store persistent app data
     */
    public static final String PREFERENCES = "archaeological-survey-location-collector-preferences";

    /**
     * The text views for displaying latitude, longitude, altitude, and status values
     */
    private TextView latitudeTextView, longitudeTextView, altitudeTextView, statusTextView, gridTextView, northingTextView, eastingTextView, sampleTextView;

    /**
     * The image view for displaying the image captured/selected by the user
     */
    private ImageView imageView;

    /**
     * The linear layout for displaying the images captured/selected by the user
     */
    private GridLayout imageContainer;

    /**
     * The paths where the images are saved
     */
    ArrayList<String> photoPaths = new ArrayList<String>();

    /**
     *  The URI of the last image taken with the camera, if one has been taken
     */
    private String lastCameraPictureURI;

    /**
     * The spinner for displaying the dropdown of materials
     */
    Spinner materialsDropdown;

    /**
     * The text box where the user can enter comments
     */
    EditText commentsEditText;



    Socket SOCKETechoSocket;
    PrintWriter SOCKETout;
    BufferedReader SOCKETin;
    BufferedReader SOCKETstdIn;
    private Integer zone;
    private String hemisphere;
    private Integer northing;
    private Integer easting;
    private Integer sample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_entry);

        initialiseViews();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu(menu);
    }



    /**
     * Initialises all the views and other layout components
     */
    private void initialiseViews() {

        // Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_data_entry);
        setSupportActionBar(toolbar);

        // Configure up button to go back to previous activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Load persistent app data from shared preferences
        SharedPreferences settings = getSharedPreferences(PREFERENCES, 0);
        reachHost = settings.getString("reachHost", DEFAULT_REACH_HOST);
        reachPort = settings.getString("reachPort", DEFAULT_REACH_PORT);
        positionUpdateInterval = settings.getInt("positionUpdateInterval", DEFAULT_POSITION_UPDATE_INTERVAL);

        // Get references to the latitude, longitude, altitude, and status text views
        latitudeTextView = (TextView) findViewById(R.id.data_entry_lat_text);
        longitudeTextView = (TextView) findViewById(R.id.data_entry_lng_text);
        altitudeTextView = (TextView) findViewById(R.id.data_entry_alt_text);
        statusTextView = (TextView) findViewById(R.id.data_entry_status_text);
        gridTextView = (TextView) findViewById(R.id.data_entry_grid);
        northingTextView = (TextView) findViewById(R.id.data_entry_northing);
        eastingTextView = (TextView) findViewById(R.id.data_entry_easting);
        sampleTextView = (TextView) findViewById(R.id.data_entry_sample);

        // Get reference to the image container
        imageContainer = (GridLayout) findViewById(R.id.data_entry_image_container);

        // Get reference to the comments edit text
        commentsEditText = (EditText) findViewById(R.id.data_entry_comment_text_view);

        // Initialize the GPS listener
        initiateGPS();

        // Setup the position updater
        positionUpdateTimer = new Timer();
        restartPositionUpdateTimer();

        /**
         * Configure switch handler for update gps switch
         */
        ToggleButton updateGpsButton = (ToggleButton) findViewById(R.id.data_entry_update_gps);
        updateGpsButton.setChecked(true);
        updateGpsButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                liveUpdatePosition = isChecked;
                if (liveUpdatePosition) {
                    resetUTMLocation();
                    initiatePositionFetch();
                } else {
                    // Ensure we only update the sample #, UTM position if the user pressed the button (otherwise this would happen on activity load)
                    if (buttonView.isPressed()) {
                        Log.v("ERROR", "ERROR");
                        setUTMLocation();
                    }
                }
            }

        });

        /**
         * Configure click handler for show GPS location on map button
         */
        findViewById(R.id.data_entry_show_map).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Check if a location is set
                if (latitude != null && longitude != null && altitude != null) {

                    // Open maps with saved latitude and longitude
                    String loc = "geo:0,0?q=" + latitude + "," + longitude + "(" + getString(R.string.location_map_pin_label) + ")" + "&z=16";
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(loc));
                    startActivity(mapIntent);

                } else {

                    // No GPS location currently set, show user message
                    Toast.makeText(DataEntryActivity.this, R.string.location_not_set, Toast.LENGTH_LONG).show();

                }

            }

        });

        /**
         * Configure click handler for opening gallery and allowing the user to select an image
         */
        findViewById(R.id.data_entry_open_gallery).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Create an intent for selecting an image, and start that activity with SELECT_IMAGE requestCode
                Intent chooseIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                chooseIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(chooseIntent, SELECT_IMAGE);
            }

        });

        /**
         * Configure click handler for opening camera and allowing the user to take a picture
         */
        findViewById(R.id.data_entry_open_camera).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Check if user has given permission to write to external storage (required to save image captured by camera app)
                // If not, request for the permission
                if (!(ContextCompat.checkSelfPermission(DataEntryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {

                    ActivityCompat.requestPermissions(DataEntryActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_ACCESS_EXTERNAL_STORAGE);
                    return;

                }

                File outputFromCamera = null;

                try {

                    // Image file to be passed to camera app.
                    // The camera app saves captured image in this file
                    outputFromCamera = createImageFile(true);

                    // Android handles saving data in intents to the camera poorly, so we save the URI of the future image in this variable for future use
                    lastCameraPictureURI = outputFromCamera.getAbsolutePath();

                } catch (Exception e) {

                    e.printStackTrace();

                }

                // Open a camera app to capture images, if available
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    Context context = getApplicationContext();
                    Uri photoURI = FileProvider.getUriForFile(context, context.getPackageName()
                            + ".my.package.name.provider", outputFromCamera);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);

                } else {

                    Toast.makeText(DataEntryActivity.this, R.string.camera_app_not_found, Toast.LENGTH_SHORT).show();

                }

            }

        });

        /**
         * Configure click handler for show GPS location on map button
         */
        findViewById(R.id.data_entry_sample).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!liveUpdatePosition) {

                    setNewSampleNum();

                }
            }

        });

        /**
         * Configure the materials dropdown menu
         */
        materialsDropdown = (Spinner) findViewById(R.id.data_entry_materials_drop_down);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> materialsAdapter = ArrayAdapter.createFromResource(this, R.array.materials_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        materialsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        materialsDropdown.setAdapter(materialsAdapter);

        boolean prepopulatedData = prePopulateFields();

    }


    private void initiateGPS() {
        // Acquire a reference to the system Location Manager
        if (locationManager == null) {

            locationManager = (LocationManager) DataEntryActivity.this.getSystemService(Context.LOCATION_SERVICE);

        }

        // Check if GPS is turned on. If not, prompt the user to turn it on.
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            buildAlertMessageNoGps();
            return;

        }

        // Define a listener that responds to location updates
        if (locationListener == null) {

            locationListener = new LocationListener() {

                public void onLocationChanged(Location location) {

                    // Called when a new location is found by the GPS location provider.
                    updateGPSlocation(location);

                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }

            };

        }

        // Check if the user has granted permission for using GPS
        if (ContextCompat.checkSelfPermission(DataEntryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Ask user for permission
            ActivityCompat.requestPermissions(DataEntryActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);

        } else {

            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }
    }

    private class GetPositionOutputFromReach extends AsyncTask<String, String, String> {
        protected String doInBackground(String... params) {
            String host = params[0];
            Integer port = Integer.parseInt(params[1]);
            String msg = "";

            if (SOCKETechoSocket == null || SOCKETout == null || SOCKETin == null || SOCKETstdIn == null) {
                try {
                    publishProgress("Attempting to connect to the Reach");

                    int timeout = positionUpdateInterval * 1000;
                    SOCKETechoSocket = new Socket();
                    SOCKETechoSocket.setSoTimeout(timeout);
                    SOCKETechoSocket.connect(new InetSocketAddress(host, port), timeout);
                    SOCKETin = new BufferedReader(new InputStreamReader(SOCKETechoSocket.getInputStream()));


                    //SOCKETout = new PrintWriter(SOCKETechoSocket.getOutputStream(), true);
                    //SOCKETstdIn = new BufferedReader(new InputStreamReader(System.in));
                } catch(Exception e) {
                    //e.printStackTrace();
                    return msg;
                }
            }
            try {
                Log.e("##################", "Attempting connection to Reach :: "+reachHost+" :: "+reachPort);
                String currentLine;
                while ((currentLine = SOCKETin.readLine()) != null) {
                    msg = currentLine;
                }
                /*
                while ((userInput = SOCKETstdIn.readLine()) != null || msg.equals("")) {
                    Log.v("reading...", "readLine");
                    String readLine = SOCKETin.readLine();
                    Log.v("DONE", "readLine");
                    msg += readLine;
                    Log.v("#######################", readLine);
                }
                */
            } catch (Exception e) {
                //Toast.makeText(DataEntryActivity.this, "Failed to connect to socket: "+e, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return msg;
        }

        protected void onProgressUpdate(String... progress) {
            //Toast.makeText(DataEntryActivity.this, "Attempting to connect to the Reach.", Toast.LENGTH_SHORT).show();
        }

        protected void onPostExecute(String result) {
            String[] parsed = result.split("\\s+");
            if (parsed.length < 15) {
                initiateGpsFetch();
            } else {
                double lat = Double.parseDouble(parsed[2]);
                double lon = Double.parseDouble(parsed[3]);
                double height = Double.parseDouble(parsed[4]);
                String status = STATUS_CODES[Integer.parseInt(parsed[5])];
                setLocationDetails(lat, lon, height, status);
            }
        }
    }


    /**
     * This function initiates fetching data from an Emlid Reach output server
     */
    private void initiatePositionFetch() {

        new GetPositionOutputFromReach().execute(reachHost, reachPort);

    }


    /**
     * This function contains the flow for fetching data from GPS
     */
    private void initiateGpsFetch() {

        long timeSinceLastGPSupdate = currentTimeMillis() - lastKnownGPSupdate;
        if (false) { //timeSinceLastGPSupdate > GPS_TIME_OUT) {
            setLocationDetailsFromLastKnownGPSlocation();
        } else {
            if (GPSlatitude != null && GPSlongitude != null && GPSlatitude != null) {
                setLocationDetails(GPSlatitude, GPSlongitude, GPSaltitude, "GPS");
            }
        }

    }


    /**
     * Check if any parameters were passed to this activity, and pre populate the data if required
     * @return True if data was pre populated, false otherwise.
     */
    private boolean prePopulateFields() {

        String id = getIntent().getStringExtra(ConstantsAndHelpers.PARAM_KEY_ID);

        // If null, it means nothing was passed
        if (id == null) {

            return false;

        }

        // Populate the UTM position details, if present
        zone = getIntent().getIntExtra(ConstantsAndHelpers.PARAM_KEY_ZONE, Integer.MIN_VALUE);
        hemisphere = getIntent().getStringExtra(ConstantsAndHelpers.PARAM_KEY_HEMISPHERE);
        northing = getIntent().getIntExtra(ConstantsAndHelpers.PARAM_KEY_NORTHING, Integer.MIN_VALUE);
        easting = getIntent().getIntExtra(ConstantsAndHelpers.PARAM_KEY_EASTING, Integer.MIN_VALUE);
        sample = getIntent().getIntExtra(ConstantsAndHelpers.PARAM_KEY_SAMPLE, Integer.MIN_VALUE);
        if (!(zone == Integer.MIN_VALUE || hemisphere == null || northing == Integer.MIN_VALUE || easting == Integer.MIN_VALUE || sample == Integer.MIN_VALUE)) {
            gridTextView.setText(zone + hemisphere);
            northingTextView.setText(String.valueOf(northing));
            eastingTextView.setText(String.valueOf(easting));
            sampleTextView.setText(String.valueOf(sample));
        } else {
            resetUTMLocation();
        }


        // Populate the latitude, longitude and altitude, if present
        double _latitude = getIntent().getDoubleExtra(ConstantsAndHelpers.PARAM_KEY_LATITUDE, Double.MIN_VALUE);
        double _longitude = getIntent().getDoubleExtra(ConstantsAndHelpers.PARAM_KEY_LONGITUDE, Double.MIN_VALUE);
        double _altitude = getIntent().getDoubleExtra(ConstantsAndHelpers.PARAM_KEY_ALTITUDE, Double.MIN_VALUE);
        String _status = getIntent().getStringExtra(ConstantsAndHelpers.PARAM_KEY_STATUS);
        if (!(_latitude == Double.MIN_VALUE || _longitude == Double.MIN_VALUE || _altitude == Double.MIN_VALUE || _status == null)) {

            setLocationDetails(_latitude, _longitude, _altitude, _status);

        }

        // Pause the coordinate fetches
        liveUpdatePosition = false;
        ToggleButton updateGpsButton = (ToggleButton) findViewById(R.id.data_entry_update_gps);
        updateGpsButton.setChecked(false);


        // Populate the image, if present
        ArrayList<String> _paths = getIntent().getStringArrayListExtra(ConstantsAndHelpers.PARAM_KEY_IMAGES);

        if(!_paths.isEmpty()) {

            photoPaths = _paths;
            populateImagesWithPhotoPaths();

        }

        // Populate the material, if present
        String _material = getIntent().getStringExtra(ConstantsAndHelpers.PARAM_KEY_MATERIAL);

        if(_material != null) {

            // Search the dropdown for a matching material, and set to that if found
            // TODO: See ISSUE #6 on github
            for (int i = 0; i < materialsDropdown.getCount(); i++) {

                if (materialsDropdown.getItemAtPosition(i).toString().equalsIgnoreCase(_material)) {

                    materialsDropdown.setSelection(i);

                }

            }

        }

        // Populate the comments, if present
        String _comments = getIntent().getStringExtra(ConstantsAndHelpers.PARAM_KEY_COMMENTS);

        if(_comments != null) {

            commentsEditText.setText(_comments);

        }

        return true;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check if the user action was success
        if (resultCode == Activity.RESULT_OK) {

            try {

                // If user selected image from gallery
                if (requestCode == SELECT_IMAGE) {

                    // Get the multiples images returned within a ClipData object
                    ClipData imageData = data.getClipData();
                    // Define a bitmap arraylist to display later
                    ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();

                    if (imageData != null) {
                        for (int i = 0; i < imageData.getItemCount(); i++) {

                            ClipData.Item image = imageData.getItemAt(i);
                            Uri uri = image.getUri();

                            // Add this image to the bitmap arraylist
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            bitmapArray.add(bitmap);

                            // Try saving this bitmap
                            if(!saveToFile(bitmap)) {
                                throw new Exception(getString(R.string.save_failed_exception));
                            }
                        }
                    }

                    /*
                    // Get the path of the image selected by the user
                    Uri selectedImageUri = data.getData();

                    // Read it into a BITMAP
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);

                    // Try saving the image
                    if(!saveToFile(bitmap)) {
                        throw new Exception(getString(R.string.save_failed_exception));
                    }

                    // Display the image selected
                    imageView.setImageURI(Uri.fromFile(new File(photoPath)));
                    */

                    // Display the selected images
                    populateImagesWithPhotoPaths();

                }
                // If user captured image with camera
                else if (requestCode == CAMERA_REQUEST) {

                    // Read the captured image into BITMAP
                    Context context = getApplicationContext();
                    Uri photoURI = FileProvider.getUriForFile(context, context.getPackageName()
                            + ".my.package.name.provider", createImageFile(false));
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                    Log.v("Camera", bitmap.toString());
                    // Try saving the image
                    if(!saveToFile(bitmap)) {
                        throw new Exception(getString(R.string.save_failed_exception));
                    }

                    // Display the image captured (and don't clear the current photos)
                    //imageView.setImageURI(Uri.fromFile(new File(photoPath)));
                    populateImagesWithPhotoPaths();

                }

            } catch (Exception ex) {

                ex.printStackTrace();
                Toast.makeText(this, R.string.save_failed_message, Toast.LENGTH_SHORT).show();

            }

        }

    }

    private void populateImagesWithPhotoPaths() {
        // Empty the current photos
        imageContainer.removeAllViews();

        // Populate the appropriate photos
        for(int i=0; i < photoPaths.size(); i++) {
            final ImageView image = new ImageView(this);

            final String img = photoPaths.get(i);
            image.setLayoutParams(new GridView.LayoutParams(185, 185));
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setPadding(5, 5, 5, 5);
            image.setScaleType(ImageView.ScaleType.MATRIX);
            image.setImageURI(Uri.fromFile(new File(photoPaths.get(i))));
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to delete this picture?")
                    .setCancelable(true)
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                        public void onClick(final DialogInterface dialog, final int id) {
                            imageContainer.removeView(image);
                            photoPaths.remove(img);
                            dialog.cancel();
                        }

                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });

            final AlertDialog alert = builder.create();
            image.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vb.vibrate(30);
                    alert.show();
                    return true;
                }
            });

            // Adds the view to the layout
            imageContainer.addView(image);
        }
    }

    /**
     * Function to save bitmap to a file and store the path in photoPath
     * @param bmp The BITMAP to be saved
     * @return true if saved, false otherwise
     */
    boolean saveToFile(Bitmap bmp) {

        FileOutputStream out = null;

        try {

            // Create an image file where the BITMAP will be saved
            File outputImageFile = createImageFile(false);

            // Resize and write the image to the file using JPEG compression
            out = new FileOutputStream(outputImageFile, false);
            bmp = resize(bmp, 1800, 1200);
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, out);

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        } finally {

            try {

                // Close the output stream to the file, if opened
                if (out != null) {

                    out.close();

                }

            } catch (IOException e) {

                e.printStackTrace();
                return false;

            }

        }

        return true;

    }

    /**
     * Reduce the size of image if it is greater than maxWidth and maxHeight
     * @param image The image to be resized
     * @param maxWidth Max allowed width of image
     * @param maxHeight Max allowed height of image
     * @return Resized image
     */
    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {

        // Check if image is at least 1x1 px
        if (maxHeight > 0 && maxWidth > 0) {

            float actualWidth = image.getWidth();
            float actualHeight = image.getHeight();
            float imgRatio = (float) actualWidth / (float) actualHeight;
            float maxRatio = (float) maxWidth / (float) maxHeight;

            // Resize only if image is bigger than maxHeight or maxWidth
            if (actualHeight > maxHeight || actualWidth > maxWidth) {

                // Calculate dimensions of resized image
                if(imgRatio < maxRatio) {

                    //adjust width according to maxHeight
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = imgRatio * actualWidth;
                    actualHeight = maxHeight;

                } else if(imgRatio > maxRatio) {

                    //adjust height according to maxWidth
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = imgRatio * actualHeight;
                    actualWidth = maxWidth;

                } else {

                    actualHeight = maxHeight;
                    actualWidth = maxWidth;

                }

                // Create the resized image
                image = Bitmap.createScaledBitmap(image, (int)actualWidth, (int)actualHeight, true);

            }

            return image;

        } else {

            return image;

        }

    }

    /**
     * Create an image file using the current timestamp
     * @param forCamera True if this file is being created to be passed to camera activity
     * @return
     * @throws IOException
     */
    private File createImageFile(boolean forCamera) throws IOException {

        // Create an image file name using timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        Log.v("Camera", imageFileName);
        // Use the apps storage
        File storageDir = new File(Environment.getExternalStorageDirectory().toString());
        Log.v("Camera", storageDir.getAbsolutePath());
        // If the file is being created to be sent to camera app, we need to use external storage
        // as the app storage returned above is private to the app and the camera will not be able to
        // access it
        if (forCamera) {

            storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            Log.v("Camera", storageDir.getAbsolutePath());
        }

        // Create the image file with required name, extension type and storage path
        File image = null;

        try {

            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            Log.v("Camera", image.getAbsolutePath());
        } catch (Exception e) {

            e.printStackTrace();

        }

        // Save the path if we are saving this from the gallery (and not the camera)
        if (!forCamera) {
            photoPaths.add(image.getAbsolutePath());
        }

        return image;

    }

    private void setUTMLocation() {
        if (latitude != null && longitude != null) {
            DataBaseHandler dataBaseHandler = new DataBaseHandler(this);

            Angle lat = Angle.fromDegrees(latitude);

            Angle lon = Angle.fromDegrees(longitude);

            UTMCoord UTMposition = UTMCoord.fromLatLon(lat, lon);

            zone = UTMposition.getZone();
            hemisphere = UTMposition.getHemisphere().contains("North") ? "N" : "S";
            northing = new Integer((int) Math.floor(UTMposition.getNorthing()));
            easting = new Integer((int) Math.floor(UTMposition.getEasting()));
            sample = dataBaseHandler.getLastSampleFromBucket(zone, hemisphere, northing, easting) + 1;

            gridTextView.setText(zone + hemisphere);
            northingTextView.setText(String.valueOf(northing));
            eastingTextView.setText(String.valueOf(easting));
            sampleTextView.setText(String.valueOf(sample));
        }
    }

    private void resetUTMLocation() {
        gridTextView.setText(R.string.blank_assignment);
        northingTextView.setText(R.string.blank_assignment);
        eastingTextView.setText(R.string.blank_assignment);
        sampleTextView.setText(R.string.blank_assignment);
    }

    /**
     * Set the required variables and text views once we get location from previous activity
     * @param _latitude The latitude to be set
     * @param _longitude The longitude to be set
     * @param _altitude The altitude to be set
     */
    private void setLocationDetails(double _latitude, double _longitude, double _altitude, String _status) {

        // Get the latitude, longitutde, altitude, and save it in the respective variables
        longitude = _longitude;
        latitude = _latitude;
        altitude = _altitude;
        status = _status;

        // Update the text views
        latitudeTextView.setText(String.format(getResources().getString(R.string.latitude), latitude));
        longitudeTextView.setText(String.format(getResources().getString(R.string.longitude), longitude));
        altitudeTextView.setText(String.format(getResources().getString(R.string.altitude), altitude));
        statusTextView.setText(String.format(getResources().getString(R.string.status), status));
    }

    /**
     * Set the required variables and text views once we get the users location
     * @param location The location to be used
     */
    private void setLocationDetails(Location location, String _status) {

        // Get the latitude, longitutde, altitude, and save it in the respective variables
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        altitude = location.getAltitude();
        status = _status;

        // Update the text views
        latitudeTextView.setText(String.format(getResources().getString(R.string.latitude), latitude));
        longitudeTextView.setText(String.format(getResources().getString(R.string.longitude), longitude));
        altitudeTextView.setText(String.format(getResources().getString(R.string.altitude), altitude));
        statusTextView.setText(String.format(getResources().getString(R.string.status), status));
    }

    /**
     * Set the required variables and text views from the last known GPS location
     */
    private void setLocationDetailsFromLastKnownGPSlocation() {
        try {

            // Get last known location
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // Stop listening to GPS updates
            locationManager.removeUpdates(locationListener);

            // Call function to set location details
            setLocationDetails(location, "GPS (old)");

            // Do not fallback to previous location, as per Github Issue 8
            Toast.makeText(DataEntryActivity.this, R.string.location_not_found, Toast.LENGTH_LONG).show();

        } catch (SecurityException e) {

            /*
             * We shouldn't use a Toast here -- having a toast will cause a "Screen overlay detected" error and won't let the user grant the app location permission
             */
            //Toast.makeText(DataEntryActivity.this, R.string.location_permission_denied_prompt, Toast.LENGTH_LONG).show();

        } catch (Exception e) {

            //Toast.makeText(DataEntryActivity.this, R.string.location_not_found, Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
    }

    /**
     * Update the GPS location details from the GPS stream, but don't update the items position data
     * @param location The location to be used
     */
    private void updateGPSlocation(Location location) {

        // Get the latitude, longitutde, altitude, and save it in the respective variables
        GPSlongitude = location.getLongitude();
        GPSlatitude = location.getLatitude();
        GPSaltitude = location.getAltitude();

        lastKnownGPSupdate = System.currentTimeMillis();
    }

    /**
     * Restart the position update timer using the global interval variable positionUpdateInterval
     */
    private void restartPositionUpdateTimer() {
        positionUpdateTimer.cancel();
        positionUpdateTimer = new Timer();
        positionUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (liveUpdatePosition) {
                    initiatePositionFetch();
                }
            }
        }, 0, positionUpdateInterval * 1000);
    }


    /**
     * Build and show alert dialog to the user, requesting him to turn GPS on
     */
    private void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.gps_enable_alert_box)
                .setCancelable(false)
                .setPositiveButton(R.string.enable_gps_alert_positive_button, new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, final int id) {

                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        dialog.cancel();

                    }

                })
                .setNegativeButton(R.string.enable_gps_alert_negative_button, new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, final int id) {

                        // Show a toast to the user requesting that he allows permission for GPS use
                        Toast.makeText(DataEntryActivity.this, R.string.location_permission_denied_prompt, Toast.LENGTH_LONG).show();
                        dialog.cancel();

                    }

                });

        final AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {

            case MY_PERMISSION_ACCESS_FINE_LOCATION: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted by user
                    initiateGPS();

                } else {

                    // Comes here if permission for GPS was denied by user
                    // Show a toast to the user requesting that he allows permission for GPS use
                    Toast.makeText(this, R.string.location_permission_denied_prompt, Toast.LENGTH_LONG).show();

                }

                break;

            }
            case MY_PERMISSION_ACCESS_EXTERNAL_STORAGE : {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted by user
                    Toast.makeText(this, R.string.external_storage_permission_granted_prompt, Toast.LENGTH_LONG).show();

                } else {

                    // Comes here if permission for GPS was denied by user
                    // Show a toast to the user requesting that he allows permission for GPS use
                    Toast.makeText(this, R.string.external_storage_permission_denied_prompt, Toast.LENGTH_LONG).show();

                }

                break;

            }

        }

    }

    @Override
    protected void onPause() {

        super.onPause();

        // Stop listening to GPS, if still listening
        try {

            locationManager.removeUpdates(locationListener);

        } catch (SecurityException e) {

            //Toast.makeText(DataEntryActivity.this, R.string.location_permission_denied_prompt, Toast.LENGTH_LONG).show();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    @Override
    protected void onResume() {

        super.onResume();

        // initiate GPS again
        initiateGPS();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                // Save data when back arrow on action bar is pressed
                saveData();
                return false;

            case R.id.settings:

                // Create and open a settings menu dialog box
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Set Emlid Reach host & port");


                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText hostBox = new EditText(this);
                hostBox.setHint("Host");
                hostBox.setSingleLine();
                layout.addView(hostBox);
                hostBox.setText(reachHost, TextView.BufferType.EDITABLE);

                final EditText portBox = new EditText(this);
                portBox.setHint("Port");
                portBox.setSingleLine();
                layout.addView(portBox);
                portBox.setText(reachPort, TextView.BufferType.EDITABLE);

                final EditText intervalBox = new EditText(this);
                intervalBox.setHint("Position update interval (seconds)");
                intervalBox.setSingleLine();
                layout.addView(intervalBox);
                intervalBox.setText(String.valueOf(positionUpdateInterval), TextView.BufferType.EDITABLE);

                dialog.setCancelable(true);

                dialog.setPositiveButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                dialog.setNegativeButton(
                        "Confirm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // Set the new host and port
                                reachHost = hostBox.getText().toString();
                                reachPort = portBox.getText().toString();
                                positionUpdateInterval = Integer.parseInt(intervalBox.getText().toString());

                                // Reset the socket connection
                                SOCKETechoSocket = null;

                                // Save these new values to shared preferences
                                SharedPreferences settings = getSharedPreferences(PREFERENCES, 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("reachHost", reachHost);
                                editor.putString("reachPort", reachPort);
                                editor.putInt("positionUpdateInterval", positionUpdateInterval);
                                editor.commit();

                                // Attempt to connect, restart the timer
                                restartPositionUpdateTimer();

                                // Close the dialog
                                dialog.cancel();
                            }
                        });

                dialog.setView(layout);
                dialog.show();

                return false;

        }

        return true;

    }

    public void submitButtonPressed(View v) {

        onBackPressed();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

        // Save data when back button on action bar is pressed
        saveData();

        // Disconnect from GPS

    }

    private void setNewSampleNum() {
        // Create and open a settings menu dialog box
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Set new sample #");


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText sampleBox = new EditText(this);
        sampleBox.setHint("Sample");
        sampleBox.setSingleLine();
        layout.addView(sampleBox);
        sampleBox.setText(String.valueOf(sample), TextView.BufferType.EDITABLE);

        dialog.setCancelable(true);

        dialog.setPositiveButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        dialog.setNegativeButton(
                "Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // Set the new sample #
                        sample = Integer.valueOf(sampleBox.getText().toString());
                        sampleTextView.setText(String.valueOf(sample));

                        // Close the dialog
                        dialog.cancel();
                    }
                });

        dialog.setView(layout);
        dialog.show();
    }

    /**
     * Save all the data entered in the form
     */
    private void saveData() {

        // Get uuid from intent extras if this activity was opened for existing bucket entry
        String id = getIntent().getStringExtra(ConstantsAndHelpers.PARAM_KEY_ID);

        // If this is a new entry, check and generate a new uuid
        if (id == null) {

            id = UUID.randomUUID().toString();

        }

        // We only save changes if location info and image are present
        // Check if location info is set
        if (zone == null || hemisphere == null || northing == null || sample == null || easting == null || latitude == null || longitude == null) {

            Toast.makeText(DataEntryActivity.this, "You did not establish a fixed location. Please try again.", Toast.LENGTH_LONG).show();
            return;

        }

        // Check if at least one image is set
        if (photoPaths.isEmpty()) {

            Toast.makeText(DataEntryActivity.this, "You need to include pictures to add a new item.", Toast.LENGTH_SHORT).show();
            return;

        }

        // Get the material and the comment
        String material = materialsDropdown.getSelectedItem().toString();
        String comment = commentsEditText.getText().toString();

        // Todo: Create a data entry element
        DataEntryElement list[] = new DataEntryElement[1];
        list[0] = new DataEntryElement(id, latitude, longitude, altitude, status, photoPaths, material, comment, (new Date()).getTime(), (new Date()).getTime(), zone, hemisphere, northing, easting, sample, false);

        // Save the dataEntryElement to DB
        DataBaseHandler dataBaseHandler = new DataBaseHandler(this);
        dataBaseHandler.addFindsRows(list);

    }

    /**
     * This method creates a tag with which we will name photos (plus a unique ID appended to the end)
     * based on the bucket's coordinates
     * @param latitude the latitude of the entry in this bucket
     * @param longitude the longitude of the entry in this bucket
     */
    /*
    private String createImageTag(Double latitude, Double longitude) {

        // Create ~meter level precise coordinates (so each entry has format bucket.uniqueID)
        // Reducing lat and long to 5 decimal places creates ~1.1132 meter blocks for each bucket at the equator, which gets smaller the further from the equator you go
        Double bucketLat = BigDecimal.valueOf(latitude).setScale(5, RoundingMode.HALF_UP).doubleValue();
        Double bucketLon = BigDecimal.valueOf(longitude).setScale(5, RoundingMode.HALF_UP).doubleValue();

        DataBaseHandler dataBaseHandler = new DataBaseHandler(this);

        return;
    }
    */

}
