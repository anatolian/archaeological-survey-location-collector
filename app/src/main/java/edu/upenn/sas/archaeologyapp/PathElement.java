package edu.upenn.sas.archaeologyapp;

/**
 * Model for a path element
 * Created by colinrob on 6/3/18.
 */

public class PathElement {

    /**
     * The ID for this path
     */
    private final String team_member;

    /**
     * The beginning latitude of the entry
     */
    private final Double begin_latitude;

    /**
     * The beginning longitude of the entry
     */
    private final Double begin_longitude;

    /**
     * The beginning altitude of the entry
     */
    private final Double begin_altitude;

    /**
     * The ending latitude of the entry
     */
    private final Double end_latitude;

    /**
     * The ending longitude of the entry
     */
    private final Double end_longitude;

    /**
     * The ending altitude of the entry
     */
    private final Double end_altitude;

    /**
     * The UTM hemisphere of the entry
     */
    private final String UTM_hemisphere;

    /**
     * The UTM zone of the entry
     */
    private final Integer UTM_zone;

    /**
     * The beginning UTM northing of the entry
     */
    private final Integer begin_UTM_northing;

    /**
     * The beginning UTM easting of the entry
     */
    private final Integer begin_UTM_easting;

    /**
     * The ending UTM northing of the entry
     */
    private final Integer end_UTM_northing;

    /**
     * The ending UTM easting of the entry
     */
    private final Integer end_UTM_easting;

    /**
     * The start time of the paths run
     */
    private final Long begin_time;

    /**
     * The stop time of the paths run
     */
    private final Long end_time;

    /**
     * The status of the start point
     */
    private final String begin_status;

    /**
     * The status of the end point
     */
    private final String end_status;

    /**
     * The AR ratio of the start point
     */
    private final Double begin_AR_ratio;

    /**
     * The AR ratio of the end point
     */
    private final Double end_AR_ratio;

    /**
     * A boolean for whether or not this item has been uploaded/synced
     */
    private final boolean beenSynced;

    PathElement(String team_member, Double begin_latitude, Double begin_longitude, Double begin_altitude,  Double end_latitude, Double end_longitude, Double end_altitude, String UTM_hemisphere, Integer UTM_zone, Integer begin_UTM_easting, Integer begin_UTM_northing, Integer end_UTM_easting, Integer end_UTM_northing, Long begin_time, Long end_time, String begin_status, String end_status, Double begin_AR_ratio, Double end_AR_ratio, boolean beenSynced) {

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
     */
    public String getTeamMember() {

        return team_member;

    }

    public Double getBeginLatitude() {

        return begin_latitude;

    }

    public Double getBeginLongitude() {

        return begin_longitude;

    }

    public Double getBeginAltitude() {

        return begin_altitude;

    }

    public Double getEndLatitude() {

        return end_latitude;

    }

    public Double getEndLongitude() {

        return end_longitude;

    }

    public Double getEndAltitude() {

        return end_altitude;

    }

    public String getHemisphere() {

        return UTM_hemisphere;

    }

    public Integer getZone() {

        return UTM_zone;

    }

    public Integer getBeginEasting() {

        return begin_UTM_easting;

    }

    public Integer getBeginNorthing() {

        return begin_UTM_northing;

    }

    public Integer getEndEasting() {

        return end_UTM_easting;

    }

    public Integer getEndNorthing() {

        return end_UTM_northing;

    }

    public Long getBeginTime() {

        return begin_time;

    }

    public Long getEndTime() {

        return end_time;

    }

    public String getBeginStatus() {

        return begin_status;

    }

    public String getEndStatus() {

        return end_status;

    }

    public Double getBeginARRatio() {

        return begin_AR_ratio;

    }

    public Double getEndARRatio() {

        return end_AR_ratio;

    }

    public boolean getBeenSynced() {

        return beenSynced;

    }

}