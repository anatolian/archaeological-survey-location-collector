/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.geom.coords;
/**
 * Converter used to translate Transverse Mercator coordinates to and from geodetic latitude and longitude.
 * Ported to Java from the NGA GeoTrans code tranmerc.c and tranmerc.h
 * @author Garrett Headley, Patrick Murris
 * @version $Id$
 * @see TMCoord, UTMCoordConverter
 */
class TMCoordConverter
{
    public final static int TRANMERC_NO_ERROR = 0x0000, TRANMERC_LON_WARNING = 0x0200;
    private final static int TRANMERC_LAT_ERROR = 0x0001, TRANMERC_LON_ERROR = 0x0002;
    private final static int TRANMERC_ORIGIN_LAT_ERROR = 0x0010, TRANMERC_CENT_MER_ERROR = 0x0020;
    private final static int TRANMERC_A_ERROR = 0x0040, TRANMERC_INV_F_ERROR = 0x0080;
    private final static int TRANMERC_SCALE_FACTOR_ERROR = 0x0100;
    private final static double PI = 3.14159265358979323, MAX_LAT = ((PI * 89.99) / 180.0);
    private final static double MAX_DELTA_LONG = ((PI * 90) / 180.0), MIN_SCALE_FACTOR = 0.3;
    private final static double MAX_SCALE_FACTOR = 3.0;
    private double tranMercA = 6378137.0, tranMercF = 1 / 298.257223563, tranMercES = 0.0066943799901413800;
    private double tranMercEBS = 0.0067394967565869, tranMercOriginLat = 0.0, tranMercOriginLong = 0.0;
    private double tranMercFalseNorthing = 0.0, tranMercFalseEasting = 0.0, tranMercScaleFactor = 1.0;
    private double tranMercAP = 6367449.1458008, tranMercBP = 16038.508696861, tranMercCP = 16.832613334334;
    private double tranMercDP = 0.021984404273757, tranMercEP = 3.1148371319283e-005;
    private double easting, northing;
    /**
     * Constructor
     */
    TMCoordConverter()
    {
    }

    /**
     * Get A
     * @return Returns A
     */
    public double getA()
    {
        return tranMercA;
    }

    /**
     * Get F
     * @return Returns F
     */
    public double getF()
    {
        return tranMercF;
    }

    /**
     * The function Set_Tranverse_Mercator_Parameters receives the ellipsoid parameters and Tranverse Mercator
     * projection parameters as inputs, and sets the corresponding state variables. If any errors occur, the error
     * code(s) are returned by the function, otherwise TRANMERC_NO_ERROR is returned.
     * @param a - Semi-major axis of ellipsoid, in meters
     * @param f - Flattening of ellipsoid
     * @param originLatitude - Latitude in radians at the origin of the projection
     * @param centralMeridian - Longitude in radians at the center of the projection
     * @param falseEasting - Easting/X at the center of the projection
     * @param falseNorthing - Northing/Y at the center of the projection
     * @param scaleFactor - Projection scale factor
     * @return error code
     */
    public long setTransverseMercatorParameters(double a, double f, double originLatitude,
        double centralMeridian, double falseEasting, double falseNorthing, double scaleFactor)
    {
        double tn, tn2, tn3, tn4, tn5, tranMercB, invF = 1 / f;
        long errorCode = TRANMERC_NO_ERROR;
        // Semi-major axis must be greater than zero
        if (a <= 0.0)
        {
            errorCode |= TRANMERC_A_ERROR;
        }
        // Inverse flattening must be between 250 and 350
        if ((invF < 250) || (invF > 350))
        {
            errorCode |= TRANMERC_INV_F_ERROR;
        }
        // origin latitude out of range
        if ((originLatitude < -MAX_LAT) || (originLatitude > MAX_LAT))
        {
            errorCode |= TRANMERC_ORIGIN_LAT_ERROR;
        }
        // origin longitude out of range
        if ((centralMeridian < -PI) || (centralMeridian > (2 * PI)))
        {
            errorCode |= TRANMERC_CENT_MER_ERROR;
        }
        if ((scaleFactor < MIN_SCALE_FACTOR) || (scaleFactor > MAX_SCALE_FACTOR))
        {
            errorCode |= TRANMERC_SCALE_FACTOR_ERROR;
        }
        // no errors
        if (errorCode == TRANMERC_NO_ERROR)
        {
            tranMercA = a;
            tranMercF = f;
            tranMercOriginLat = 0;
            tranMercOriginLong = 0;
            tranMercFalseNorthing = 0;
            tranMercFalseEasting = 0;
            tranMercScaleFactor = 1;
            // Eccentricity Squared
            tranMercES = 2 * tranMercF - tranMercF * tranMercF;
            // Second Eccentricity Squared
            tranMercEBS = (1 / (1 - tranMercES)) - 1;
            tranMercB = tranMercA * (1 - tranMercF);
            // True meridianal constants
            tn = (tranMercA - tranMercB) / (tranMercA + tranMercB);
            tn2 = tn * tn;
            tn3 = tn2 * tn;
            tn4 = tn3 * tn;
            tn5 = tn4 * tn;
            tranMercAP = tranMercA * (1.e0 - tn + 5.e0 * (tn2 - tn3) / 4.e0 + 81.e0 * (tn4 - tn5) / 64.e0);
            tranMercBP = 3.e0 * tranMercA * (tn - tn2 + 7.e0 * (tn3 - tn4) / 8.e0 + 55.e0 * tn5 / 64.e0) / 2.e0;
            tranMercCP = 15.e0 * tranMercA * (tn2 - tn3 + 3.e0 * (tn4 - tn5) / 4.e0) / 16.0;
            tranMercDP = 35.e0 * tranMercA * (tn3 - tn4 + 11.e0 * tn5 / 16.e0) / 48.e0;
            tranMercEP = 315.e0 * tranMercA * (tn4 - tn5) / 512.e0;
            convertGeodeticToTransverseMercator(MAX_LAT, MAX_DELTA_LONG);
            convertGeodeticToTransverseMercator(0, MAX_DELTA_LONG);
            tranMercOriginLat = originLatitude;
            if (centralMeridian > PI)
            {
                centralMeridian -= (2 * PI);
            }
            tranMercOriginLong = centralMeridian;
            tranMercFalseNorthing = falseNorthing;
            tranMercFalseEasting = falseEasting;
            tranMercScaleFactor = scaleFactor;
        }
        return errorCode;
    }

    /**
     * The function Convert_Geodetic_To_Transverse_Mercator converts geodetic (latitude and longitude) coordinates to
     * Transverse Mercator projection (easting and northing) coordinates, according to the current ellipsoid and
     * Transverse Mercator projection coordinates.  If any errors occur, the error code(s) are returned by the function,
     * otherwise TRANMERC_NO_ERROR is returned.
     * @param latitude - Latitude in radians
     * @param longitude - Longitude in radians
     * @return error code
     */
    public long convertGeodeticToTransverseMercator(double latitude, double longitude)
    {
        double c, c2, c3, c5, c7, dlam, eta, eta2, eta3, eta4, s, sn, t, tan2, tan3, tan4, tan5, tan6;
        double t1, t2, t3, t4, t5, t6, t7, t8, t9, tmd, tmdo;
        long errorCode = TRANMERC_NO_ERROR;
        double tempOrigin, tempLong;
        // Latitude out of range
        if ((latitude < -MAX_LAT) || (latitude > MAX_LAT))
        {
            errorCode |= TRANMERC_LAT_ERROR;
        }
        if (longitude > PI)
        {
            longitude -= (2 * PI);
        }
        if ((longitude < (tranMercOriginLong - MAX_DELTA_LONG)) || (longitude > (tranMercOriginLong + MAX_DELTA_LONG)))
        {
            if (longitude < 0)
            {
                tempLong = longitude + 2 * PI;
            }
            else
            {
                tempLong = longitude;
            }
            if (tranMercOriginLong < 0)
            {
                tempOrigin = tranMercOriginLong + 2 * PI;
            }
            else
            {
                tempOrigin = tranMercOriginLong;
            }
            if ((tempLong < (tempOrigin - MAX_DELTA_LONG)) || (tempLong > (tempOrigin + MAX_DELTA_LONG)))
            {
                errorCode |= TRANMERC_LON_ERROR;
            }
        }
        // no errors
        if (errorCode == TRANMERC_NO_ERROR)
        {
            // Delta Longitude
            dlam = longitude - tranMercOriginLong;
            // Distortion will result if Longitude is more than 9 degrees from the Central Meridian
            if (Math.abs(dlam) > (9.0 * PI / 180))
            {
                errorCode |= TRANMERC_LON_WARNING;
            }
            if (dlam > PI)
            {
                dlam -= (2 * PI);
            }
            if (dlam < -PI)
            {
                dlam += (2 * PI);
            }
            if (Math.abs(dlam) < 2.e-10)
            {
                dlam = 0.0;
            }
            s = Math.sin(latitude);
            c = Math.cos(latitude);
            c2 = c * c;
            c3 = c2 * c;
            c5 = c3 * c2;
            c7 = c5 * c2;
            t = Math.tan(latitude);
            tan2 = t * t;
            tan3 = tan2 * t;
            tan4 = tan3 * t;
            tan5 = tan4 * t;
            tan6 = tan5 * t;
            eta = tranMercEBS * c2;
            eta2 = eta * eta;
            eta3 = eta2 * eta;
            eta4 = eta3 * eta;
            // radius of curvature in prime vertical
            sn = tranMercA / Math.sqrt(1 - tranMercES * Math.pow(Math.sin(latitude), 2));
            // True Meridianal Distances
            tmd = tranMercAP * latitude - tranMercBP * Math.sin(2.0 * latitude)
                + tranMercCP * Math.sin(4.0 * latitude) - tranMercDP * Math.sin(6.0 * latitude)
                + tranMercEP * Math.sin(8.0 * latitude);
            // Origin
            tmdo = tranMercAP * tranMercOriginLat - tranMercBP * Math.sin(2.0 * tranMercOriginLat)
                + tranMercCP * Math.sin(4.0 * tranMercOriginLat) - tranMercDP * Math.sin(6.0 * tranMercOriginLat)
                + tranMercEP * Math.sin(8.0 * tranMercOriginLat);
            // northing
            t1 = (tmd - tmdo) * tranMercScaleFactor;
            t2 = sn * s * c * tranMercScaleFactor / 2.e0;
            t3 = sn * s * c3 * tranMercScaleFactor * (5.e0 - tan2 + 9.e0 * eta + 4.e0 * eta2) / 24.e0;
            t4 = sn * s * c5 * tranMercScaleFactor * (61.e0 - 58.e0 * tan2 + tan4 + 270.e0 * eta
                    - 330.e0 * tan2 * eta + 445.e0 * eta2 + 324.e0 * eta3 - 680.e0 * tan2 * eta2
                    + 88.e0 * eta4 - 600.e0 * tan2 * eta3 - 192.e0 * tan2 * eta4) / 720.e0;
            t5 = sn * s * c7 * tranMercScaleFactor * (1385.e0 - 3111.e0 * tan2 + 543.e0 * tan4 - tan6) / 40320.e0;
            northing = tranMercFalseNorthing + t1 + Math.pow(dlam, 2.e0) * t2 + Math.pow(dlam, 4.e0) * t3
                    + Math.pow(dlam, 6.e0) * t4 + Math.pow(dlam, 8.e0) * t5;
            // Easting
            t6 = sn * c * tranMercScaleFactor;
            t7 = sn * c3 * tranMercScaleFactor * (1.e0 - tan2 + eta) / 6.e0;
            t8 = sn * c5 * tranMercScaleFactor * (5.e0 - 18.e0 * tan2 + tan4 + 14.e0 * eta - 58.e0 * tan2
                    * eta + 13.e0 * eta2 + 4.e0 * eta3 - 64.e0 * tan2 * eta2 - 24.e0 * tan2 * eta3) / 120.e0;
            t9 = sn * c7 * tranMercScaleFactor * (61.e0 - 479.e0 * tan2 + 179.e0 * tan4 - tan6) / 5040.e0;
            easting = tranMercFalseEasting + dlam * t6 + Math.pow(dlam, 3.e0) * t7
                    + Math.pow(dlam, 5.e0) * t8 + Math.pow(dlam, 7.e0) * t9;
        }
        return errorCode;
    }

    /**
     * Get easting
     * @return Easting/X at the center of the projection
     */
    public double getEasting()
    {
        return easting;
    }

    /**
     * Get northing
     * @return Northing/Y at the center of the projection
     */
    public double getNorthing()
    {
        return northing;
    }
}