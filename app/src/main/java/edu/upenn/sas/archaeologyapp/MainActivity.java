package edu.upenn.sas.archaeologyapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Timer;
import java.util.TimerTask;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.coords.UTMCoord;

import static edu.upenn.sas.archaeologyapp.ConstantsAndHelpers.DEFAULT_POSITION_UPDATE_INTERVAL;
import static edu.upenn.sas.archaeologyapp.R.id.map;
import static edu.upenn.sas.archaeologyapp.R.string.latitude;
import static edu.upenn.sas.archaeologyapp.R.string.longitude;

/**
 * This activity shows the user the list of items presently in his bucket
 * Created by eanvith on 30/12/16.
 */

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    /**
     * Reference to the list view
     */
    ListView listView;

    /**
     * Reference to the list entry adapter
     */
    BucketListEntryAdapter listEntryAdapter;

    /**
     * Reference to the swipe refresh layout
     */
    SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Reference to the Google map
     */
    GoogleMap googleMap;
    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;

    /**
     * Location manager for accessing the users location
     */
    private LocationManager locationManager;

    /**
     * Listener with callbacks to get the users location
     */
    private LocationListener locationListener;

    /**
     * Variables to store the users location data obtained from GPS, as a backup to the Reach data
     */
    private Double GPSlatitude, GPSlongitude, GPSaltitude;

    /**
     * Int constant used to determine if GPS permission was granted or denied
     */
    private final int MY_PERMISSION_ACCESS_FINE_LOCATION = 100;

    /**
     * Int constant used to determine if External Storage permission was granted or denied
     */
    private final int MY_PERMISSION_ACCESS_EXTERNAL_STORAGE = 200;

    /**
     * The global variable used for the position update interval
     */
    private int positionUpdateInterval = ConstantsAndHelpers.DEFAULT_POSITION_UPDATE_INTERVAL;

    /**
     * The timer used to periodically update the position
     */
    private Timer positionUpdateTimer;

    /**
     * The text views for displaying latitude, longitude, altitude, and status values
     */
    private TextView gridTextView, northingTextView, eastingTextView;

    private Integer zone;
    private String hemisphere;
    private Integer northing;
    private Integer easting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseViews();
        initiateGPS();

        gridTextView = (TextView) findViewById(R.id.data_entry_grid);
        northingTextView = (TextView) findViewById(R.id.data_entry_northing);
        eastingTextView = (TextView) findViewById(R.id.data_entry_easting);

        resetUTMLocation();

        // Setup the position updater
        positionUpdateTimer = new Timer();
        restartPositionUpdateTimer();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap m) {
                googleMap = m;
                listEntryAdapter.setMap(m);
                m.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                    @Override
                    public void onCameraMoveStarted(int i) {
                        disableSwipeRefresh();
                    }
                });
                googleMap.setOnCameraMoveCanceledListener(new GoogleMap.OnCameraMoveCanceledListener() {
                    @Override
                    public void onCameraMoveCanceled() {
                        enableSwipeRefresh();
                    }
                });
                populateDataFromLocalStore();
            }
        });

    }

    /**
     * Initialises all the views and other layout components
     */
    private void initialiseViews() {

        // Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // Configure the new action button to handle clicks
        findViewById(R.id.fab_new).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Open DataEntryActivity to create a new entry
                MainActivity.super.startActivityUsingIntent(DataEntryActivity.class, false);

            }

        });

        // Configure the settings action button to handle clicks
        findViewById(R.id.fab_sync).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                MainActivity.this.startActivityUsingIntent(SyncActivity.class, false);

            }

        });

        // Store references to the list and list entry
        listView = (ListView) findViewById(R.id.main_activity_list_view);
        listEntryAdapter = new BucketListEntryAdapter(this, R.layout.bucket_list_entry);
        listView.setAdapter(listEntryAdapter);

        // Configure the list items to handle clicks
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // Open the data entry activity with fields pre-populated
                DataEntryElement dataEntryElement = listEntryAdapter.getItem(position);

                Bundle paramsToPass = new Bundle();
                paramsToPass.putString(ConstantsAndHelpers.PARAM_KEY_ID, dataEntryElement.getID());
                paramsToPass.putInt(ConstantsAndHelpers.PARAM_KEY_ZONE, dataEntryElement.getZone());
                paramsToPass.putString(ConstantsAndHelpers.PARAM_KEY_HEMISPHERE, dataEntryElement.getHemisphere());
                paramsToPass.putInt(ConstantsAndHelpers.PARAM_KEY_NORTHING, dataEntryElement.getNorthing());
                paramsToPass.putInt(ConstantsAndHelpers.PARAM_KEY_EASTING, dataEntryElement.getEasting());
                paramsToPass.putInt(ConstantsAndHelpers.PARAM_KEY_SAMPLE, dataEntryElement.getSample());
                paramsToPass.putDouble(ConstantsAndHelpers.PARAM_KEY_LATITUDE, dataEntryElement.getLatitude());
                paramsToPass.putDouble(ConstantsAndHelpers.PARAM_KEY_LONGITUDE, dataEntryElement.getLongitude());
                paramsToPass.putDouble(ConstantsAndHelpers.PARAM_KEY_ALTITUDE, dataEntryElement.getAltitude());
                paramsToPass.putString(ConstantsAndHelpers.PARAM_KEY_STATUS, dataEntryElement.getStatus());
                paramsToPass.putStringArrayList(ConstantsAndHelpers.PARAM_KEY_IMAGES, dataEntryElement.getImagePaths());
                paramsToPass.putString(ConstantsAndHelpers.PARAM_KEY_MATERIAL, dataEntryElement.getMaterial());
                paramsToPass.putString(ConstantsAndHelpers.PARAM_KEY_COMMENTS, dataEntryElement.getComments());

                startActivityUsingIntent(DataEntryActivity.class, false, paramsToPass);

            }

        });

        // Get a reference for the swipe layout and set the listener
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_activity_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        // Populate the list with data from DB
        populateDataFromLocalStore();

    }

    private void initiateGPS() {
        // Acquire a reference to the system Location Manager
        if (locationManager == null) {

            locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

        }

        // Check if GPS is turned on. If not, prompt the user to turn it on.
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Toast.makeText(MainActivity.this, "This app needs GPS on to work. Please turn it on and restart the app.", Toast.LENGTH_SHORT).show();
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
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Ask user for permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);

        } else {

            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }
    }

    /**
     * This function is called when the user swipes down to refresh the list
     */
    @Override
    public void onRefresh() {

        // Populate list with data from DB
        swipeRefreshLayout.setRefreshing(true);
        populateDataFromLocalStore();
        swipeRefreshLayout.setRefreshing(false);

    }

    /**
     * Function to populate the list with data available locally
     */
    private void populateDataFromLocalStore() {

        // Get data from DB
        DataBaseHandler dataBaseHandler = new DataBaseHandler(this);
        int numrows = dataBaseHandler.getRows().size();

        // Populate map markers
        if (googleMap != null) {
            googleMap.clear();
            for (DataEntryElement elem : dataBaseHandler.getRows()) {
                String id = elem.getZone()+"."+elem.getHemisphere()+"."+elem.getNorthing()+"."+elem.getEasting()+"."+elem.getSample();
                googleMap.addMarker(new MarkerOptions().position(new LatLng(elem.getLatitude(), elem.getLongitude())).title(id));
            }

            // Set map center to last placed marker
            if (numrows > 0) {
                DataEntryElement lastElem = dataBaseHandler.getRows().get(numrows - 1);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastElem.getLatitude(), lastElem.getLongitude()), 14));
            }
        }

        // Clear list and populate with data got from DB
        listEntryAdapter.clear();
        listEntryAdapter.addAll(dataBaseHandler.getUnsyncedRows());
        listEntryAdapter.notifyDataSetChanged();

    }

    private void enableSwipeRefresh() {
        swipeRefreshLayout.setEnabled(true);
    }

    private void disableSwipeRefresh() {
        swipeRefreshLayout.setEnabled(false);
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

    }

    private void setUTMLocation() {
        if (GPSlatitude != null && GPSlongitude != null) {
            DataBaseHandler dataBaseHandler = new DataBaseHandler(this);

            Angle lat = Angle.fromDegrees(GPSlatitude);

            Angle lon = Angle.fromDegrees(GPSlongitude);

            UTMCoord UTMposition = UTMCoord.fromLatLon(lat, lon);

            zone = UTMposition.getZone();
            hemisphere = UTMposition.getHemisphere().contains("North") ? "N" : "S";
            northing = new Integer((int) Math.floor(UTMposition.getNorthing()));
            easting = new Integer((int) Math.floor(UTMposition.getEasting()));

            gridTextView.setText(zone + hemisphere);
            northingTextView.setText(String.valueOf(northing));
            eastingTextView.setText(String.valueOf(easting));
        }
    }

    private void resetUTMLocation() {
        gridTextView.setText(R.string.blank_id);
        northingTextView.setText(R.string.blank_id);
        eastingTextView.setText(R.string.blank_id);
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
                // Need to run setUTMLocation() on an UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setUTMLocation();
                    }
                });
            }
        }, 0, positionUpdateInterval * 1000);
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
            case MY_PERMISSION_ACCESS_EXTERNAL_STORAGE: {

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
    protected void onResume() {

        super.onResume();

        populateDataFromLocalStore();

    }

}
