/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.geom;
import android.support.annotation.NonNull;
/**
 * Represents a geometric angle. Instances of <code>Angle</code> are immutable. An angle can be obtained through the
 * factory methods {@link #fromDegrees} and {@link #fromRadians}.
 * @author Tom Gaskins
 * @version $Id$
 */
public class Angle implements Comparable<Angle>
{
    private final static double DEGREES_TO_RADIANS = Math.PI / 180d, RADIANS_TO_DEGREES = 180d / Math.PI;
    private final double DEGREES;
    public final double RADIANS;
    /**
     * Obtains an angle from a specified number of degrees.
     * @param degrees - the size in degrees of the angle to be obtained
     * @return a new angle, whose size in degrees is given by <code>degrees</code>
     */
    public static Angle fromDegrees(double degrees)
    {
        return new Angle(degrees, DEGREES_TO_RADIANS * degrees);
    }

    /**
     * Obtains an angle from a specified number of radians.
     * @param radians - the size in radians of the angle to be obtained.
     * @return a new angle, whose size in radians is given by <code>radians</code>.
     */
    public static Angle fromRadians(double radians)
    {
        return new Angle(RADIANS_TO_DEGREES * radians, radians);
    }

    /**
     * Constructor
     * @param degrees - angle degrees
     * @param radians - angle radians
     */
    private Angle(double degrees, double radians)
    {
        this.DEGREES = degrees;
        this.RADIANS = radians;
    }

    /**
     * Retrieves the size of this angle in degrees. This method may be faster than first obtaining the radians and then
     * converting to degrees.
     * @return the size of this angle in degrees.
     */
    public final double getDegrees()
    {
        return this.DEGREES;
    }

    /**
     * Obtains the sum of these two angles. Does not accept a null argument. This method is commutative, so
     * <code>a.add(b)</code> and <code>b.add(a)</code> are equivalent. Neither this angle nor angle is changed, instead
     * the result is returned as a new angle.
     * @param angle - the angle to add to this one.
     * @return an angle whose size is the total of this angles and angles size.
     * @throws IllegalArgumentException if angle is null.
     */
    public final Angle add(Angle angle)
    {
        if (angle == null)
        {
            throw new IllegalArgumentException("Angle Is Null");
        }
        return Angle.fromDegrees(this.DEGREES + angle.DEGREES);
    }

    /**
     * Multiplies this angle by <code>multiplier</code>. This angle remains unchanged. The result is returned as a new
     * angle.
     * @param multiplier a scalar by which this angle is multiplied.
     * @return a new angle whose size equals this angle's size multiplied by <code>multiplier</code>.
     */
    public final Angle multiply(double multiplier)
    {
        return Angle.fromDegrees(this.DEGREES * multiplier);
    }

    /**
     * Compares this {@link Angle} with another. Returns a negative integer if this is the smaller angle, a positive
     * integer if this is the larger, and zero if both angles are equal.
     * @param angle - the angle to compare against.
     * @return -1 if this angle is smaller, 0 if both are equal and +1 if this angle is larger.
     */
    public final int compareTo(@NonNull Angle angle)
    {
        return Double.compare(DEGREES, angle.DEGREES);
    }

    /**
     * Get normalized latitude
     * @param degrees - angle degrees
     * @return Returns the normalized latitude
     */
    private static double normalizedDegreesLatitude(double degrees)
    {
        double lat = degrees % 180;
        return lat > 90 ? 180 - lat : lat < -90 ? -180 - lat : lat;
    }

    /**
     * Get normalized longitude
     * @param degrees - angle degrees
     * @return Returns the normalized longitude
     */
    private static double normalizedDegreesLongitude(double degrees)
    {
        double lon = degrees % 360;
        return lon > 180 ? lon - 360 : lon < -180 ? 360 + lon : lon;
    }

    /**
     * Normalize latitude
     * @param unnormalizedAngle - old angle
     * @return Returns the normalized angle
     */
    public static Angle normalizedLatitude(Angle unnormalizedAngle)
    {
        if (unnormalizedAngle == null)
        {
            throw new IllegalArgumentException("Angle Is Null");
        }
        return Angle.fromDegrees(normalizedDegreesLatitude(unnormalizedAngle.DEGREES));
    }

    /**
     * Normalize longitude
     * @param unnormalizedAngle - old angle
     * @return Returns the normalized angle
     */
    public static Angle normalizedLongitude(Angle unnormalizedAngle)
    {
        if (unnormalizedAngle == null)
        {
            throw new IllegalArgumentException("Angle Is Null");
        }
        return Angle.fromDegrees(normalizedDegreesLongitude(unnormalizedAngle.DEGREES));
    }

    /**
     * Obtains a <code>String</code> representation of this angle.
     * @return the value of this angle in degrees and as a <code>String</code>.
     */
    @Override
    public final String toString()
    {
        return Double.toString(this.DEGREES) + '\u00B0';
    }

    /**
     * See if angles are equal
     * @param o - other object
     * @return Returns whether this object equals the other
     */
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
        Angle angle = (Angle) o;
        // noinspection RedundantIfStatement
        if (angle.DEGREES != this.DEGREES)
        {
            return false;
        }
        return true;
    }

    /**
     * Hash
     * @return Returns the hash code
     */
    public int hashCode()
    {
        long temp = DEGREES != +0.0d ? Double.doubleToLongBits(DEGREES) : 0L;
        return (int) (temp ^ (temp >>> 32));
    }
}