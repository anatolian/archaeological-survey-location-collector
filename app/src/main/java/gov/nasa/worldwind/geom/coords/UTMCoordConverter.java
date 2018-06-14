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
    private double Easting, Northing;
    private String Hemisphere;
    private int Zone;
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
     * @param Latitude - Latitude in radians
     * @param Longitude - Longitude in radians
     * @return error code
     */
    public long convertGeodeticToUTM(double Latitude, double Longitude)
    {
        long Lat_Degrees, Long_Degrees, temp_zone, Error_Code = UTM_NO_ERROR;
        double Origin_Latitude = 0, False_Easting = 500000, False_Northing = 0, Scale = 0.9996;
        // Latitude out of range
        if ((Latitude < MIN_LAT) || (Latitude > MAX_LAT))
        {
            Error_Code |= UTM_LAT_ERROR;
        }
        // Longitude out of range
        if ((Longitude < -PI) || (Longitude > (2 * PI)))
        {
            Error_Code |= UTM_LON_ERROR;
        }
        // no errors
        if (Error_Code == UTM_NO_ERROR)
        {
            if (Longitude < 0)
            {
                Longitude += (2 * PI) + 1.0e-10;
            }
            Lat_Degrees = (long) (Latitude * 180.0 / PI);
            Long_Degrees = (long) (Longitude * 180.0 / PI);
            if (Longitude < PI)
            {
                temp_zone = (long) (31 + ((Longitude * 180.0 / PI) / 6.0));
            }
            else
            {
                temp_zone = (long) (((Longitude * 180.0 / PI) / 6.0) - 29);
            }
            if (temp_zone > 60)
            {
                temp_zone = 1;
            }
            // UTM special cases
            if ((Lat_Degrees > 55) && (Lat_Degrees < 64) && (Long_Degrees > -1) && (Long_Degrees < 3))
            {
                temp_zone = 31;
            }
            if ((Lat_Degrees > 55) && (Lat_Degrees < 64) && (Long_Degrees > 2) && (Long_Degrees < 12))
            {
                temp_zone = 32;
            }
            if ((Lat_Degrees > 71) && (Long_Degrees > -1) && (Long_Degrees < 9))
            {
                temp_zone = 31;
            }
            if ((Lat_Degrees > 71) && (Long_Degrees > 8) && (Long_Degrees < 21))
            {
                temp_zone = 33;
            }
            if ((Lat_Degrees > 71) && (Long_Degrees > 20) && (Long_Degrees < 33))
            {
                temp_zone = 35;
            }
            if ((Lat_Degrees > 71) && (Long_Degrees > 32) && (Long_Degrees < 42))
            {
                temp_zone = 37;
            }
            if (Error_Code == UTM_NO_ERROR)
            {
                double Central_Meridian;
                if (temp_zone >= 31)
                {
                    Central_Meridian = (6 * temp_zone - 183) * PI / 180.0;
                }
                else
                {
                    Central_Meridian = (6 * temp_zone + 177) * PI / 180.0;
                }
                Zone = (int) temp_zone;
                if (Latitude < 0)
                {
                    False_Northing = 10000000;
                    Hemisphere = AVKey.SOUTH;
                }
                else
                {
                    Hemisphere = AVKey.NORTH;
                }
                try
                {
                    TMCoord TM = TMCoord.fromLatLon(Angle.fromRadians(Latitude), Angle.fromRadians(Longitude),
                            6378137.0, 1 / 298.257223563, Angle.fromRadians(Origin_Latitude),
                            Angle.fromRadians(Central_Meridian), False_Easting, False_Northing, Scale);
                    Easting = TM.getEasting();
                    Northing = TM.getNorthing();
                    if ((Easting < MIN_EASTING) || (Easting > MAX_EASTING))
                    {
                        Error_Code = UTM_EASTING_ERROR;
                    }
                    if ((Northing < MIN_NORTHING) || (Northing > MAX_NORTHING))
                    {
                        Error_Code |= UTM_NORTHING_ERROR;
                    }
                }
                catch (Exception e)
                {
                    Error_Code = UTM_TM_ERROR;
                }
            }
        }
        return (Error_Code);
    }

    /**
     * Get easting
     * @return Easting (X) in meters
     */
    public double getEasting()
    {
        return Easting;
    }

    /**
     * Get northing
     * @return Northing (Y) in meters
     */
    public double getNorthing()
    {
        return Northing;
    }

    /**
     * Get hemisphere
     * @return The coordinate hemisphere, either {@link gov.nasa.worldwind.avlist.AVKey#NORTH} or {@link
     *         gov.nasa.worldwind.avlist.AVKey#SOUTH}.
     */
    public String getHemisphere()
    {
        return Hemisphere;
    }

    /**
     * Get zone
     * @return UTM zone
     */
    public int getZone()
    {
        return Zone;
    }
}