/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.geom;
import gov.nasa.worldwind.util.*;
/**
 * Represents a point on the two-dimensional surface of a globe. Latitude is the degrees North and ranges between [-90,
 * 90], while longitude refers to degrees East, and ranges between (-180, 180].
 * Instances of <code>LatLon</code> are immutable.
 * @author Tom Gaskins
 * @version $Id$
 */
public class LatLon
{
    public final Angle latitude;
    public final Angle longitude;
    /**
     * Factor method for obtaining a new <code>LatLon</code> from two angles expressed in radians.
     * @param latitude - in radians
     * @param longitude - in radians
     * @return a new <code>LatLon</code> from the given angles, which are expressed as radians
     */
    public static LatLon fromRadians(double latitude, double longitude)
    {
        return new LatLon(Math.toDegrees(latitude), Math.toDegrees(longitude));
    }

    /**
     * Factory method for obtaining a new <code>LatLon</code> from two angles expressed in degrees.
     * @param latitude - in degrees
     * @param longitude - in degrees
     * @return a new <code>LatLon</code> from the given angles, which are expressed as degrees
     */
    public static LatLon fromDegrees(double latitude, double longitude)
    {
        return new LatLon(latitude, longitude);
    }

    /**
     * Constructor
     * @param latitude - in degrees
     * @param longitude - in degrees
     */
    private LatLon(double latitude, double longitude)
    {
        this.latitude = Angle.fromDegrees(latitude);
        this.longitude = Angle.fromDegrees(longitude);
    }

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
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Constructor
     * @param latLon - a latitude longitude pair
     */
    public LatLon(LatLon latLon)
    {
        if (latLon == null)
        {
            throw new IllegalArgumentException("Lat Lon Is Null");
        }
        this.latitude = latLon.latitude;
        this.longitude = latLon.longitude;
    }

    /**
     * Obtains the latitude of this <code>LatLon</code>.
     * @return this <code>LatLon</code>'s latitude
     */
    public final Angle getLatitude()
    {
        return this.latitude;
    }

    /**
     * Obtains the longitude of this <code>LatLon</code>.
     * @return this <code>LatLon</code>'s longitude
     */
    public final Angle getLongitude()
    {
        return this.longitude;
    }

    /**
     * Returns the linear interpolation of <code>value1</code> and <code>value2</code>, treating the geographic
     * locations as simple 2D coordinate pairs.
     * @param amount - the interpolation factor
     * @param value1 - the first location.
     * @param value2 - the second location.
     * @return the linear interpolation of <code>value1</code> and <code>value2</code>.
     * @throws IllegalArgumentException if either location is null.
     */
    public static LatLon interpolate(double amount, LatLon value1, LatLon value2)
    {
        if (value1 == null || value2 == null)
        {
            throw new IllegalArgumentException("Lat Lon Is Null");
        }
        if (LatLon.equals(value1, value2))
        {
            return value1;
        }
        Line line;
        try
        {
            line = Line.fromSegment(new Vec4(value1.getLongitude().radians, value1.getLatitude().radians, 0),
                    new Vec4(value2.getLongitude().radians, value2.getLatitude().radians, 0));
        }
        catch (IllegalArgumentException e)
        {
            // Locations became coincident after calculations.
            return value1;
        }
        Vec4 p = line.getPointAt(amount);
        return LatLon.fromRadians(p.y(), p.x);
    }

    /**
     * Returns the an interpolated location along the great-arc between <code>value1</code> and <code>value2</code>. The
     * interpolation factor <code>amount</code> defines the weight given to each value, and is clamped to the range [0,
     * 1]. If <code>a</code> is 0 or less, this returns <code>value1</code>. If <code>amount</code> is 1 or more, this
     * returns <code>value2</code>. Otherwise, this returns the location on the great-arc between <code>value1</code>
     * and <code>value2</code> corresponding to the specified interpolation factor.
     * @param amount - the interpolation factor
     * @param value1 - the first location.
     * @param value2 - the second location.
     * @return an interpolated location along the great-arc between <code>value1</code> and <code>value2</code>.
     * @throws IllegalArgumentException if either location is null.
     */
    public static LatLon interpolateGreatCircle(double amount, LatLon value1, LatLon value2)
    {
        if (value1 == null || value2 == null)
        {
            throw new IllegalArgumentException("Lat Lon Is Null");
        }
        if (LatLon.equals(value1, value2))
        {
            return value1;
        }
        double t = WWMath.clamp(amount, 0d, 1d);
        Angle azimuth = LatLon.greatCircleAzimuth(value1, value2);
        Angle distance = LatLon.greatCircleDistance(value1, value2);
        Angle pathLength = Angle.fromDegrees(t * distance.degrees);
        return LatLon.greatCircleEndPosition(value1, azimuth, pathLength);
    }

    /**
     * Returns the an interpolated location along the rhumb line between <code>value1</code> and <code>value2</code>.
     * The interpolation factor <code>amount</code> defines the weight given to each value, and is clamped to the range
     * [0, 1]. If <code>a</code> is 0 or less, this returns <code>value1</code>. If <code>amount</code> is 1 or more,
     * this returns <code>value2</code>. Otherwise, this returns the location on the rhumb line between
     * <code>value1</code> and <code>value2</code> corresponding to the specified interpolation factor.
     * @param amount - the interpolation factor
     * @param value1 - the first location.
     * @param value2 - the second location.
     * @return an interpolated location along the rhumb line between <code>value1</code> and <code>value2</code>
     * @throws IllegalArgumentException if either location is null.
     */
    public static LatLon interpolateRhumb(double amount, LatLon value1, LatLon value2)
    {
        if (value1 == null || value2 == null)
        {
            throw new IllegalArgumentException("Lat Lon Is Null");
        }
        if (LatLon.equals(value1, value2))
        {
            return value1;
        }
        double t = WWMath.clamp(amount, 0d, 1d);
        Angle azimuth = LatLon.rhumbAzimuth(value1, value2);
        Angle distance = LatLon.rhumbDistance(value1, value2);
        Angle pathLength = Angle.fromDegrees(t * distance.degrees);
        return LatLon.rhumbEndPosition(value1, azimuth, pathLength);
    }

    /**
     * Computes the great circle angular distance between two locations. The return value gives the distance as the
     * angle between the two positions on the pi radius circle. In radians, this angle is also the arc length of the
     * segment between the two positions on that circle. To compute a distance in meters from this value, multiply it by
     * the radius of the globe.
     * @param p1 - LatLon of the first location
     * @param p2 - LatLon of the second location
     * @return the angular distance between the two locations. In radians, this value is the arc length
     *     on the radius pi circle.
     */
    public static Angle greatCircleDistance(LatLon p1, LatLon p2)
    {
        if ((p1 == null) || (p2 == null))
        {
            throw new IllegalArgumentException("Lat Lon Is Null");
        }
        double lat1 = p1.getLatitude().radians;
        double lon1 = p1.getLongitude().radians;
        double lat2 = p2.getLatitude().radians;
        double lon2 = p2.getLongitude().radians;
        if (lat1 == lat2 && lon1 == lon2)
        {
            return Angle.ZERO;
        }
        // "Haversine formula," taken from http://en.wikipedia.org/wiki/Great-circle_distance#Formul.C3.A6
        double a = Math.sin((lat2 - lat1) / 2.0);
        double b = Math.sin((lon2 - lon1) / 2.0);
        double c = a * a + +Math.cos(lat1) * Math.cos(lat2) * b * b;
        double distanceRadians = 2.0 * Math.asin(Math.sqrt(c));
        return Double.isNaN(distanceRadians) ? Angle.ZERO : Angle.fromRadians(distanceRadians);
    }

    /**
     * Computes the azimuth angle (clockwise from North) that points from the first location to the second location.
     * This angle can be used as the starting azimuth for a great circle arc that begins at the first location, and
     * passes through the second location.
     * @param p1 - LatLon of the first location
     * @param p2 - LatLon of the second location
     * @return Angle that points from the first location to the second location.
     */
    public static Angle greatCircleAzimuth(LatLon p1, LatLon p2)
    {
        if ((p1 == null) || (p2 == null))
        {
            throw new IllegalArgumentException("Lat Lon Is Null");
        }
        double lat1 = p1.getLatitude().radians;
        double lon1 = p1.getLongitude().radians;
        double lat2 = p2.getLatitude().radians;
        double lon2 = p2.getLongitude().radians;
        if (lat1 == lat2 && lon1 == lon2)
        {
            return Angle.ZERO;
        }
        if (lon1 == lon2)
        {
            return lat1 > lat2 ? Angle.POS180 : Angle.ZERO;
        }
        // Taken from "Map Projections - A Working Manual", page 30, equation 5-4b.
        // The atan2() function is used in place of the traditional atan(y/x) to simplify the case when x==0.
        double y = Math.cos(lat2) * Math.sin(lon2 - lon1);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1);
        double azimuthRadians = Math.atan2(y, x);
        return Double.isNaN(azimuthRadians) ? Angle.ZERO : Angle.fromRadians(azimuthRadians);
    }

    /**
     * Computes the location on a great circle arc with the given starting location, azimuth, and arc distance.
     * @param p - LatLon of the starting location
     * @param greatCircleAzimuth - great circle azimuth angle (clockwise from North)
     * @param pathLength - arc distance to travel
     * @return LatLon location on the great circle arc.
     */
    public static LatLon greatCircleEndPosition(LatLon p, Angle greatCircleAzimuth, Angle pathLength)
    {
        if (p == null)
        {	
            throw new IllegalArgumentException("Lat Lon Is Null");
        }
        else if (greatCircleAzimuth == null || pathLength == null)
        {
            throw new IllegalArgumentException("Angle Is Null");
        }
        double lat = p.getLatitude().radians;
        double lon = p.getLongitude().radians;
        double azimuth = greatCircleAzimuth.radians;
        double distance = pathLength.radians;
        if (distance == 0)
        {
            return p;
        }
        // Taken from "Map Projections - A Working Manual", page 31, equation 5-5 and 5-6.
        double endLatRadians = Math.asin(Math.sin(lat) * Math.cos(distance) + Math.cos(lat)
                * Math.sin(distance) * Math.cos(azimuth));
        double endLonRadians = lon + Math.atan2(Math.sin(distance) * Math.sin(azimuth),
            Math.cos(lat) * Math.cos(distance) - Math.sin(lat) * Math.sin(distance) * Math.cos(azimuth));
        if (Double.isNaN(endLatRadians) || Double.isNaN(endLonRadians))
        {
            return p;
        }
        return new LatLon(Angle.fromRadians(endLatRadians).normalizedLatitude(),
                Angle.fromRadians(endLonRadians).normalizedLongitude());
    }

    /**
     * Computes the length of the rhumb line between two locations. The return value gives the distance as the angular
     * distance between the two positions on the pi radius circle. In radians, this angle is also the arc length of the
     * segment between the two positions on that circle. To compute a distance in meters from this value, multiply it by
     * the radius of the globe.
     * @param p1 - LatLon of the first location
     * @param p2 - LatLon of the second location
     * @return the arc length of the rhumb line between the two locations. In radians, this value is the arc length on
     *         the radius pi circle.
     */
    private static Angle rhumbDistance(LatLon p1, LatLon p2)
    {
        if (p1 == null || p2 == null)
        {
            throw new IllegalArgumentException("LatLon Is Null");
        }
        double lat1 = p1.getLatitude().radians;
        double lon1 = p1.getLongitude().radians;
        double lat2 = p2.getLatitude().radians;
        double lon2 = p2.getLongitude().radians;
        if (lat1 == lat2 && lon1 == lon2)
        {
            return Angle.ZERO;
        }
        // Taken from http://www.movable-type.co.uk/scripts/latlong.html
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        double dPhi = Math.log(Math.tan(lat2 / 2.0 + Math.PI / 4.0) / Math.tan(lat1 / 2.0 + Math.PI / 4.0));
        double q = dLat / dPhi;
        if (Double.isNaN(dPhi) || Double.isNaN(q))
        {
            q = Math.cos(lat1);
        }
        // If lonChange over 180 take shorter rhumb across 180 meridian.
        if (Math.abs(dLon) > Math.PI)
        {
            dLon = dLon > 0 ? -(2 * Math.PI - dLon) : (2 * Math.PI + dLon);
        }
        double distanceRadians = Math.sqrt(dLat * dLat + q * q * dLon * dLon);
        return Double.isNaN(distanceRadians) ? Angle.ZERO : Angle.fromRadians(distanceRadians);
    }

    /**
     * Computes the azimuth angle (clockwise from North) of a rhumb line (a line of constant heading) between two
     * locations.
     * @param p1 - LatLon of the first location
     * @param p2 - LatLon of the second location
     * @return azimuth Angle of a rhumb line between the two locations.
     */
    private static Angle rhumbAzimuth(LatLon p1, LatLon p2)
    {
        if (p1 == null || p2 == null)
        {
            throw new IllegalArgumentException("LatLon Is Null");
        }
        double lat1 = p1.getLatitude().radians;
        double lon1 = p1.getLongitude().radians;
        double lat2 = p2.getLatitude().radians;
        double lon2 = p2.getLongitude().radians;
        if (lat1 == lat2 && lon1 == lon2)
        {
            return Angle.ZERO;
        }
        // Taken from http://www.movable-type.co.uk/scripts/latlong.html
        double dLon = lon2 - lon1;
        double dPhi = Math.log(Math.tan(lat2 / 2.0 + Math.PI / 4.0) / Math.tan(lat1 / 2.0 + Math.PI / 4.0));
        // If lonChange over 180 take shorter rhumb across 180 meridian.
        if (Math.abs(dLon) > Math.PI)
        {
            dLon = dLon > 0 ? -(2 * Math.PI - dLon) : (2 * Math.PI + dLon);
        }
        double azimuthRadians = Math.atan2(dLon, dPhi);
        return Double.isNaN(azimuthRadians) ? Angle.ZERO : Angle.fromRadians(azimuthRadians);
    }

    /**
     * Computes the location on a rhumb line with the given starting location, rhumb azimuth, and
     * arc distance along the line.
     * @param p - LatLon of the starting location
     * @param rhumbAzimuth - rhumb azimuth angle (clockwise from North)
     * @param pathLength - arc distance to travel
     * @return LatLon location on the rhumb line.
     */
    private static LatLon rhumbEndPosition(LatLon p, Angle rhumbAzimuth, Angle pathLength)
    {
        if (p == null)
        {
            throw new IllegalArgumentException("LatLon Is Null");
        }
        else if (rhumbAzimuth == null || pathLength == null)
        {
            throw new IllegalArgumentException("Angle Is Null");
        }
        double lat1 = p.getLatitude().radians;
        double lon1 = p.getLongitude().radians;
        double azimuth = rhumbAzimuth.radians;
        double distance = pathLength.radians;
        if (distance == 0)
        {
            return p;
        }
        // Taken from http://www.movable-type.co.uk/scripts/latlong.html
        double lat2 = lat1 + distance * Math.cos(azimuth);
        double dPhi = Math.log(Math.tan(lat2 / 2.0 + Math.PI / 4.0) / Math.tan(lat1 / 2.0 + Math.PI / 4.0));
        double q = (lat2 - lat1) / dPhi;
        if (Double.isNaN(dPhi) || Double.isNaN(q) || Double.isInfinite(q))
        {
            q = Math.cos(lat1);
        }
        double dLon = distance * Math.sin(azimuth) / q;
        // Handle latitude passing over either pole.
        if (Math.abs(lat2) > Math.PI / 2.0)
        {
            lat2 = lat2 > 0 ? Math.PI - lat2 : -Math.PI - lat2;
        }
        double lon2 = (lon1 + dLon + Math.PI) % (2 * Math.PI) - Math.PI;
        if (Double.isNaN(lat2) || Double.isNaN(lon2))
        {
            return p;
        }
        return new LatLon(Angle.fromRadians(lat2).normalizedLatitude(), Angle.fromRadians(lon2).normalizedLongitude());
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
        Angle lat = Angle.normalizedLatitude(this.latitude.add(that.latitude));
        Angle lon = Angle.normalizedLongitude(this.longitude.add(that.longitude));
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
        Angle lat = Angle.normalizedLatitude(this.latitude.add(that.getLatitude()));
        Angle lon = Angle.normalizedLongitude(this.longitude.add(that.getLongitude()));
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
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        final gov.nasa.worldwind.geom.LatLon latLon = (gov.nasa.worldwind.geom.LatLon) o;
        if (!latitude.equals(latLon.latitude))
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if (!longitude.equals(latLon.longitude))
        {
            return false;
        }
        return true;
    }

    /**
     * Compare two latlongs
     * @param a - first
     * @param b - second
     * @return Returns whether they are equal
     */
    public static boolean equals(LatLon a, LatLon b)
    {
        return a.getLatitude().equals(b.getLatitude()) && a.getLongitude().equals(b.getLongitude());
    }

    /**
     * Hash
     * @return Returns the hashcode of the latlong
     */
    @Override
    public int hashCode()
    {
        return 29 * latitude.hashCode() + longitude.hashCode();
    }
}
