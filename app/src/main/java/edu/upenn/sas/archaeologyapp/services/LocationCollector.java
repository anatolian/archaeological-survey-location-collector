// Location collector interface
// @author Colin Roberts
package edu.upenn.sas.archaeologyapp.services;
import android.Manifest;
import android.app.Activity;
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
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;
import edu.upenn.sas.archaeologyapp.R;
public abstract class LocationCollector
{
    // The timer used to periodically update the position
    private Timer positionUpdateTimer;
    // Location manager for accessing the users location
    private LocationManager locationManager;
    // Listener with callbacks to get the users location
    private LocationListener locationListener;
    // Variables to store the users location data obtained from GPS, as a backup to the Reach data
    private Double GPSlatitude, GPSlongitude, GPSaltitude;
    // Int constant used to determine if GPS permission was granted or denied
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 100;
    // Corresponding strings for status codes in LLH format as defined in the RTKLIB manual v2.4.2, p.102
    private static final String[] STATUS_CODES = {"Error", "Fixed", "Float", "Reserved", "DGPS", "Single"};
    // The activity/context from where this location collector is used
    private Activity context;
    // The socket we use to connect with the Reach rover
    private Socket reachSocket;
    private String reachHost, reachPort;
    private Integer positionUpdateInterval;
    /**
     * Constructor
     * @param _context - calling context
     * @param _reachHost - reach IP
     * @param _reachPort - reach port
     * @param _positionUpdateInterval - time between updates
     */
    protected LocationCollector(Activity _context, String _reachHost, String _reachPort, Integer _positionUpdateInterval)
    {
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

    /**
     * Start GPS
     */
    private void initiateGPS()
    {
        // Acquire a reference to the system Location Manager
        if (locationManager == null)
        {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        // Check if GPS is turned on. If not, prompt the user to turn it on.
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            buildAlertMessageNoGPS();
            return;
        }
        // Define a listener that responds to location updates
        if (locationListener == null)
        {
            locationListener = new LocationListener() {
                /**
                 * GPS location changed
                 * @param location - new location
                 */
                public void onLocationChanged(Location location)
                {
                    // Called when a new location is found by the GPS location provider.
                    updateGPSlocation(location);
                }

                /**
                 * GPS status changed
                 * @param provider - GPS provider
                 * @param status - GPS status
                 * @param extras - GPS data
                 */
                public void onStatusChanged(String provider, int status, Bundle extras)
                {
                }

                /**
                 * GPS enabled
                 * @param provider - GPS provider
                 */
                public void onProviderEnabled(String provider)
                {
                }

                /**
                 * GPS disabled
                 * @param provider - GPS provider
                 */
                public void onProviderDisabled(String provider)
                {
                }
            };
        }
        // Check if the user has granted permission for using GPS
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            // Ask user for permission
            ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_ACCESS_FINE_LOCATION);
        }
        else
        {
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    private class GetPositionOutputFromReach extends AsyncTask<String, String, String>
    {
        /**
         * Background process
         * @param params - process parameters
         * @return Returns the position
         */
        protected String doInBackground(String... params)
        {
            String host = params[0];
            Integer port = Integer.parseInt(params[1]);
            String msg = "";
            BufferedReader reachSocketInput;
            if (reachSocket == null)
            {
                try
                {
                    int timeout = positionUpdateInterval * 1000;
                    reachSocket = new Socket();
                    reachSocket.setSoTimeout(timeout);
                    reachSocket.connect(new InetSocketAddress(host, port), timeout);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return context.getString(R.string.no_connection);
                }
            }
            try
            {
                reachSocketInput = new BufferedReader(new InputStreamReader(reachSocket.getInputStream()));
            }
            catch(Exception E)
            {
                return context.getString(R.string.no_connection);
            }
            try
            {
                String currentLine;
                if ((currentLine = reachSocketInput.readLine()) != null)
                {
                    return currentLine;
                }
            }
            catch(SocketTimeoutException e)
            {
                e.printStackTrace();
                return context.getString(R.string.timeout);
            }
            catch (Exception e)
            {
                return context.getString(R.string.timeout);
            }
            return msg;
        }

        /**
         * Task finished
         * @param result - task result
         */
        protected void onPostExecute(String result)
        {
            String[] parsed = result.split("\\s+");
            if (parsed.length < 15)
            {
                // Check to see if no data was passed through
                if (result.length() == 0)
                {
                    broadcastReachStatus(context.getString(R.string.no_data));
                }
                else
                {
                    // Connection error, print the result/connection error we passed through
                    broadcastReachStatus(result);
                }
                initiateGPSFetch();
            }
            else
            {
                double lat = Double.parseDouble(parsed[2]);
                double lon = Double.parseDouble(parsed[3]);
                double height = Double.parseDouble(parsed[4]);
                String status = STATUS_CODES[Integer.parseInt(parsed[5])];
                double ARRatio = Double.parseDouble(parsed[14]);
                broadcastLocation(lat, lon, height, status, ARRatio);
                broadcastReachStatus(context.getString(R.string.connected));
            }
        }
    }

    /**
     * This function initiates fetching data from an Emlid Reach output server
     */
    private void initiateReachFetch()
    {
        new GetPositionOutputFromReach().execute(reachHost, reachPort);
    }

    /**
     * This function contains the flow for fetching data from GPS
     */
    private void initiateGPSFetch()
    {
        if (GPSlatitude != null && GPSlongitude != null)
        {
            broadcastLocation(GPSlatitude, GPSlongitude, GPSaltitude, "GPS", null);
            broadcastGPSStatus(context.getString(R.string.connected));
        }
        else
        {
            broadcastGPSStatus(context.getString(R.string.no_data));
        }
    }

    /**
     * Update the GPS location details from the GPS stream, but don't update the items position data
     * @param location The location to be used
     */
    private void updateGPSlocation(Location location)
    {
        // Get the latitude, longitutde, altitude, and save it in the respective variables
        GPSlongitude = location.getLongitude();
        GPSlatitude = location.getLatitude();
        GPSaltitude = location.getAltitude();
    }

    /**
     * Build and show alert dialog to the user, requesting him/her to turn GPS on
     */
    private void buildAlertMessageNoGPS()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.gps_enable_alert_box)
                .setCancelable(false).setPositiveButton(R.string.enable_gps_alert_positive_button, new DialogInterface.OnClickListener() {
            /**
             * User approved
             * @param dialog - alert window
             * @param id - approve button id
             */
            public void onClick(final DialogInterface dialog, final int id)
            {
                context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dialog.cancel();
            }
        }).setNegativeButton(R.string.enable_gps_alert_negative_button, new DialogInterface.OnClickListener() {
            /**
             * User cancelled
             * @param dialog - alert window
             * @param id - cancel button id
             */
            public void onClick(final DialogInterface dialog, final int id)
            {
                // Show a toast to the user requesting that he allows permission for GPS use
                Toast.makeText(context, R.string.location_permission_denied_prompt, Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Pause reading
     */
    public void pause()
    {
        // Stop listening to GPS, if still listening
        try
        {
            locationManager.removeUpdates(locationListener);
        }
        catch (SecurityException e)
        {
            // do nothing
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Read GPS again
     */
    public void resume()
    {
        // initiate GPS again
        initiateGPS();
    }

    /**
     * Restart the position update timer using the global interval variable positionUpdateInterval
     */
    private void restartPositionUpdateTimer()
    {
        positionUpdateTimer.cancel();
        positionUpdateTimer = new Timer();
        positionUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            /**
             * Run GPS at intervals
             */
            @Override
            public void run()
            {
                initiateReachFetch();
            }
        },0, positionUpdateInterval * 1000);
    }

    /**
     * Cancel the position update timer, prevent this from getting positions
     */
    public void cancelPositionUpdateTimer()
    {
        positionUpdateTimer.cancel();

    }

    /**
     * Reconnect to reach
     * @param _reachHost - reach IP
     * @param _reachPort - reach port
     */
    public void resetReachConnection(String _reachHost, String _reachPort)
    {
        // Reset the socket
        reachSocket = null;
        // Define new host
        reachHost = _reachHost;
        reachPort = _reachPort;
    }

    /**
     * Change reach update interval
     * @param _positionUpdateInterval - new update interval
     */
    public void resetPositionUpdateInterval(Integer _positionUpdateInterval)
    {
        // Set new positionUpdateInterval
        positionUpdateInterval = _positionUpdateInterval;
        // Attempt to connect, restart the timer
        restartPositionUpdateTimer();
    }

    /**
     * Get location
     * @param latitude - item latitude
     * @param longitude - item longitude
     * @param altitude - item altitude
     * @param status - broadcast status
     * @param AR_ratio - broadcast AR ratio
     */
    public abstract void broadcastLocation(double latitude, double longitude, double altitude, String status, Double AR_ratio);

    /**
     * Get GPS status
     * @param status - GPS status
     */
    public abstract void broadcastGPSStatus(String status);

    /**
     * Get reach status
     * @param status - reach status
     */
    public abstract void broadcastReachStatus(String status);
}