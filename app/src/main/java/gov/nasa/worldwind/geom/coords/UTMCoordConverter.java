/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.geom.coords;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
/**
 * Converter used to translate UTM coordinates to and from geodetic latitude and longitude.
 * Ported to Java from the NGA GeoTrans utm.c and utm.h
 * @author Garrett Headley, Patrick Murris
 * @version $Id$
 * @see UTMCoord, TMCoordConverter
 */
class UTMCoordConverter
{
    public final static int UTM_NO_ERROR = 0x0000;
    private final static int UTM_LAT_ERROR = 0x0001, UTM_LON_ERROR = 0x0002, UTM_EASTING_ERROR = 0x0004;
    private final static int UTM_NORTHING_ERROR = 0x0008;
    private final static int UTM_TM_ERROR = 0x0200, MIN_EASTING = 100000, MAX_EASTING = 900000, MIN_NORTHING = 0;
    private final static double PI = 3.14159265358979323, MIN_LAT = ((-82 * PI) / 180.0);
    // 86 degrees in radians
    private final static double MAX_LAT = ((86 * PI) / 180.0);
    private final static int MAX_NORTHING = 10000000;
    private double easting, northing;
    private String hemisphere;
    private int zone;
    /**
     * Constructor
     */
    UTMCoordConverter()
    {
    }

    /**
     * The function Convert_Geodetic_To_UTM converts geodetic (latitude and longitude) coordinates to UTM projection
     * (zone, hemisphere, easting and northing) coordinates according to the current ellipsoid and UTM zone override
     * parameters.  If any errors occur, the error code(s) are returned by the function, otherwise UTM_NO_ERROR is
     * returned.
     * @param latitude - Latitude in radians
     * @param longitude - Longitude in radians
     * @return error code
     */
    public long convertGeodeticToUTM(double latitude, double longitude)
    {
        long latDegrees, longDegrees, tempZone, errorCode = UTM_NO_ERROR;
        double originLatitude = 0, falseEasting = 500000, falseNorthing = 0, scale = 0.9996;
        // Latitude out of range
        if ((latitude < MIN_LAT) || (latitude > MAX_LAT))
        {
            errorCode |= UTM_LAT_ERROR;
        }
        // Longitude out of range
        if ((longitude < -PI) || (longitude > (2 * PI)))
        {
            errorCode |= UTM_LON_ERROR;
        }
        // no errors
        if (errorCode == UTM_NO_ERROR)
        {
            if (longitude < 0)
            {
                longitude += (2 * PI) + 1.0e-10;
            }
            latDegrees = (long) (latitude * 180.0 / PI);
            longDegrees = (long) (longitude * 180.0 / PI);
            if (longitude < PI)
            {
                tempZone = (long) (31 + ((longitude * 180.0 / PI) / 6.0));
            }
            else
            {
                tempZone = (long) (((longitude * 180.0 / PI) / 6.0) - 29);
            }
            if (tempZone > 60)
            {
                tempZone = 1;
            }
            // UTM special cases
            if ((latDegrees > 55) && (latDegrees < 64) && (longDegrees > -1) && (longDegrees < 3))
            {
                tempZone = 31;
            }
            if ((latDegrees > 55) && (latDegrees < 64) && (longDegrees > 2) && (longDegrees < 12))
            {
                tempZone = 32;
            }
            if ((latDegrees > 71) && (longDegrees > -1) && (longDegrees < 9))
            {
                tempZone = 31;
            }
            if ((latDegrees > 71) && (longDegrees > 8) && (longDegrees < 21))
            {
                tempZone = 33;
            }
            if ((latDegrees > 71) && (longDegrees > 20) && (longDegrees < 33))
            {
                tempZone = 35;
            }
            if ((latDegrees > 71) && (longDegrees > 32) && (longDegrees < 42))
            {
                tempZone = 37;
            }
            if (errorCode == UTM_NO_ERROR)
            {
                double centralMeridian;
                if (tempZone >= 31)
                {
                    centralMeridian = (6 * tempZone - 183) * PI / 180.0;
                }
                else
                {
                    centralMeridian = (6 * tempZone + 177) * PI / 180.0;
                }
                zone = (int) tempZone;
                if (latitude < 0)
                {
                    falseNorthing = 10000000;
                    hemisphere = AVKey.SOUTH;
                }
                else
                {
                    hemisphere = AVKey.NORTH;
                }
                try
                {
                    TMCoord TM = TMCoord.fromLatLon(Angle.fromRadians(latitude), Angle.fromRadians(longitude),
                            6378137.0, 1 / 298.257223563, Angle.fromRadians(originLatitude),
                            Angle.fromRadians(centralMeridian), falseEasting, falseNorthing, scale);
                    easting = TM.getEasting();
                    northing = TM.getNorthing();
                    if ((easting < MIN_EASTING) || (easting > MAX_EASTING))
                    {
                        errorCode = UTM_EASTING_ERROR;
                    }
                    if ((northing < MIN_NORTHING) || (northing > MAX_NORTHING))
                    {
                        errorCode |= UTM_NORTHING_ERROR;
                    }
                }
                catch (Exception e)
                {
                    errorCode = UTM_TM_ERROR;
                }
            }
        }
        return errorCode;
    }

    /**
     * Get easting
     * @return Easting (X) in meters
     */
    public double getEasting()
    {
        return easting;
    }

    /**
     * Get northing
     * @return Northing (Y) in meters
     */
    public double getNorthing()
    {
        return northing;
    }

    /**
     * Get hemisphere
     * @return The coordinate hemisphere, either {@link gov.nasa.worldwind.avlist.AVKey#NORTH} or {@link
     *         gov.nasa.worldwind.avlist.AVKey#SOUTH}.
     */
    public String getHemisphere()
    {
        return hemisphere;
    }

    /**
     * Get zone
     * @return UTM zone
     */
    public int getZone()
    {
        return zone;
    }
}