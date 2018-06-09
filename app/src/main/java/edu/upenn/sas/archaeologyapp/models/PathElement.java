package edu.upenn.sas.archaeologyapp.models;
/**
 * Model for a path element
 * @author Created by colinrob on 6/3/18.
 */
public class PathElement
{
    // The ID for this path
    private final String team_member, UTM_hemisphere, begin_status, end_status;
    // The beginning latitude of the entry
    private final Double begin_latitude, begin_longitude, begin_altitude;
    // The ending latitude of the entry
    private final Double end_latitude, end_longitude, end_altitude, begin_AR_ratio, end_AR_ratio;
    // The UTM zone of the entry
    private final Integer UTM_zone, begin_UTM_northing, begin_UTM_easting, end_UTM_northing, end_UTM_easting;
    // The start time of the paths run
    private final Long begin_time, end_time;
    // A boolean for whether or not this item has been uploaded/synced
    private final boolean beenSynced;
    /**
     * Constructor
     * @param team_member - user submitting the path
     * @param begin_latitude - starting latitude
     * @param begin_longitude - starting longitude
     * @param begin_altitude - starting altitude
     * @param end_latitude - ending latitude
     * @param end_longitude - ending longitude
     * @param end_altitude - ending altitude
     * @param UTM_hemisphere - hemisphere
     * @param UTM_zone - zone
     * @param begin_UTM_easting - starting easting
     * @param begin_UTM_northing - starting northing
     * @param end_UTM_easting - ending easting
     * @param end_UTM_northing - ending northing
     * @param begin_time - starting time
     * @param end_time - ending time
     * @param begin_status - starting statud
     * @param end_status - ending status
     * @param begin_AR_ratio - starting AR ratio
     * @param end_AR_ratio - ending AR ratio
     * @param beenSynced - whether the path is synced
     */
    public PathElement(String team_member, Double begin_latitude, Double begin_longitude, Double begin_altitude,
                Double end_latitude, Double end_longitude, Double end_altitude, String UTM_hemisphere,
                Integer UTM_zone, Integer begin_UTM_easting, Integer begin_UTM_northing, Integer end_UTM_easting,
                Integer end_UTM_northing, Long begin_time, Long end_time, String begin_status, String end_status,
                Double begin_AR_ratio, Double end_AR_ratio, boolean beenSynced)
    {
        this.team_member = team_member;
        this.begin_latitude = begin_latitude;
        this.begin_longitude = begin_longitude;
        this.begin_altitude = begin_altitude;
        this.end_latitude = end_latitude;
        this.end_longitude = end_longitude;
        this.end_altitude = end_altitude;
        this.UTM_hemisphere = UTM_hemisphere;
        this.UTM_zone = UTM_zone;
        this.begin_UTM_northing = begin_UTM_northing;
        this.begin_UTM_easting = begin_UTM_easting;
        this.end_UTM_northing = end_UTM_northing;
        this.end_UTM_easting = end_UTM_easting;
        this.begin_time = begin_time;
        this.end_time = end_time;
        this.begin_status = begin_status;
        this.end_status = end_status;
        this.begin_AR_ratio = begin_AR_ratio;
        this.end_AR_ratio = end_AR_ratio;
        this.beenSynced = beenSynced;
    }

    /**
     * Getters for all the variables
     * @return Returns team member
     */
    public String getTeamMember()
    {
        return team_member;
    }

    /**
     * Get starting latitude
     * @return Returns starting latitude
     */
    public Double getBeginLatitude()
    {
        return begin_latitude;
    }

    /**
     * Get starting longitude
     * @return Returns starting longitude
     */
    public Double getBeginLongitude()
    {
        return begin_longitude;
    }

    /**
     * Get starting altitude
     * @return Returns starting altitude
     */
    public Double getBeginAltitude()
    {
        return begin_altitude;
    }

    /**
     * Get ending latitude
     * @return Returns ending latitude
     */
    public Double getEndLatitude()
    {
        return end_latitude;
    }

    /**
     * Get ending longitude
     * @return Returns ending longitude
     */
    public Double getEndLongitude()
    {
        return end_longitude;
    }

    /**
     * Get ending altitude
     * @return Returns ending altitude
     */
    public Double getEndAltitude()
    {
        return end_altitude;
    }

    /**
     * Get hemisphere
     * @return - Returns hemisphere
     */
    public String getHemisphere()
    {
        return UTM_hemisphere;
    }

    /**
     * Get zone
     * @return Returns zone
     */
    public Integer getZone()
    {
        return UTM_zone;
    }

    /**
     * Get starting easting
     * @return Returns starting easting
     */
    public Integer getBeginEasting()
    {
        return begin_UTM_easting;
    }

    /**
     * Get starting northing
     * @return Returns starting northing
     */
    public Integer getBeginNorthing()
    {
        return begin_UTM_northing;
    }

    /**
     * Get ending easting
     * @return Returns ending easting
     */
    public Integer getEndEasting()
    {
        return end_UTM_easting;
    }

    /**
     * Get ending northing
     * @return Returns ending northing
     */
    public Integer getEndNorthing()
    {
        return end_UTM_northing;
    }

    /**
     * Get starting time
     * @return Returns starting time
     */
    public Long getBeginTime()
    {
        return begin_time;
    }

    /**
     * Get ending time
     * @return Returns ending time
     */
    public Long getEndTime()
    {
        return end_time;
    }

    /**
     * Get starting status
     * @return Returns starting status
     */
    public String getBeginStatus()
    {
        return begin_status;
    }

    /**
     * Get ending status
     * @return Returns ending status
     */
    public String getEndStatus()
    {
        return end_status;
    }

    /**
     * Get starting AR ratio
     * @return Returns starting AR ratio
     */
    public Double getBeginARRatio()
    {
        return begin_AR_ratio;
    }

    /**
     * Get ending AR ratio
     * @return Returns ending AR ratio
     */
    public Double getEndARRatio()
    {
        return end_AR_ratio;
    }

    /**
     * Get whether the find is synced
     * @return Returns whether the find is synced
     */
    public boolean getBeenSynced()
    {
        return beenSynced;
    }
}