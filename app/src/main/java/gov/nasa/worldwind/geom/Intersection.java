/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.geom;
/**
 * Instances are immutable
 * @author Tom Gaskins
 * @version $Id$
 */
public final class Intersection
{
    private Vec4 intersectionPoint;
    private boolean isTangent;
    protected Object object;
    /**
     * Constructor
     * @param intersectionPoint - intersection
     * @param isTangent - whether the lines are tangent
     * @param object - another object
     * @throws IllegalArgumentException if intersectionPoint is null
     */
    public Intersection(Vec4 intersectionPoint, boolean isTangent, Object object)
    {
        if (intersectionPoint == null)
        {
            throw new IllegalArgumentException("Intersection Point Is Null");
        }
        this.intersectionPoint = intersectionPoint;
        this.isTangent = isTangent;
        this.object = object;
    }

    /**
     * Returns the object associated with the intersection.
     * @return the object associated with the intersection, or null if no object is associated.
     */
    public Object getObject()
    {
        return object;
    }

    /**
     * Specifies the object to associate with the intersection.
     * @param object the object to associate with the intersection. May be null.
     */
    public void setObject(Object object)
    {
        this.object = object;
    }

    /**
     * Returns the intersection point.
     * @return the intersection point.
     */
    public Vec4 getIntersectionPoint()
    {
        return intersectionPoint;
    }

    /**
     * Compare to another object
     * @param o - other object
     * @return Returns whether this and o are equal
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
        final gov.nasa.worldwind.geom.Intersection that = (gov.nasa.worldwind.geom.Intersection) o;
        if (isTangent != that.isTangent)
        {
            return false;
        }
        // noinspection RedundantIfStatement
        if (!intersectionPoint.equals(that.intersectionPoint))
        {
            return false;
        }
        return true;
    }

    /**
     * Hash
     * @return Returns a hash code
     */
    @Override
    public int hashCode()
    {
        return 29 * intersectionPoint.hashCode() + (isTangent ? 1 : 0);
    }

    /**
     * Convert to string
     * @return Returns string
     */
    @Override
    public String toString()
    {
        return "Intersection Point: " + this.intersectionPoint + (this.isTangent ? " is a tangent." : " not a tangent");
    }
}
