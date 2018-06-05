package edu.upenn.sas.archaeologyapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;
import java.net.ConnectException;

import static edu.upenn.sas.archaeologyapp.ConstantsAndHelpers.*;
import static java.lang.System.currentTimeMillis;

public abstract class LocationCollector {

    /**
     * The global variable used for the position update interval
     */
    private int positionUpdateInterval = DEFAULT_POSITION_UPDATE_INTERVAL;

    /**
     * The timer used to periodically update the position
     */
    private Timer positionUpdateTimer;

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
     * Corresponding strings for status codes in LLH format as defined in the RTKLIB manual v2.4.2, p.102
     */
    private static final String[] STATUS_CODES = {"Error", "Fixed", "Float", "Reserved", "DGPS", "Single"};

    /**
     * The global string for the Emlid Reach host
     */
    private String reachHost = DEFAULT_REACH_HOST;

    /**
     * The global string for the Emlid Reach port
     */
    private String reachPort = DEFAULT_REACH_PORT;

    /**
     * The activity/context from where this location collector is used
     */
    private android.app.Activity context;

    /**
     * The socket we use to connect with the Reach rover
     */
    Socket reachSocket;


    public LocationCollector(android.app.Activity _context, String _reachHost, String _reachPort, Integer _positionUpdateInterval) {

        // Set the context
        context = _context;

        // Set host and port
        reachHost = _reachHost;
        reachPort = _reachPort;
        positionUpdateInterval = _positionUpdateInterval;

        // Initialize the GPS listener
        initiateGPS();

        // Setup the position updater
        positionUpdateTimer = new Timer();
        restartPositionUpdateTimer();


    }

    private void initiateGPS() {
        // Acquire a reference to the system Location Manager
        if (locationManager == null) {

            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

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
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Ask user for permission
            ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);

        } else {

            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }
    }

    private class GetPositionOutputFromReach extends AsyncTask<String, String, String> {
        private String data;

        protected String doInBackground(String... params) {
            String host = params[0];
            Integer port = Integer.parseInt(params[1]);
            String msg = "";
            BufferedReader reachSocketInput;

            if (reachSocket == null) {
                System.out.println("Trying to create Socket, host: "+host);
                try {
                    int timeout = positionUpdateInterval * 1000;
                    System.out.println("Timeout: "+Integer.toString(timeout));
                    reachSocket = new Socket();
                    reachSocket.setSoTimeout(timeout);
                    reachSocket.connect(new InetSocketAddress(host, port), timeout);
                    System.out.println("MADE SOCKET CONNECTION");

                } catch(ConnectException e) {

                    e.printStackTrace();
                    return context.getString(R.string.no_connection);

                } catch(Exception e) {

                    return context.getString(R.string.no_connection);

                }
            }
            try {
                reachSocketInput = new BufferedReader(new InputStreamReader(reachSocket.getInputStream()));
            } catch(Exception E) {
                return context.getString(R.string.no_connection);
            }

            System.out.println("Socket exists, trying to wait for input");
            try {
                String currentLine;
                System.out.println("WAITING FOR MSG");
                while ((currentLine = reachSocketInput.readLine()) != null) {
                    // TODO: make sure this is the latest line
                    //msg = currentLine;
                    return currentLine;
                }
            } catch(SocketTimeoutException e) {

                e.printStackTrace();
                return context.getString(R.string.timeout);

            } catch(Exception e) {

                return context.getString(R.string.timeout);

            }
            System.out.println("DIDN'T GET MSG: "+msg);
            return msg;

        }

        protected void onPostExecute(String result) {
            System.out.println(result);
            String[] parsed = result.split("\\s+");
            if (parsed.length < 15) {
                // Check to see if no data was passed through
                if (result.length() == 0) {
                    broadcastReachStatus(context.getString(R.string.no_data));
                } else {
                    // Connection error, print the result/connection error we passed through
                    broadcastReachStatus(result);
                }
                initiateGpsFetch();
            } else {
                double lat = Double.parseDouble(parsed[2]);
                double lon = Double.parseDouble(parsed[3]);
                double height = Double.parseDouble(parsed[4]);
                String status = STATUS_CODES[Integer.parseInt(parsed[5])];
                broadcastLocation(lat, lon, height, status);
                broadcastReachStatus(context.getString(R.string.connected));
            }
        }
    }


    /**
     * This function initiates fetching data from an Emlid Reach output server
     */
    private void initiateReachFetch() {

        System.out.println("TRYING TO GETB POSITION");
        new GetPositionOutputFromReach().execute(reachHost, reachPort);

    }

    /**
     * This function contains the flow for fetching data from GPS
     */
    private void initiateGpsFetch() {

        if (GPSlatitude != null && GPSlongitude != null && GPSlatitude != null) {
            broadcastLocation(GPSlatitude, GPSlongitude, GPSaltitude, "GPS");
            broadcastGPSStatus(context.getString(R.string.connected));
        } else {
            broadcastGPSStatus(context.getString(R.string.no_data));
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

    }

    /**
     * Build and show alert dialog to the user, requesting him to turn GPS on
     */
    private void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(R.string.gps_enable_alert_box)
                .setCancelable(false)
                .setPositiveButton(R.string.enable_gps_alert_positive_button, new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, final int id) {

                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        dialog.cancel();

                    }

                })
                .setNegativeButton(R.string.enable_gps_alert_negative_button, new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, final int id) {

                        // Show a toast to the user requesting that he allows permission for GPS use
                        Toast.makeText(context, R.string.location_permission_denied_prompt, Toast.LENGTH_LONG).show();
                        dialog.cancel();

                    }

                });

        final AlertDialog alert = builder.create();
        alert.show();

    }

    public void pause() {

        // Stop listening to GPS, if still listening
        try {

            locationManager.removeUpdates(locationListener);

        } catch (SecurityException e) {

            //Toast.makeText(DataEntryActivity.this, R.string.location_permission_denied_prompt, Toast.LENGTH_LONG).show();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void resume() {

        // initiate GPS again
        initiateGPS();

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
                initiateReachFetch();
            }
        }, 0, positionUpdateInterval * 1000);
    }

    /**
     * Cancel the position update timer, prevent this from getting positions
     */
    public void cancelPositionUpdateTimer() {
        positionUpdateTimer.cancel();

    }

    public void resetReachConnection(String _reachHost, String _reachPort) {

        System.out.println("resetting reach connection"+_reachHost);

        // Reset the socket
        reachSocket = null;

        // Define new host
        reachHost = _reachHost;
        reachPort = _reachPort;

    }

    public void resetPositionUpdateInterval(Integer _positionUpdateInterval) {

        // Set new positionUpdateInterval
        positionUpdateInterval = _positionUpdateInterval;

        // Attempt to connect, restart the timer
        restartPositionUpdateTimer();

    }

    public String getReachHost() {
        return reachHost;
    }

    public void setReachHost(String _reachHost) {
        reachHost = _reachHost;
    }

    public String getReachPort() {
        return reachPort;
    }

    public void setReachPort(String _reachPort) {
        reachPort = _reachPort;
    }

    public int getPositionUpdateInterval() {
        return positionUpdateInterval;
    }

    public void setPositionUpdateInterval(int _positionUpdateInterval) {
        positionUpdateInterval = _positionUpdateInterval;
    }

    public abstract void broadcastLocation(double latitude, double longitude, double altitude, String status);

    public abstract void broadcastGPSStatus(String status);

    public abstract void broadcastReachStatus(String status);
}
