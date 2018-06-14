/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.geom.coords;
import gov.nasa.worldwind.geom.Angle;
/**
 * This class holds a set of Transverse Mercator coordinates along with the corresponding latitude and longitude.
 * @author Patrick Murris
 * @version $Id$
 * @see TMCoordConverter
 */
public class TMCoord
{
    private final Angle latitude, longitude;
    private final double easting, northing;
    /**
     * Create a set of Transverse Mercator coordinates from a pair of latitude and longitude,
     * for the given <code>Globe</code> and projection parameters.
     * @param latitude the latitude <code>Angle</code>.
     * @param longitude the longitude <code>Angle</code>.
     * @param a semi-major ellipsoid radius. If this and argument f are non-null and globe is null, will use the specfied a and f.
     * @param f ellipsoid flattening. If this and argument a are non-null and globe is null, will use the specfied a and f.
     * @param originLatitude the origin latitude <code>Angle</code>.
     * @param centralMeridian the central meridian longitude <code>Angle</code>.
     * @param falseEasting easting value at the center of the projection in meters.
     * @param falseNorthing northing value at the center of the projection in meters.
     * @param scale scaling factor.
     * @return the corresponding <code>TMCoord</code>.
     * @throws IllegalArgumentException if <code>latitude</code> or <code>longitude</code> is null,
     * or the conversion to TM coordinates fails. If the globe is null conversion will default
     * to using WGS84.
     */
    public static TMCoord fromLatLon(Angle latitude, Angle longitude, Double a, Double f, Angle originLatitude,
                                     Angle centralMeridian, double falseEasting, double falseNorthing,
                                     double scale)
    {
        if (latitude == null || longitude == null)
        {
            throw new IllegalArgumentException("Latitude Or Longitude Is Null");
        }
        if (originLatitude == null || centralMeridian == null)
        {
            throw new IllegalArgumentException("Angle Is Null");
        }
        final TMCoordConverter converter = new TMCoordConverter();
        if (a == null || f == null)
        {
            a = converter.getA();
            f = converter.getF();
        }
        long err = converter.setTransverseMercatorParameters(a, f, originLatitude.radians,
                centralMeridian.radians, falseEasting, falseNorthing, scale);
        if (err == TMCoordConverter.TRANMERC_NO_ERROR)
        {
            err = converter.convertGeodeticToTransverseMercator(latitude.radians, longitude.radians);
        }
        if (err != TMCoordConverter.TRANMERC_NO_ERROR && err != TMCoordConverter.TRANMERC_LON_WARNING)
        {
            throw new IllegalArgumentException("TM Conversion Error");
        }
        return new TMCoord(latitude, longitude, converter.getEasting(), converter.getNorthing(),
                originLatitude, centralMeridian);
    }

    /**
     * Create an arbitrary set of Transverse Mercator coordinates with the given values.
     * @param latitude the latitude <code>Angle</code>.
     * @param longitude the longitude <code>Angle</code>.
     * @param easting the easting distance value in meters.
     * @param northing the northing distance value in meters.
     * @param originLatitude the origin latitude <code>Angle</code>.
     * @param centralMeridian the central meridian longitude <code>Angle</code>.
     * @throws IllegalArgumentException if <code>latitude</code>, <code>longitude</code>, <code>originLatitude</code>
     * or <code>centralMeridian</code> is null.
     */
    private TMCoord(Angle latitude, Angle longitude, double easting, double northing, Angle originLatitude,
                    Angle centralMeridian)
    {
        if (latitude == null || longitude == null)
        {
            throw new IllegalArgumentException("Latitude Or Longitude Is Null");
        }
        if (originLatitude == null || centralMeridian == null)
        {
            throw new IllegalArgumentException("Angle Is Null");
        }
        this.latitude = latitude;
        this.longitude = longitude;
        this.easting = easting;
        this.northing = northing;
    }

    /**
     * Get latitude
     * @return Returns latitude
     */
    public Angle getLatitude()
    {
        return this.latitude;
    }

    /**
     * Get longitude
     * @return Returns longitude
     */
    public Angle getLongitude()
    {
        return this.longitude;
    }

    /**
     * Get easting
     * @return Returns easting
     */
    public double getEasting()
    {
        return this.easting;
    }

    /**
     * Get northing
     * @return Returns northing
     */
    public double getNorthing()
    {
        return this.northing;
    }
}