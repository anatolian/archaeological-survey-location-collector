package edu.upenn.sas.archaeologyapp;

/**
 * Model for a data entry element
 * Created by eanvith on 16/01/17.
 */

public class DataEntryElement {

    /**
     * The latitude of the entry
     */
    private final double latitude;

    /**
     * The longitude of the entry
     */
    private final double longitude;

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

    DataEntryElement(double latitude, double longitude, String imagePath, String material, String comments) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.imagePath = imagePath;
        this.material = material;
        this.comments = comments;

    }

    /**
     * Getters for all the variables
     */
    public double getLatitude() {

        return latitude;

    }

    public double getLongitude() {

        return longitude;

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

}
