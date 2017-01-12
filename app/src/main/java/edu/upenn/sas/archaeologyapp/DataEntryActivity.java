package edu.upenn.sas.archaeologyapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
     * Variables to store the users location
     */
    private double latitude, longitude;

    /**
     * This variable is set to true once a GPS location is got and saved in above variables
     */
    private boolean isLocationSet = false;

    /**
     * This flag is used to determine if the users location was fetched by the location listener
     */
    private boolean locationUpdated = false;

    /**
     * Int constant used to determine if GPS permission was granted or denied
     */
    private final int MY_PERMISSION_ACCESS_FINE_LOCATION = 100;

    /**
     * The text views for displaying latitude and longitude values
     */
    private TextView latitudeTextView, longitudeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_entry);

        initialiseViews();

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

        // Get references to the latitude and longitude text views
        latitudeTextView = (TextView) findViewById(R.id.data_entry_lat_text);
        longitudeTextView = (TextView) findViewById(R.id.data_entry_lng_text);

        // Configure click handler for update gps button
        findViewById(R.id.data_entry_update_gps).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Acquire a reference to the system Location Manager
                if (locationManager == null) {

                    locationManager = (LocationManager) DataEntryActivity.this.getSystemService(Context.LOCATION_SERVICE);

                }

                // Check if GPS is turned on. If not, prompt the user to turn it on.
                if ( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {

                    buildAlertMessageNoGps();
                    return;

                }

                // Define a listener that responds to location updates
                if (locationListener == null) {

                    locationListener = new LocationListener() {

                        public void onLocationChanged(Location location) {

                            // Called when a new location is found by the GPS location provider.
                            setLocationDetails(location);

                            try {

                                // Stop listening to the GPS
                                locationManager.removeUpdates(locationListener);

                            } catch (SecurityException e) {

                                // Show error message if permissions are missing
                                Toast.makeText(DataEntryActivity.this, R.string.location_permission_denied_prompt, Toast.LENGTH_LONG).show();

                            }

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
                if ( ContextCompat.checkSelfPermission( DataEntryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

                    // Ask user for permission
                    ActivityCompat.requestPermissions( DataEntryActivity.this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  }, MY_PERMISSION_ACCESS_FINE_LOCATION );

                } else {

                    // Set flag to false. This flag is set to true inside location listener when user location is got.
                    locationUpdated = false;

                    // Register the listener with the Location Manager to receive location updates
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Toast.makeText(DataEntryActivity.this, R.string.retrieving_location, Toast.LENGTH_LONG).show();

                    // Wait for specified time and check if we got a location. If not, use last known location.
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            // Check if locationUpdated flag is still false. If it is still false, it means
                            // users location still not determined. Try using previous location.
                            if (!locationUpdated) {

                                try {

                                    // Get last known location
                                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                    // Stop listening to GPS updates
                                    locationManager.removeUpdates(locationListener);

                                    // Call function to set location details
                                    setLocationDetails(location);

                                } catch (SecurityException e) {

                                    Toast.makeText(DataEntryActivity.this, R.string.location_permission_denied_prompt, Toast.LENGTH_LONG).show();

                                } catch (Exception e) {

                                    Toast.makeText(DataEntryActivity.this, R.string.location_not_found, Toast.LENGTH_LONG).show();
                                    e.printStackTrace();

                                }

                            }

                        }
                    }, ConstantsAndHelpers.GPS_TIME_OUT);

                }

            }

        });

        // Configure click handler for show GPS location on map button
        findViewById(R.id.data_entry_show_map).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Check if a location is set
                if (isLocationSet) {

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

    }

    /**
     * Set the required variables and text views once we get the users location
     * @param location The location to be used
     */
    private void setLocationDetails(Location location) {

        // Get the latitude and longitutde and save it in the respective variables
        longitude = location.getLongitude();
        latitude = location.getLatitude();

        // Update the text views
        latitudeTextView.setText(String.format(getResources().getString(R.string.latitude), latitude));
        longitudeTextView.setText(String.format(getResources().getString(R.string.longitude), longitude));

        // Update the flags
        isLocationSet = true;
        locationUpdated = true;

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
                    Toast.makeText(this, R.string.location_permission_granted_prompt, Toast.LENGTH_LONG).show();

                } else {

                    // Comes here if permission for GPS was denied by user
                    // Show a toast to the user requesting that he allows permission for GPS use
                    Toast.makeText(this, R.string.location_permission_denied_prompt, Toast.LENGTH_LONG).show();

                }

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

            Toast.makeText(DataEntryActivity.this, R.string.location_permission_denied_prompt, Toast.LENGTH_LONG).show();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }
}
