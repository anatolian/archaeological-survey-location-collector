package edu.upenn.sas.archaeologyapp.models;
/**
 * Model for a path element
 * @author Created by colinrob on 6/3/18.
 */
public class PathElement
{
    // The ID for this path
    private final String TEAM_MEMBER, UTM_HEMISPHERE, BEGIN_STATUS, END_STATUS;
    // The beginning latitude of the entry
    private final Double BEGIN_LATITUDE, BEGIN_LONGITUDE, BEGIN_ALTITUDE, BEGIN_AR_RATIO;
    // The ending latitude of the entry
    private final Double END_LATITUDE, END_LONGITUDE, END_ALTITUDE, END_AR_RATIO;
    // The UTM zone of the entry
    private final Integer UTM_ZONE;
    private final Double BEGIN_UTM_NORTHING, BEGIN_UTM_EASTING, END_UTM_NORTHING, END_UTM_EASTING;
    // The start time of the paths run
    private final Long BEGIN_TIME, END_TIME;
    // A boolean for whether or not this item has been uploaded/synced
    private final boolean BEEN_SYNCED;
    /**
     * Constructor
     * @param teamMember - user submitting the path
     * @param beginLatitude - starting latitude
     * @param beginLongitude - starting longitude
     * @param beginAltitude - starting altitude
     * @param endLatitude - ending latitude
     * @param endLongitude - ending longitude
     * @param endAltitude - ending altitude
     * @param UTMHemisphere - hemisphere
     * @param UTMZone - zone
     * @param beginUTMEasting - starting easting
     * @param beginUTMNorthing - starting northing
     * @param endUTMEasting - ending easting
     * @param endUTMNorthing - ending northing
     * @param beginTime - starting time
     * @param endTime - ending time
     * @param beginStatus - starting statud
     * @param endStatus - ending status
     * @param beginARRatio - starting AR ratio
     * @param endARRatio - ending AR ratio
     * @param beenSynced - whether the path is synced
     */
    public PathElement(String teamMember, Double beginLatitude, Double beginLongitude, Double beginAltitude,
                Double endLatitude, Double endLongitude, Double endAltitude, String UTMHemisphere,
                Integer UTMZone, Double beginUTMEasting, Double beginUTMNorthing, Double endUTMEasting,
                Double endUTMNorthing, Long beginTime, Long endTime, String beginStatus, String endStatus,
                Double beginARRatio, Double endARRatio, boolean beenSynced)
    {
        this.TEAM_MEMBER = teamMember;
        this.BEGIN_LATITUDE = beginLatitude;
        this.BEGIN_LONGITUDE = beginLongitude;
        this.BEGIN_ALTITUDE = beginAltitude;
        this.END_LATITUDE = endLatitude;
        this.END_LONGITUDE = endLongitude;
        this.END_ALTITUDE = endAltitude;
        this.UTM_HEMISPHERE = UTMHemisphere;
        this.UTM_ZONE = UTMZone;
        this.BEGIN_UTM_NORTHING = beginUTMNorthing;
        this.BEGIN_UTM_EASTING = beginUTMEasting;
        this.END_UTM_NORTHING = endUTMNorthing;
        this.END_UTM_EASTING = endUTMEasting;
        this.BEGIN_TIME = beginTime;
        this.END_TIME = endTime;
        this.BEGIN_STATUS = beginStatus;
        this.END_STATUS = endStatus;
        this.BEGIN_AR_RATIO = beginARRatio;
        this.END_AR_RATIO = endARRatio;
        this.BEEN_SYNCED = beenSynced;
    }

    /**
     * Getters for all the variables
     * @return Returns team member
     */
    public String getTeamMember()
    {
        return TEAM_MEMBER;
    }

    /**
     * Get starting latitude
     * @return Returns starting latitude
     */
    public Double getBeginLatitude()
    {
        return BEGIN_LATITUDE;
    }

    /**
     * Get starting longitude
     * @return Returns starting longitude
     */
    public Double getBeginLongitude()
    {
        return BEGIN_LONGITUDE;
    }

    /**
     * Get starting altitude
     * @return Returns starting altitude
     */
    public Double getBeginAltitude()
    {
        return BEGIN_ALTITUDE;
    }

    /**
     * Get ending latitude
     * @return Returns ending latitude
     */
    public Double getEndLatitude()
    {
        return END_LATITUDE;
    }

    /**
     * Get ending longitude
     * @return Returns ending longitude
     */
    public Double getEndLongitude()
    {
        return END_LONGITUDE;
    }

    /**
     * Get ending altitude
     * @return Returns ending altitude
     */
    public Double getEndAltitude()
    {
        return END_ALTITUDE;
    }

    /**
     * Get hemisphere
     * @return - Returns hemisphere
     */
    public String getHemisphere()
    {
        return UTM_HEMISPHERE;
    }

    /**
     * Get zone
     * @return Returns zone
     */
    public Integer getZone()
    {
        return UTM_ZONE;
    }

    /**
     * Get starting easting
     * @return Returns starting easting
     */
    public Double getBeginEasting()
    {
        return BEGIN_UTM_EASTING;
    }

    /**
     * Get starting northing
     * @return Returns starting northing
     */
    public Double getBeginNorthing()
    {
        return BEGIN_UTM_NORTHING;
    }

    /**
     * Get ending easting
     * @return Returns ending easting
     */
    public Double getEndEasting()
    {
        return END_UTM_EASTING;
    }

    /**
     * Get ending northing
     * @return Returns ending northing
     */
    public Double getEndNorthing()
    {
        return END_UTM_NORTHING;
    }

    /**
     * Get starting time
     * @return Returns starting time
     */
    public Long getBeginTime()
    {
        return BEGIN_TIME;
    }

    /**
     * Get ending time
     * @return Returns ending time
     */
    public Long getEndTime()
    {
        return END_TIME;
    }

    /**
     * Get starting status
     * @return Returns starting status
     */
    public String getBeginStatus()
    {
        return BEGIN_STATUS;
    }

    /**
     * Get ending status
     * @return Returns ending status
     */
    public String getEndStatus()
    {
        return END_STATUS;
    }

    /**
     * Get starting AR ratio
     * @return Returns starting AR ratio
     */
    public Double getBeginARRatio()
    {
        return BEGIN_AR_RATIO;
    }

    /**
     * Get ending AR ratio
     * @return Returns ending AR ratio
     */
    public Double getEndARRatio()
    {
        return END_AR_RATIO;
    }

    /**
     * Get whether the find is synced
     * @return Returns whether the find is synced
     */
    public boolean getBeenSynced()
    {
        return BEEN_SYNCED;
    }
}