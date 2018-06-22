/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.geom.coords;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
/**
 * This immutable class holds a set of UTM coordinates along with it's corresponding latitude and longitude.
 * @author Patrick Murris
 * @version $Id$
 */
public class UTMCoord
{
    private final Angle LATITUDE, LONGITUDE;
    private final String HEMISPHERE;
    private final int ZONE;
    private final double EASTING, NORTHING;
    /**
     * Create a set of UTM coordinates from a pair of latitude and longitude for the given <code>Globe</code>.
     * @param latitude - the latitude <code>Angle</code>.
     * @param longitude - the longitude <code>Angle</code>.
     * @return the corresponding <code>UTMCoord</code>.
     * @throws IllegalArgumentException if <code>latitude</code> or <code>longitude</code> is null, or the conversion to
     *                                  UTM coordinates fails.
     */
    public static UTMCoord fromLatLon(Angle latitude, Angle longitude)
    {
        if (latitude == null || longitude == null)
        {
            throw new IllegalArgumentException("Latitude Or Longitude Is Null");
        }
        final UTMCoordConverter converter = new UTMCoordConverter();
        long err = converter.convertGeodeticToUTM(latitude.RADIANS, longitude.RADIANS);
        if (err != UTMCoordConverter.UTM_NO_ERROR)
        {
            throw new IllegalArgumentException("UTM Conversion Error");
        }
        return new UTMCoord(latitude, longitude, converter.getZone(), converter.getHemisphere(),
                converter.getEasting(), converter.getNorthing());
    }

    /**
     * Create an arbitrary set of UTM coordinates with the given values.
     * @param latitude - the latitude <code>Angle</code>.
     * @param longitude - the longitude <code>Angle</code>.
     * @param zone - the UTM zone - 1 to 60.
     * @param hemisphere - the hemisphere, either {@link gov.nasa.worldwind.avlist.AVKey#NORTH} or {@link
     *                        gov.nasa.worldwind.avlist.AVKey#SOUTH}.
     * @param easting - the easting distance in meters
     * @param northing - the northing distance in meters.
     * @throws IllegalArgumentException if <code>latitude</code> or <code>longitude</code> is null.
     */
    private UTMCoord(Angle latitude, Angle longitude, int zone, String hemisphere, double easting,
                     double northing)
    {
        if (latitude == null || longitude == null)
        {
            throw new IllegalArgumentException("Latitude Or Longitude Is Null");
        }
        this.LATITUDE = latitude;
        this.LONGITUDE = longitude;
        this.HEMISPHERE = hemisphere;
        this.ZONE = zone;
        this.EASTING = easting;
        this.NORTHING = northing;
    }

    /**
     * Get latitude
     * @return Returns latitude
     */
    public Angle getLatitude()
    {
        return this.LATITUDE;
    }

    /**
     * Get longitude
     * @return Returns longitude
     */
    public Angle getLongitude()
    {
        return this.LONGITUDE;
    }

    /**
     * Get zone
     * @return Returns zone
     */
    public int getZone()
    {
        return this.ZONE;
    }

    /**
     * Get hemisphere
     * @return Returns hemisphere
     */
    public String getHemisphere()
    {
        return this.HEMISPHERE;
    }

    /**
     * Get easting
     * @return Returns easting
     */
    public double getEasting()
    {
        return this.EASTING;
    }

    /**
     * Get northing
     * @return Returns northing
     */
    public double getNorthing()
    {
        return this.NORTHING;
    }

    /**
     * Convert to String
     * @return Returns string form of coordinate
     */
    public String toString()
    {
        return ZONE + " " + (AVKey.NORTH.equals(HEMISPHERE) ? "N" : "S") + " " + EASTING + "E " + NORTHING + "N";
    }
}