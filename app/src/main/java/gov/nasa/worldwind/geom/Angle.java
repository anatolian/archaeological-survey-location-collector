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
    public final static Angle ZERO = Angle.fromDegrees(0), POS90 = Angle.fromDegrees(90);
    public final static Angle NEG90 = Angle.fromDegrees(-90), POS180 = Angle.fromDegrees(180);
    public final static Angle NEG180 = Angle.fromDegrees(-180);
    private final static double DEGREES_TO_RADIANS = Math.PI / 180d, RADIANS_TO_DEGREES = 180d / Math.PI;
    public final double degrees, radians;
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
     * @param angle - the angle
     */
    public Angle(Angle angle)
    {
        this.degrees = angle.degrees;
        this.radians = angle.radians;
    }

    /**
     * Constructor
     * @param degrees - angle degrees
     * @param radians - angle radians
     */
    private Angle(double degrees, double radians)
    {
        this.degrees = degrees;
        this.radians = radians;
    }

    /**
     * Retrieves the size of this angle in degrees. This method may be faster than first obtaining the radians and then
     * converting to degrees.
     * @return the size of this angle in degrees.
     */
    public final double getDegrees()
    {
        return this.degrees;
    }

    /**
     * Retrieves the size of this angle in radians. This may be useful for <code>java.lang.Math</code> functions, which
     * generally take radians as trigonometric arguments. This method may be faster that first obtaining the degrees and
     * then converting to radians.
     * @return the size of this angle in radians.
     */
    public final double getRadians()
    {
        return this.radians;
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
        return Angle.fromDegrees(this.degrees + angle.degrees);
    }

    /**
     * Obtains the difference of these two angles. Does not accept a null argument. This method is not commutative.
     * Neither this angle nor angle is changed, instead the result is returned as a new angle.
     * @param angle - the angle to subtract from this angle.
     * @return a new angle corresponding to this angle's size minus angle's size.
     * @throws IllegalArgumentException if angle is null.
     */
    public final Angle subtract(Angle angle)
    {
        if (angle == null)
        {
            throw new IllegalArgumentException("Angle Is Null");
        }
        return Angle.fromDegrees(this.degrees - angle.degrees);
    }

    /**
     * Multiplies this angle by <code>multiplier</code>. This angle remains unchanged. The result is returned as a new
     * angle.
     * @param multiplier a scalar by which this angle is multiplied.
     * @return a new angle whose size equals this angle's size multiplied by <code>multiplier</code>.
     */
    public final Angle multiply(double multiplier)
    {
        return Angle.fromDegrees(this.degrees * multiplier);
    }

    /**
     * Obtains the sine of this angle.
     * @return the trigonometric sine of this angle.
     */
    public final double sin()
    {
        return Math.sin(this.radians);
    }

    /**
     * Half sine
     * @return Returns half sine
     */
    public final double sinHalfAngle()
    {
        return Math.sin(0.5 * this.radians);
    }

    /**
     * Obtains the cosine of this angle.
     * @return the trigonometric cosine of this angle.
     */
    public final double cos()
    {
        return Math.cos(this.radians);
    }

    /**
     * Half cosine
     * @return Returns half cosine
     */
    public final double cosHalfAngle()
    {
        return Math.cos(0.5 * this.radians);
    }

    /**
     * Obtains the tangent of half of this angle.
     * @return the trigonometric tangent of half of this angle.
     */
    public final double tanHalfAngle()
    {
        return Math.tan(0.5 * this.radians);
    }

    /**
     * Obtains the average of three angles. The order of parameters does not matter.
     * @param a - the first angle.
     * @param b - the second angle.
     * @return the average of <code>a1</code>, <code>a2</code> and <code>a3</code>
     * @throws IllegalArgumentException if <code>a</code> or <code>b</code> is null
     */
    public static Angle average(Angle a, Angle b)
    {
        if (a == null || b == null)
        {	
            throw new IllegalArgumentException("Angle Is Null");
        }
        return Angle.fromDegrees(0.5 * (a.degrees + b.degrees));
    }

    /**
     * Compares this {@link Angle} with another. Returns a negative integer if this is the smaller angle, a positive
     * integer if this is the larger, and zero if both angles are equal.
     * @param angle - the angle to compare against.
     * @return -1 if this angle is smaller, 0 if both are equal and +1 if this angle is larger.
     */
    public final int compareTo(@NonNull Angle angle)
    {
        if (this.degrees < angle.degrees)
        {
            return -1;
        }
        if (this.degrees > angle.degrees)
        {
            return 1;
        }
        return 0;
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
        return Angle.fromDegrees(normalizedDegreesLatitude(unnormalizedAngle.degrees));
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
        return Angle.fromDegrees(normalizedDegreesLongitude(unnormalizedAngle.degrees));
    }

    /**
     * Normalize latitude
     * @return Returns normalized latitude
     */
    public Angle normalizedLatitude()
    {
        return normalizedLatitude(this);
    }

    /**
     * Normalize longitude
     * @return Returns normalized longitude
     */
    public Angle normalizedLongitude()
    {
        return normalizedLongitude(this);
    }

    /**
     * Obtains a <code>String</code> representation of this angle.
     * @return the value of this angle in degrees and as a <code>String</code>.
     */
    @Override
    public final String toString()
    {
        return Double.toString(this.degrees) + '\u00B0';
    }

    /**
     * Obtains the amount of memory this {@link Angle} consumes.
     * @return the memory footprint of this angle in bytes.
     */
    public long getSizeInBytes()
    {
        return Double.SIZE / 8;
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
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        Angle angle = (Angle) o;
        // noinspection RedundantIfStatement
        if (angle.degrees != this.degrees)
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
        long temp = degrees != +0.0d ? Double.doubleToLongBits(degrees) : 0L;
        return (int) (temp ^ (temp >>> 32));
    }
}