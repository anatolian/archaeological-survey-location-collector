/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.geom;
/**
 * @author tag
 * @version $Id$
 */
public class Position extends LatLon
{
    private final double ELEVATION;
    /**
     * Constructor
     * @param latitude - latitude measurement
     * @param longitude - longitude measurement
     * @param elevation - altitude
     */
    private Position(Angle latitude, Angle longitude, double elevation)
    {
        super(latitude, longitude);
        this.ELEVATION = elevation;
    }

    /**
     * Obtains the elevation of this position
     * @return this position's elevation
     */
    public double getElevation()
    {
        return this.ELEVATION;
    }

    /**
     * Obtains the elevation of this position
     * @return this position's elevation
     */
    public double getAltitude()
    {
        return this.ELEVATION;
    }

    /**
     * Add two positions
     * @param that - the position
     * @return Returns the sum
     */
    public Position add(Position that)
    {
        Angle lat = Angle.normalizedLatitude(this.LATITUDE.add(that.LATITUDE));
        Angle lon = Angle.normalizedLongitude(this.LONGITUDE.add(that.LONGITUDE));
        return new Position(lat, lon, this.ELEVATION + that.ELEVATION);
    }

    /**
     * Equality check this position with another object
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
        else if (!super.equals(o))
        {
            return false;
        }
        Position position = (Position) o;
        // noinspection RedundantIfStatement
        if (Double.compare(position.ELEVATION, ELEVATION) != 0)
        {
            return false;
        }
        return true;
    }

    /**
     * Hash
     * @return Returns the hash code
     */
    @Override
    public int hashCode()
    {
        long temp = ELEVATION != +0.0d ? Double.doubleToLongBits(ELEVATION) : 0L;
        return 31 * super.hashCode() + (int) (temp ^ (temp >>> 32));
    }

    /**
     * Get a string representation of the position
     * @return Returns a string representation of the string
     */
    public String toString()
    {
        return "(" + this.LATITUDE.toString() + ", " + this.LONGITUDE.toString() + ", " + this.ELEVATION + ")";
    }
}