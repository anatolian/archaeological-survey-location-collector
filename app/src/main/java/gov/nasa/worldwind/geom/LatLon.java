/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.geom;
/**
 * Represents a point on the two-dimensional surface of a globe. Latitude is the degrees North and ranges between [-90,
 * 90], while longitude refers to degrees East, and ranges between (-180, 180].
 * Instances of <code>LatLon</code> are immutable.
 * @author Tom Gaskins
 * @version $Id$
 */
public class LatLon
{
    public final Angle LATITUDE;
    public final Angle LONGITUDE;
    /**
     * Constructs a new  <code>LatLon</code> from two angles. Neither angle may be null.
     * @param latitude - latitude
     * @param longitude - longitude
     * @throws IllegalArgumentException if <code>latitude</code> or <code>longitude</code> is null
     */
    public LatLon(Angle latitude, Angle longitude)
    {
        if (latitude == null || longitude == null)
        {
            throw new IllegalArgumentException("Latitude Or Longitude Is Null");
        }
        this.LATITUDE = latitude;
        this.LONGITUDE = longitude;
    }

    /**
     * Obtains the latitude of this <code>LatLon</code>.
     * @return this <code>LatLon</code>'s latitude
     */
    public final Angle getLatitude()
    {
        return this.LATITUDE;
    }

    /**
     * Obtains the longitude of this <code>LatLon</code>.
     * @return this <code>LatLon</code>'s longitude
     */
    public final Angle getLongitude()
    {
        return this.LONGITUDE;
    }

    /**
     * Add another latlong to this
     * @param that - other latlong
     * @return Returns the sum
     */
    public LatLon add(LatLon that)
    {
        if (that == null)
        {
            throw new IllegalArgumentException("Angle Is Null");
        }
        Angle lat = Angle.normalizedLatitude(this.LATITUDE.add(that.LATITUDE));
        Angle lon = Angle.normalizedLongitude(this.LONGITUDE.add(that.LONGITUDE));
        return new LatLon(lat, lon);
    }

    /**
     * Add a position to this
     * @param that - the position
     * @return Returns the sum
     */
    public LatLon add(Position that)
    {
        if (that == null)
        {
            throw new IllegalArgumentException("Angle Is Null");
        }
        Angle lat = Angle.normalizedLatitude(this.LATITUDE.add(that.getLatitude()));
        Angle lon = Angle.normalizedLongitude(this.LONGITUDE.add(that.getLongitude()));
        return new LatLon(lat, lon);
    }

    /**
     * Convert to string
     * @return Returns a string
     */
    @Override
    public String toString()
    {
        String las = String.format("Lat %7.4f\u00B0", this.getLatitude().getDegrees());
        String los = String.format("Lon %7.4f\u00B0", this.getLongitude().getDegrees());
        return "(" + las + ", " + los + ")";
    }

    /**
     * Compare this to another object for equality
     * @param o - other object
     * @return Returns whether the objects are equal
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        else if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        final gov.nasa.worldwind.geom.LatLon latLon = (gov.nasa.worldwind.geom.LatLon) o;
        if (!LATITUDE.equals(latLon.LATITUDE))
        {
            return false;
        }
        // noinspection RedundantIfStatement
        if (!LONGITUDE.equals(latLon.LONGITUDE))
        {
            return false;
        }
        return true;
    }

    /**
     * Hash
     * @return Returns the hashcode of the latlong
     */
    @Override
    public int hashCode()
    {
        return 29 * LATITUDE.hashCode() + LONGITUDE.hashCode();
    }
}
