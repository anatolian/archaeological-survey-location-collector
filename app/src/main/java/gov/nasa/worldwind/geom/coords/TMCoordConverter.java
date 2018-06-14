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
    private double TranMerc_a = 6378137.0, TranMerc_f = 1 / 298.257223563, TranMerc_es = 0.0066943799901413800;
    private double TranMerc_ebs = 0.0067394967565869, TranMerc_Origin_Lat = 0.0, TranMerc_Origin_Long = 0.0;
    private double TranMerc_False_Northing = 0.0, TranMerc_False_Easting = 0.0, TranMerc_Scale_Factor = 1.0;
    private double TranMerc_ap = 6367449.1458008, TranMerc_bp = 16038.508696861, TranMerc_cp = 16.832613334334;
    private double TranMerc_dp = 0.021984404273757, TranMerc_ep = 3.1148371319283e-005;
    private double Easting, Northing;
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
        return TranMerc_a;
    }

    /**
     * Get F
     * @return Returns F
     */
    public double getF()
    {
        return TranMerc_f;
    }

    /**
     * The function Set_Tranverse_Mercator_Parameters receives the ellipsoid parameters and Tranverse Mercator
     * projection parameters as inputs, and sets the corresponding state variables. If any errors occur, the error
     * code(s) are returned by the function, otherwise TRANMERC_NO_ERROR is returned.
     * @param a - Semi-major axis of ellipsoid, in meters
     * @param f - Flattening of ellipsoid
     * @param Origin_Latitude - Latitude in radians at the origin of the projection
     * @param Central_Meridian - Longitude in radians at the center of the projection
     * @param False_Easting - Easting/X at the center of the projection
     * @param False_Northing - Northing/Y at the center of the projection
     * @param Scale_Factor - Projection scale factor
     * @return error code
     */
    public long setTransverseMercatorParameters(double a, double f, double Origin_Latitude,
        double Central_Meridian, double False_Easting, double False_Northing, double Scale_Factor)
    {
        double tn, tn2, tn3, tn4, tn5, TranMerc_b, inv_f = 1 / f;
        long Error_Code = TRANMERC_NO_ERROR;
        // Semi-major axis must be greater than zero
        if (a <= 0.0)
        {
            Error_Code |= TRANMERC_A_ERROR;
        }
        // Inverse flattening must be between 250 and 350
        if ((inv_f < 250) || (inv_f > 350))
        {
            Error_Code |= TRANMERC_INV_F_ERROR;
        }
        // origin latitude out of range
        if ((Origin_Latitude < -MAX_LAT) || (Origin_Latitude > MAX_LAT))
        {
            Error_Code |= TRANMERC_ORIGIN_LAT_ERROR;
        }
        // origin longitude out of range
        if ((Central_Meridian < -PI) || (Central_Meridian > (2 * PI)))
        {
            Error_Code |= TRANMERC_CENT_MER_ERROR;
        }
        if ((Scale_Factor < MIN_SCALE_FACTOR) || (Scale_Factor > MAX_SCALE_FACTOR))
        {
            Error_Code |= TRANMERC_SCALE_FACTOR_ERROR;
        }
        // no errors
        if (Error_Code == TRANMERC_NO_ERROR)
        {
            TranMerc_a = a;
            TranMerc_f = f;
            TranMerc_Origin_Lat = 0;
            TranMerc_Origin_Long = 0;
            TranMerc_False_Northing = 0;
            TranMerc_False_Easting = 0;
            TranMerc_Scale_Factor = 1;
            // Eccentricity Squared
            TranMerc_es = 2 * TranMerc_f - TranMerc_f * TranMerc_f;
            // Second Eccentricity Squared
            TranMerc_ebs = (1 / (1 - TranMerc_es)) - 1;
            TranMerc_b = TranMerc_a * (1 - TranMerc_f);
            // True meridianal constants
            tn = (TranMerc_a - TranMerc_b) / (TranMerc_a + TranMerc_b);
            tn2 = tn * tn;
            tn3 = tn2 * tn;
            tn4 = tn3 * tn;
            tn5 = tn4 * tn;
            TranMerc_ap = TranMerc_a * (1.e0 - tn + 5.e0 * (tn2 - tn3) / 4.e0 + 81.e0 * (tn4 - tn5) / 64.e0);
            TranMerc_bp = 3.e0 * TranMerc_a * (tn - tn2 + 7.e0 * (tn3 - tn4) / 8.e0 + 55.e0 * tn5 / 64.e0) / 2.e0;
            TranMerc_cp = 15.e0 * TranMerc_a * (tn2 - tn3 + 3.e0 * (tn4 - tn5) / 4.e0) / 16.0;
            TranMerc_dp = 35.e0 * TranMerc_a * (tn3 - tn4 + 11.e0 * tn5 / 16.e0) / 48.e0;
            TranMerc_ep = 315.e0 * TranMerc_a * (tn4 - tn5) / 512.e0;
            convertGeodeticToTransverseMercator(MAX_LAT, MAX_DELTA_LONG);
            convertGeodeticToTransverseMercator(0, MAX_DELTA_LONG);
            TranMerc_Origin_Lat = Origin_Latitude;
            if (Central_Meridian > PI)
            {
                Central_Meridian -= (2 * PI);
            }
            TranMerc_Origin_Long = Central_Meridian;
            TranMerc_False_Northing = False_Northing;
            TranMerc_False_Easting = False_Easting;
            TranMerc_Scale_Factor = Scale_Factor;
        }
        return (Error_Code);
    }

    /**
     * The function Convert_Geodetic_To_Transverse_Mercator converts geodetic (latitude and longitude) coordinates to
     * Transverse Mercator projection (easting and northing) coordinates, according to the current ellipsoid and
     * Transverse Mercator projection coordinates.  If any errors occur, the error code(s) are returned by the function,
     * otherwise TRANMERC_NO_ERROR is returned.
     * @param Latitude - Latitude in radians
     * @param Longitude - Longitude in radians
     * @return error code
     */
    public long convertGeodeticToTransverseMercator(double Latitude, double Longitude)
    {
        double c, c2, c3, c5, c7, dlam, eta, eta2, eta3, eta4, s, sn, t, tan2, tan3, tan4, tan5, tan6;
        double t1, t2, t3, t4, t5, t6, t7, t8, t9, tmd, tmdo;
        long Error_Code = TRANMERC_NO_ERROR;
        double temp_Origin, temp_Long;
        // Latitude out of range
        if ((Latitude < -MAX_LAT) || (Latitude > MAX_LAT))
        {
            Error_Code |= TRANMERC_LAT_ERROR;
        }
        if (Longitude > PI)
        {
            Longitude -= (2 * PI);
        }
        if ((Longitude < (TranMerc_Origin_Long - MAX_DELTA_LONG)) || (Longitude > (TranMerc_Origin_Long + MAX_DELTA_LONG)))
        {
            if (Longitude < 0)
            {
                temp_Long = Longitude + 2 * PI;
            }
            else
            {
                temp_Long = Longitude;
            }
            if (TranMerc_Origin_Long < 0)
            {
                temp_Origin = TranMerc_Origin_Long + 2 * PI;
            }
            else
            {
                temp_Origin = TranMerc_Origin_Long;
            }
            if ((temp_Long < (temp_Origin - MAX_DELTA_LONG)) || (temp_Long > (temp_Origin + MAX_DELTA_LONG)))
            {
                Error_Code |= TRANMERC_LON_ERROR;
            }
        }
        // no errors
        if (Error_Code == TRANMERC_NO_ERROR)
        {
            // Delta Longitude
            dlam = Longitude - TranMerc_Origin_Long;
            // Distortion will result if Longitude is more than 9 degrees from the Central Meridian
            if (Math.abs(dlam) > (9.0 * PI / 180))
            {
                Error_Code |= TRANMERC_LON_WARNING;
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
            s = Math.sin(Latitude);
            c = Math.cos(Latitude);
            c2 = c * c;
            c3 = c2 * c;
            c5 = c3 * c2;
            c7 = c5 * c2;
            t = Math.tan(Latitude);
            tan2 = t * t;
            tan3 = tan2 * t;
            tan4 = tan3 * t;
            tan5 = tan4 * t;
            tan6 = tan5 * t;
            eta = TranMerc_ebs * c2;
            eta2 = eta * eta;
            eta3 = eta2 * eta;
            eta4 = eta3 * eta;
            // radius of curvature in prime vertical
            sn = TranMerc_a / Math.sqrt(1 - TranMerc_es * Math.pow(Math.sin(Latitude), 2));
            // True Meridianal Distances
            tmd = TranMerc_ap * Latitude - TranMerc_bp * Math.sin(2.0 * Latitude)
                + TranMerc_cp * Math.sin(4.0 * Latitude) - TranMerc_dp * Math.sin(6.0 * Latitude)
                + TranMerc_ep * Math.sin(8.0 * Latitude);
            // Origin
            tmdo = TranMerc_ap * TranMerc_Origin_Lat - TranMerc_bp * Math.sin(2.0 * TranMerc_Origin_Lat)
                + TranMerc_cp * Math.sin(4.0 * TranMerc_Origin_Lat) - TranMerc_dp * Math.sin(6.0 * TranMerc_Origin_Lat)
                + TranMerc_ep * Math.sin(8.0 * TranMerc_Origin_Lat);
            // northing
            t1 = (tmd - tmdo) * TranMerc_Scale_Factor;
            t2 = sn * s * c * TranMerc_Scale_Factor / 2.e0;
            t3 = sn * s * c3 * TranMerc_Scale_Factor * (5.e0 - tan2 + 9.e0 * eta + 4.e0 * eta2) / 24.e0;
            t4 = sn * s * c5 * TranMerc_Scale_Factor * (61.e0 - 58.e0 * tan2 + tan4 + 270.e0 * eta
                    - 330.e0 * tan2 * eta + 445.e0 * eta2 + 324.e0 * eta3 - 680.e0 * tan2 * eta2
                    + 88.e0 * eta4 - 600.e0 * tan2 * eta3 - 192.e0 * tan2 * eta4) / 720.e0;
            t5 = sn * s * c7 * TranMerc_Scale_Factor * (1385.e0 - 3111.e0 * tan2 + 543.e0 * tan4 - tan6) / 40320.e0;
            Northing = TranMerc_False_Northing + t1 + Math.pow(dlam, 2.e0) * t2 + Math.pow(dlam, 4.e0) * t3
                    + Math.pow(dlam, 6.e0) * t4 + Math.pow(dlam, 8.e0) * t5;
            // Easting
            t6 = sn * c * TranMerc_Scale_Factor;
            t7 = sn * c3 * TranMerc_Scale_Factor * (1.e0 - tan2 + eta) / 6.e0;
            t8 = sn * c5 * TranMerc_Scale_Factor * (5.e0 - 18.e0 * tan2 + tan4 + 14.e0 * eta
                    - 58.e0 * tan2 * eta + 13.e0 * eta2 + 4.e0 * eta3 - 64.e0 * tan2 * eta2 - 24.e0 * tan2 * eta3) / 120.e0;
            t9 = sn * c7 * TranMerc_Scale_Factor * (61.e0 - 479.e0 * tan2 + 179.e0 * tan4 - tan6) / 5040.e0;
            Easting = TranMerc_False_Easting + dlam * t6 + Math.pow(dlam, 3.e0) * t7
                    + Math.pow(dlam, 5.e0) * t8 + Math.pow(dlam, 7.e0) * t9;
        }
        return (Error_Code);
    }

    /**
     * Get easting
     * @return Easting/X at the center of the projection
     */
    public double getEasting()
    {
        return Easting;
    }

    /**
     * Get northing
     * @return Northing/Y at the center of the projection
     */
    public double getNorthing()
    {
        return Northing;
    }
}