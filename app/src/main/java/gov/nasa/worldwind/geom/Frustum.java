/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.geom;
/**
 * Represents a view frustum composed of six planes: left, right, bottom, top, near far.
 * Frustum instances are immutable.
 * @author Tom Gaskins
 * @version $Id$
 */
public class Frustum
{
    private final Plane LEFT, RIGHT, BOTTOM, TOP, NEAR, FAR;
    // Holds all six frustum planes in an array in the order left, right, bottom, top, near, far.
    private final Plane[] ALL_PLANES;
    /**
     * Create a frustum from six {@link gov.nasa.worldwind.geom.Plane}s defining the frustum boundaries.
     * None of the arguments may be null.
     * @param near - the near plane
     * @param far - the far plane
     * @param left - the left plane
     * @param right - the right plane
     * @param top - the top plane
     * @param bottom - the bottom plane
     * @throws IllegalArgumentException if any argument is null.
     */
    private Frustum(Plane left, Plane right, Plane bottom, Plane top, Plane near, Plane far)
    {
        if (left == null || right == null || bottom == null || top == null || near == null || far == null)
        {
            throw new IllegalArgumentException("Plane Is Null");
        }
        this.LEFT = left;
        this.RIGHT = right;
        this.BOTTOM = bottom;
        this.TOP = top;
        this.NEAR = near;
        this.FAR = far;
        this.ALL_PLANES = new Plane[] {this.LEFT, this.RIGHT, this.BOTTOM, this.TOP, this.NEAR, this.FAR};
    }

    /**
     * Returns the left plane.
     * @return the left plane.
     */
    public final Plane getLeft()
    {
        return this.LEFT;
    }

    /**
     * Returns the right plane.
     * @return the right plane.
     */
    public final Plane getRight()
    {
        return this.RIGHT;
    }

    /**
     * Returns the bottom plane.
     * @return the bottom plane.
     */
    public final Plane getBottom()
    {
        return this.BOTTOM;
    }

    /**
     * Returns the top plane.
     * @return the top plane.
     */
    public final Plane getTop()
    {
        return this.TOP;
    }

    /**
     * Returns all the planes.
     * @return an array of the frustum planes, in the order left, right, bottom, top, near, far.
     */
    public Plane[] getAllPlanes()
    {
        return this.ALL_PLANES;
    }

    /**
     * Comapre to another object
     * @param obj - other object
     * @return Returns true if the objects are equal
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        Frustum that = (Frustum) obj;
        return this.LEFT.equals(that.LEFT) && this.RIGHT.equals(that.RIGHT) && this.BOTTOM.equals(that.BOTTOM)
                && this.TOP.equals(that.TOP) && this.NEAR.equals(that.NEAR) && this.FAR.equals(that.FAR);
    }

    /**
     * Hash
     * @return Returns hash code
     */
    public int hashCode()
    {
        int result = this.LEFT.hashCode();
        result = 31 * result + this.RIGHT.hashCode();
        result = 19 * result + this.BOTTOM.hashCode();
        result = 23 * result + this.TOP.hashCode();
        result = 17 * result + this.NEAR.hashCode();
        return 19 * result + this.FAR.hashCode();
    }

    /**
     * Convert to string
     * @return Returns string form of frustrum
     */
    public String toString()
    {
        return "(left=" + this.LEFT + ", right=" + this.RIGHT + ", bottom=" + this.BOTTOM + ", top="
                + this.TOP + ", near=" + this.NEAR + ", far=" + this.FAR + ")";
    }

    /**
     * Indicates whether a specified point is within this frustum.
     * @param point - the point to test.
     * @return true if the point is within the frustum, otherwise false.
     * @throws IllegalArgumentException if the point is null.
     */
    public final boolean contains(Vec4 point)
    {
        if (point == null)
        {
            throw new IllegalArgumentException("Point Is Null");
        }
        // See if the point is entirely within the frustum. The dot product of the point with each plane's vector
        // provides a distance to each plane. If this distance is less than 0, the point is clipped by that plane and
        // neither intersects nor is contained by the space enclosed by this Frustum.
        if (this.FAR.dot(point) <= 0)
        {
            return false;
        }
        if (this.LEFT.dot(point) <= 0)
        {
            return false;
        }
        if (this.RIGHT.dot(point) <= 0)
        {
            return false;
        }
        if (this.TOP.dot(point) <= 0)
        {
            return false;
        }
        if (this.BOTTOM.dot(point) <= 0)
        {
            return false;
        }
        // noinspection RedundantIfStatement
        if (this.NEAR.dot(point) <= 0)
        {
            return false;
        }
        return true;
    }
}