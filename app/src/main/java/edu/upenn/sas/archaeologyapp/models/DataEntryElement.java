package edu.upenn.sas.archaeologyapp.models;
import java.util.ArrayList;
/**
 * Model for a data entry element
 * @author Created by eanvith on 16/01/17.
 */
public class DataEntryElement
{
    // The ID for this entry
    private final String ID, STATUS, MATERIAL, COMMENTS, HEMISPHERE;
    private final double LATITUDE, LONGITUDE, ALTITUDE, PRECISE_NORTHING, PRECISE_EASTING;
    // The status of the position fetch
    private final Double AR_RATIO;
    // The paths of the images linked to the entry
    private final ArrayList<String> IMAGE_PATHS;
    // Timestamp when the entry was first created
    private final long CREATED_TIME_STAMP, UPDATE_TIME_STAMP;
    // A boolean for whether or not this item has been uploaded/synced
    private final boolean BEEN_SYNCED;
    private final Integer NORTHING, EASTING, ZONE, SAMPLE;
    /**
     * Constructor
     * @param ID - item id
     * @param latitude - item latitude
     * @param longitude - item longitude
     * @param altitude - item altitude
     * @param status - recorded status
     * @param ARRatio - AR ratio
     * @param imagePaths - image URIs
     * @param material - item material
     * @param comments - recorded comments
     * @param createdTimestamp - timestamp when created
     * @param updateTimestamp - timestamp when updated
     * @param zone - item zone
     * @param hemisphere - item hemisphere
     * @param northing - item northing
     * @param preciseNorthing - precise northing
     * @param easting - item easting
     * @param preciseEasting - precise easting
     * @param sample - find number
     * @param beenSynced - whether the find is synced
     */
    public DataEntryElement(String ID, double latitude, double longitude, double altitude, String status,
                            Double ARRatio, ArrayList<String> imagePaths, String material, String comments,
                            long createdTimestamp, long updateTimestamp, Integer zone, String hemisphere,
                            Integer northing, Double preciseNorthing, Integer easting, Double preciseEasting,
                            Integer sample, boolean beenSynced)
    {
        this.ID = ID;
        this.LATITUDE = latitude;
        this.LONGITUDE = longitude;
        this.ALTITUDE = altitude;
        this.STATUS = status;
        this.AR_RATIO = ARRatio;
        this.IMAGE_PATHS = imagePaths;
        this.MATERIAL = material;
        this.COMMENTS = comments;
        this.CREATED_TIME_STAMP = createdTimestamp;
        this.UPDATE_TIME_STAMP = updateTimestamp;
        this.ZONE = zone;
        this.HEMISPHERE = hemisphere;
        this.NORTHING = northing;
        this.EASTING = easting;
        this.SAMPLE = sample;
        this.BEEN_SYNCED = beenSynced;
        this.PRECISE_EASTING = preciseEasting;
        this.PRECISE_NORTHING = preciseNorthing;
    }

    /**
     * Getters for all the variables
     * @return Returns the id
     */
    public String getID()
    {
        return ID;
    }

    /**
     * Get latitude
     * @return Returns latitude
     */
    public double getLatitude()
    {
        return LATITUDE;
    }

    /**
     * Get longitude
     * @return Returns longitude
     */
    public double getLongitude()
    {
        return LONGITUDE;
    }

    /**
     * Get altitude
     * @return Returns altitude
     */
    public double getAltitude()
    {
        return ALTITUDE;
    }

    /**
     * Get status
     * @return Returns status
     */
    public String getStatus()
    {
        return STATUS;
    }

    /**
     * Get AR ratio
     * @return Returns AR ratio
     */
    public Double getARRatio()
    {
        return AR_RATIO;
    }

    /**
     * Get image URIs
     * @return Returns image URIs
     */
    public ArrayList<String> getImagePaths()
    {
        return IMAGE_PATHS;
    }

    /**
     * Get material
     * @return Returns material
     */
    public String getMaterial()
    {
        return MATERIAL;
    }

    /**
     * Get comments
     * @return Returns comments
     */
    public String getComments()
    {
        return COMMENTS;
    }

    /**
     * Get created time stamp
     * @return Returns created time stamp
     */
    public long getCreatedTimestamp()
    {
        return CREATED_TIME_STAMP;
    }

    /**
     * Get updated time stamp
     * @return Returns updated time stamp
     */
    public long getUpdateTimestamp()
    {
        return UPDATE_TIME_STAMP;
    }

    /**
     * Get zone
     * @return Returns zone
     */
    public Integer getZone()
    {
        return ZONE;
    }

    /**
     * Get hemisphere
     * @return Returns hemisphere
     */
    public String getHemisphere()
    {
        return HEMISPHERE;
    }

    /**
     * Get northing
     * @return Returns northing
     */
    public Integer getNorthing()
    {
        return NORTHING;
    }

    /**
     * Get precise northing
     * @return Returns northing
     */
    public Double getPreciseNorthing()
    {
        return PRECISE_NORTHING;
    }

    /**
     * Get easting
     * @return Returns easting
     */
    public Integer getEasting()
    {
        return EASTING;
    }

    /**
     * Get precise easting
     * @return Returns easting
     */
    public Double getPreciseEasting()
    {
        return PRECISE_EASTING;
    }

    /**
     * Get find number
     * @return Returns find number
     */
    public Integer getSample()
    {
        return SAMPLE;
    }

    /**
     * Returns whether the find was synced
     * @return Returns whether the find was synced
     */
    public boolean getBeenSynced()
    {
        return BEEN_SYNCED;
    }
}