package edu.upenn.sas.archaeologyapp;

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
     * The path of the image linked to the entry
     */
    private final String imagePath;

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

    DataEntryElement(String ID, double latitude, double longitude, double altitude, String imagePath, String material, String comments, long createdTimestamp, long updateTimestamp) {

        this.ID = ID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.imagePath = imagePath;
        this.material = material;
        this.comments = comments;
        this.createdTimestamp = createdTimestamp;
        this.updateTimestamp = updateTimestamp;

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

    public String getImagePath() {

        return imagePath;

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

}