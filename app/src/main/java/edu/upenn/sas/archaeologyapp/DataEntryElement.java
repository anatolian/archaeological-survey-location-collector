package edu.upenn.sas.archaeologyapp;

import java.util.ArrayList;

/**
 * Model for a data entry element
 * Created by eanvith on 16/01/17.
 */

public class DataEntryElement {

    /**
     * The ID for this entry
     */
    private final String ID;

    /**
     * The latitude of the entry
     */
    private final double latitude;

    /**
     * The longitude of the entry
     */
    private final double longitude;

    /**
     * The altitude of the entry
     */
    private final double altitude;

    /**
     * The status of the position fetch
     */
    private final String status;

    /**
     * The status of the position fetch
     */
    private final Double AR_ratio;

    /**
     * The paths of the images linked to the entry
     */
    private final ArrayList<String> imagePaths;

    /**
     * The material of the entry
     */
    private final String material;

    /**
     * The comments for the entry
     */
    private final String comments;

    /**
     * Timestamp when the entry was first created
     */
    private final long createdTimestamp;

    /**
     * Timestamp when the entry was last edited
     */
    private final long updateTimestamp;

    /**
     * A boolean for whether or not this item has been uploaded/synced
     */
    private final boolean beenSynced;

    private final Integer northing;
    private final Integer easting;
    private final Integer zone;
    private final String hemisphere;
    private final Integer sample;

    DataEntryElement(String ID, double latitude, double longitude, double altitude, String status, Double AR_ratio, ArrayList<String> imagePaths, String material, String comments, long createdTimestamp, long updateTimestamp, Integer zone, String hemisphere, Integer northing, Integer easting, Integer sample, boolean beenSynced) {

        this.ID = ID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.status = status;
        this.AR_ratio = AR_ratio;
        this.imagePaths = imagePaths;
        this.material = material;
        this.comments = comments;
        this.createdTimestamp = createdTimestamp;
        this.updateTimestamp = updateTimestamp;
        this.zone = zone;
        this.hemisphere = hemisphere;
        this.northing = northing;
        this.easting = easting;
        this.sample = sample;
        this.beenSynced = beenSynced;

    }

    /**
     * Getters for all the variables
     */
    public String getID() {

        return ID;

    }

    public double getLatitude() {

        return latitude;

    }

    public double getLongitude() {

        return longitude;

    }

    public double getAltitude() {

        return altitude;

    }

    public String getStatus() {

        return status;

    }

    public Double getARRatio() {

        return AR_ratio;

    }

    public ArrayList<String> getImagePaths() {

        return imagePaths;

    }

    public String getMaterial() {

        return material;

    }

    public String getComments() {

        return comments;

    }

    public long getCreatedTimestamp() {

        return createdTimestamp;

    }

    public long getUpdateTimestamp() {

        return updateTimestamp;

    }

    public Integer getZone() {

        return zone;

    }

    public String getHemisphere() {

        return hemisphere;

    }

    public Integer getNorthing() {

        return northing;

    }

    public Integer getEasting() {

        return easting;

    }

    public Integer getSample() {

        return sample;

    }

    public boolean getBeenSynced() {

        return beenSynced;

    }

}