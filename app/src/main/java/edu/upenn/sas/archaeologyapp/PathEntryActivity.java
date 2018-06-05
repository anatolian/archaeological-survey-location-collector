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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView;


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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.coords.UTMCoord;

import static android.R.attr.button;
import static android.R.attr.start;
import static edu.upenn.sas.archaeologyapp.ConstantsAndHelpers.*;
import static java.lang.System.currentTimeMillis;

/**
 * The Activity where the user enters all the data
 * Created by colinrob on 6/3/18.
 */

public class PathEntryActivity extends BaseActivity {

    /**
     * The shared preferences file name where we will store persistent app data
     */
    public static final String PREFERENCES = "archaeological-survey-location-collector-preferences";

    /**
     * The text views for displaying latitude, longitude, altitude, and status values
     */
    private TextView beginLatitudeTextView, beginLongitudeTextView, beginAltitudeTextView, beginGridTextView, beginNorthingTextView, beginEastingTextView, beginTimeTextView;
    private TextView endLatitudeTextView, endLongitudeTextView, endAltitudeTextView, endGridTextView, endNorthingTextView, endEastingTextView, endTimeTextView;

    private TextView GPSConnectionTextView, reachConnectionTextView;

    /**
     * The spinner for displaying the dropdown of materials
     */
    Spinner teamMembersDropdown;

    private boolean newPath = true;

    private LocationCollector locationCollector;

    private Double liveLatitude, liveLongitude, liveAltitude;
    private String liveStatus;

    private Integer beginZone, endZone;
    private String beginHemisphere, endHemisphere;
    private Integer beginNorthing, beginEasting, endNorthing, endEasting;

    /**
     * Variables to store the users location data obtained from the Reach
     */
    private Double beginLatitude, beginLongitude, beginAltitude, endLatitude, endLongitude, endAltitude;

    /**
     * Variables to store status of the position fetch
     */
    private String beginStatus, endStatus;

    String teamMember;

    long beginTime, endTime;

    boolean startPointSet = false;
    boolean endPointSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_entry);

        initialiseViews();

        // Load persistent app data from shared preferences
        SharedPreferences settings = getSharedPreferences(PREFERENCES, 0);
        String reachHost = settings.getString("reachHost", DEFAULT_REACH_HOST);
        String reachPort = settings.getString("reachPort", DEFAULT_REACH_PORT);
        Integer positionUpdateInterval = settings.getInt("positionUpdateInterval", DEFAULT_POSITION_UPDATE_INTERVAL);

        // Initialize the locationCollector
        locationCollector = new LocationCollector(PathEntryActivity.this, reachHost, reachPort, positionUpdateInterval) {
            @Override
            public void broadcastLocation(double _latitude, double _longitude, double _altitude, String _status) {
                liveLatitude = _latitude;
                liveLongitude = _longitude;
                liveAltitude = _altitude;
                liveStatus = _status;
                previewLocationDetails();
            }

            @Override
            public void broadcastGPSStatus(String status) {
                setGPSStatus(status);
            }

            public void broadcastReachStatus(String status) {
                setReachStatus(status);
            }
        };

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        locationCollector.cancelPositionUpdateTimer();

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_path_entry);
        setSupportActionBar(toolbar);

        // Configure up button to go back to previous activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get references to the latitude, longitude, altitude, and status text views
        beginLatitudeTextView = (TextView) findViewById(R.id.beginLatitude);
        beginLongitudeTextView = (TextView) findViewById(R.id.beginLongitude);
        beginAltitudeTextView = (TextView) findViewById(R.id.beginAltitude);
        beginGridTextView = (TextView) findViewById(R.id.beginGrid);
        beginNorthingTextView = (TextView) findViewById(R.id.beginNorthing);
        beginEastingTextView = (TextView) findViewById(R.id.beginEasting);
        beginTimeTextView = (TextView) findViewById(R.id.beginTime);
        endLatitudeTextView = (TextView) findViewById(R.id.endLatitude);
        endLongitudeTextView = (TextView) findViewById(R.id.endLongitude);
        endAltitudeTextView = (TextView) findViewById(R.id.endAltitude);
        endGridTextView = (TextView) findViewById(R.id.endGrid);
        endNorthingTextView = (TextView) findViewById(R.id.endNorthing);
        endEastingTextView = (TextView) findViewById(R.id.endEasting);
        endTimeTextView = (TextView) findViewById(R.id.endTime);

        GPSConnectionTextView = (TextView) findViewById(R.id.GPSConnection);
        reachConnectionTextView = (TextView) findViewById(R.id.reachConnection);

        GPSConnectionTextView.setText(String.format(getResources().getString(R.string.GPSConnection), getString(R.string.blank_assignment)));
        reachConnectionTextView.setText(String.format(getResources().getString(R.string.reachConnection), getString(R.string.blank_assignment)));

        /**
         * Configure the materials dropdown menu
         */
        teamMembersDropdown = (Spinner) findViewById(R.id.path_entry_team_members_drop_down);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> teamMemberAdapter = ArrayAdapter.createFromResource(this, R.array.people_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        teamMemberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        teamMembersDropdown.setAdapter(teamMemberAdapter);
        teamMember = teamMembersDropdown.getSelectedItem().toString();
        teamMembersDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                teamMember = teamMembersDropdown.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        boolean prepopulatedData = prePopulateFields();

        // Set text on submit button (depending on newPath)
        Button submitButton = (Button) findViewById(R.id.path_entry_submit_button);
        if (!startPointSet) {
            submitButton.setText(R.string.start_path_button);
        } else if (!endPointSet) {
            submitButton.setText(R.string.stop_path_button);
        } else {
            submitButton.setText(R.string.reset_stop_path_button);
        }
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button submitButton = (Button) v;
                if (liveLatitude != null && !liveLatitude.equals(0)) {
                    if (!startPointSet) {

                        setStartPoint(liveLatitude, liveLongitude, liveAltitude, liveStatus);
                        startPointSet = true;
                        submitButton.setText(R.string.stop_path_button);
                        saveData();

                    } else if (!endPointSet) {

                        setEndPoint(liveLatitude, liveLongitude, liveAltitude, liveStatus);
                        endPointSet = true;
                        submitButton.setText(R.string.reset_stop_path_button);
                        onBackPressed();

                    } else {

                        setEndPoint(liveLatitude, liveLongitude, liveAltitude, liveStatus);
                        onBackPressed();
                        Toast.makeText(PathEntryActivity.this, "End point reset.", Toast.LENGTH_LONG).show();

                    }
                } else {
                    Toast.makeText(PathEntryActivity.this, "You do not have a valid point.", Toast.LENGTH_LONG).show();
                }

            }

        });

    }

    /**
     * Check if any parameters were passed to this activity, and pre populate the data if required
     * @return True if data was pre populated, false otherwise.
     */
    private boolean prePopulateFields() {

        // Reset the starting and end points
        resetStartPoint();
        resetEndPoint();

        Long _beginTime = getIntent().getLongExtra(ConstantsAndHelpers.PARAM_KEY_BEGIN_TIME, 0);
        String _teamMember = getIntent().getStringExtra(ConstantsAndHelpers.PARAM_KEY_TEAM_MEMBER);

        System.out.println(_teamMember);
        System.out.println(Long.toString(_beginTime));

        // If null, it means nothing was passed
        if (_beginTime.equals(0) || _teamMember == null) {

            return false;

        }

        // If those are passed in, we know this isn't a new path
        newPath = false;
        startPointSet = true;

        beginTime = _beginTime;

        double _beginLatitude = getIntent().getDoubleExtra(ConstantsAndHelpers.PARAM_KEY_BEGIN_LATITUDE, Double.MIN_VALUE);
        double _beginLongitude = getIntent().getDoubleExtra(ConstantsAndHelpers.PARAM_KEY_BEGIN_LONGITUDE, Double.MIN_VALUE);
        double _beginAltitude = getIntent().getDoubleExtra(ConstantsAndHelpers.PARAM_KEY_BEGIN_ALTITUDE, Double.MIN_VALUE);
        setStartPoint(_beginLatitude, _beginLongitude, _beginAltitude, getString(R.string.blank_assignment));

        // TODO: Add delete button below submit button

        // Populate the selected team member
        // Search the dropdown for a matching teamMember, and set to that if found
        // TODO: DO THIS
        for (int i = 0; i < teamMembersDropdown.getCount(); i++) {

            if (teamMembersDropdown.getItemAtPosition(i).toString().equalsIgnoreCase(_teamMember)) {

                teamMembersDropdown.setSelection(i);

            }

        }

        // Check to see if the end point has been set before
        Long _endTime = getIntent().getLongExtra(ConstantsAndHelpers.PARAM_KEY_END_TIME, 0);
        if (!_endTime.equals(0)) {
            endPointSet = true;

            double _endLatitude = getIntent().getDoubleExtra(ConstantsAndHelpers.PARAM_KEY_END_LATITUDE, Double.MIN_VALUE);
            double _endLongitude = getIntent().getDoubleExtra(ConstantsAndHelpers.PARAM_KEY_END_LONGITUDE, Double.MIN_VALUE);
            double _endAltitude = getIntent().getDoubleExtra(ConstantsAndHelpers.PARAM_KEY_END_ALTITUDE, Double.MIN_VALUE);
            setEndPoint(_endLatitude, _endLongitude, _endAltitude, getString(R.string.blank_assignment));
            // TODO: setEndPoint();
        }

        return true;

    }

    private void setStartPoint(double _latitude, double _longitude, double _altitude, String status) {

        // Change "Start path" button to "End path", populate starting point coords and begin time
        beginLongitude = _longitude;
        beginLatitude = _latitude;
        beginAltitude = _altitude;

        // Update the text views for coordinates
        beginLatitudeTextView.setText(String.format(getResources().getString(R.string.latitude), beginLatitude));
        beginLongitudeTextView.setText(String.format(getResources().getString(R.string.longitude), beginLongitude));
        beginAltitudeTextView.setText(String.format(getResources().getString(R.string.altitude), beginAltitude));

        // Update UTM positions
        Angle lat = Angle.fromDegrees(beginLatitude);

        Angle lon = Angle.fromDegrees(beginLongitude);

        UTMCoord UTMposition = UTMCoord.fromLatLon(lat, lon);

        beginZone = UTMposition.getZone();
        beginHemisphere = UTMposition.getHemisphere().contains("North") ? "N" : "S";
        beginNorthing = new Integer((int) Math.floor(UTMposition.getNorthing()));
        beginEasting = new Integer((int) Math.floor(UTMposition.getEasting()));

        beginGridTextView.setText(String.format(getResources().getString(R.string.grid), beginZone + beginHemisphere));
        beginNorthingTextView.setText(String.format(getResources().getString(R.string.northing), beginNorthing));
        beginEastingTextView.setText(String.format(getResources().getString(R.string.easting), beginEasting));

        if (!startPointSet) {
            beginTime = System.currentTimeMillis();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        beginTimeTextView.setText(sdf.format(new Date(beginTime)));
    }

    private void setEndPoint(double _latitude, double _longitude, double _altitude, String status) {

        // Change "Start path" button to "End path", populate starting point coords and begin time
        endLongitude = _longitude;
        endLatitude = _latitude;
        endAltitude = _altitude;

        // Update the text views for coordinates
        endLatitudeTextView.setText(String.format(getResources().getString(R.string.latitude), endLatitude));
        endLongitudeTextView.setText(String.format(getResources().getString(R.string.longitude), endLongitude));
        endAltitudeTextView.setText(String.format(getResources().getString(R.string.altitude), endAltitude));

        // Update UTM positions
        Angle lat = Angle.fromDegrees(endLatitude);

        Angle lon = Angle.fromDegrees(endLongitude);

        UTMCoord UTMposition = UTMCoord.fromLatLon(lat, lon);

        endZone = UTMposition.getZone();
        endHemisphere = UTMposition.getHemisphere().contains("North") ? "N" : "S";
        endNorthing = new Integer((int) Math.floor(UTMposition.getNorthing()));
        endEasting = new Integer((int) Math.floor(UTMposition.getEasting()));

        endGridTextView.setText(String.format(getResources().getString(R.string.grid), endZone + endHemisphere));
        endNorthingTextView.setText(String.format(getResources().getString(R.string.northing), endNorthing));
        endEastingTextView.setText(String.format(getResources().getString(R.string.easting), endEasting));

        if (!endPointSet) {
            endTime = System.currentTimeMillis();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        endTimeTextView.setText(sdf.format(new Date(beginTime)));
    }

    private void resetStartPoint() {

        beginLatitudeTextView.setText(R.string.blank_assignment);
        beginLongitudeTextView.setText(R.string.blank_assignment);
        beginAltitudeTextView.setText(R.string.blank_assignment);

        beginGridTextView.setText(R.string.blank_assignment);
        beginNorthingTextView.setText(R.string.blank_assignment);
        beginEastingTextView.setText(R.string.blank_assignment);

        beginTimeTextView.setText(R.string.blank_assignment);


    }

    private void resetEndPoint() {

        endLatitudeTextView.setText(R.string.blank_assignment);
        endLongitudeTextView.setText(R.string.blank_assignment);
        endAltitudeTextView.setText(R.string.blank_assignment);

        endGridTextView.setText(R.string.blank_assignment);
        endNorthingTextView.setText(R.string.blank_assignment);
        endEastingTextView.setText(R.string.blank_assignment);

        endTimeTextView.setText(R.string.blank_assignment);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        /*
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
        */
    }

    @Override
    protected void onPause() {

        super.onPause();

        locationCollector.pause();

    }

    @Override
    protected void onResume() {

        super.onResume();

        locationCollector.resume();

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

                SharedPreferences settings = getSharedPreferences(PREFERENCES, 0);
                String reachHost = settings.getString("reachHost", DEFAULT_REACH_HOST);
                String reachPort = settings.getString("reachPort", DEFAULT_REACH_PORT);
                Integer positionUpdateInterval = settings.getInt("positionUpdateInterval", DEFAULT_POSITION_UPDATE_INTERVAL);

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
                                String reachHost = hostBox.getText().toString();
                                String reachPort = portBox.getText().toString();

                                Integer positionUpdateInterval = Integer.parseInt(intervalBox.getText().toString());

                                // Reset the socket connection
                                locationCollector.resetReachConnection(reachHost, reachPort);

                                // Save these new values to shared preferences
                                SharedPreferences settings = getSharedPreferences(PREFERENCES, 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("reachHost", reachHost);
                                editor.putString("reachPort", reachPort);
                                editor.putInt("positionUpdateInterval", positionUpdateInterval);
                                editor.commit();

                                // Restart the timer
                                locationCollector.resetPositionUpdateInterval(positionUpdateInterval);

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

    private void previewLocationDetails() {
        System.out.println("SETTING STARTING POINT*********************************");
        if (!startPointSet) {
            beginLatitudeTextView.setText(Double.toString(liveLatitude));
            beginLongitudeTextView.setText(Double.toString(liveLongitude));
            beginAltitudeTextView.setText(Double.toString(liveAltitude));

            beginGridTextView.setText(R.string.blank_assignment);
            beginNorthingTextView.setText(R.string.blank_assignment);
            beginEastingTextView.setText(R.string.blank_assignment);

            beginTimeTextView.setText(R.string.blank_assignment);
        } else if (!endPointSet) {
            endLatitudeTextView.setText(Double.toString(liveLatitude));
            endLongitudeTextView.setText(Double.toString(liveLongitude));
            endAltitudeTextView.setText(Double.toString(liveAltitude));

            endGridTextView.setText(R.string.blank_assignment);
            endNorthingTextView.setText(R.string.blank_assignment);
            endEastingTextView.setText(R.string.blank_assignment);

            endTimeTextView.setText(R.string.blank_assignment);
        }

    }

    private void setGPSStatus(String status) {
        GPSConnectionTextView.setText(String.format(getResources().getString(R.string.GPSConnection), status));
    }

    private void setReachStatus(String status) {
        reachConnectionTextView.setText(String.format(getResources().getString(R.string.reachConnection), status));
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

        // Save data when back button on action bar is pressed
        saveData();

        // Disconnect from GPS
        locationCollector.pause();

    }


    /**
     * Save all the data entered in the form
     */
    private void saveData() {

        // We only save changes if teamMember and begin time are present
        if (teamMember == null || beginTime == 0) {

            Toast.makeText(PathEntryActivity.this, "You did not start a path or select a team member. Try again.", Toast.LENGTH_LONG).show();
            return;

        }

        // Todo: Create a data entry element
        PathElement list[] = new PathElement[1];

        list[0] = new PathElement(teamMember, beginLatitude, beginLongitude, beginAltitude, endLatitude, endLongitude, endAltitude, beginHemisphere, beginZone, beginEasting, beginNorthing, endEasting, endNorthing, beginTime, endTime);

        // Save the dataEntryElement to DB
        DataBaseHandler dataBaseHandler = new DataBaseHandler(this);
        dataBaseHandler.addPathsRows(list);

    }


}
