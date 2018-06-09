package edu.upenn.sas.archaeologyapp.models;
import java.util.ArrayList;
/**
 * Model for a data entry element
 * @author Created by eanvith on 16/01/17.
 */
public class DataEntryElement
{
    // The ID for this entry
    private final String ID, status, material, comments, hemisphere;
    private final double latitude, longitude, altitude;
    // The status of the position fetch
    private final Double AR_ratio;
    // The paths of the images linked to the entry
    private final ArrayList<String> imagePaths;
    // Timestamp when the entry was first created
    private final long createdTimestamp, updateTimestamp;
    // A boolean for whether or not this item has been uploaded/synced
    private final boolean beenSynced;
    private final Integer northing, easting, zone, sample;
    /**
     * Constructor
     * @param ID - item id
     * @param latitude - item latitude
     * @param longitude - item longitude
     * @param altitude - item altitude
     * @param status - recorded status
     * @param AR_ratio - AR ratio
     * @param imagePaths - image URIs
     * @param material - item material
     * @param comments - recorded comments
     * @param createdTimestamp - timestamp when created
     * @param updateTimestamp - timestamp when updated
     * @param zone - item zone
     * @param hemisphere - item hemisphere
     * @param northing - item northing
     * @param easting - item easting
     * @param sample - find number
     * @param beenSynced - whether the find is synced
     */
    public DataEntryElement(String ID, double latitude, double longitude, double altitude, String status,
                     Double AR_ratio, ArrayList<String> imagePaths, String material, String comments,
                     long createdTimestamp, long updateTimestamp, Integer zone, String hemisphere,
                     Integer northing, Integer easting, Integer sample, boolean beenSynced)
    {
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
        return latitude;
    }

    /**
     * Get longitude
     * @return Returns longitude
     */
    public double getLongitude()
    {
        return longitude;
    }

    /**
     * Get altitude
     * @return Returns altitude
     */
    public double getAltitude()
    {
        return altitude;
    }

    /**
     * Get status
     * @return Returns status
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * Get AR ratio
     * @return Returns AR ratio
     */
    public Double getARRatio()
    {
        return AR_ratio;
    }

    /**
     * Get image URIs
     * @return Returns image URIs
     */
    public ArrayList<String> getImagePaths()
    {
        return imagePaths;
    }

    /**
     * Get material
     * @return Returns material
     */
    public String getMaterial()
    {
        return material;
    }

    /**
     * Get comments
     * @return Returns comments
     */
    public String getComments()
    {
        return comments;
    }

    /**
     * Get created time stamp
     * @return Returns created time stamp
     */
    public long getCreatedTimestamp()
    {
        return createdTimestamp;
    }

    /**
     * Get updated time stamp
     * @return Returns updated time stamp
     */
    public long getUpdateTimestamp()
    {
        return updateTimestamp;
    }

    /**
     * Get zone
     * @return Returns zone
     */
    public Integer getZone()
    {
        return zone;
    }

    /**
     * Get hemisphere
     * @return Returns hemisphere
     */
    public String getHemisphere()
    {
        return hemisphere;
    }

    /**
     * Get northing
     * @return Returns northing
     */
    public Integer getNorthing()
    {
        return northing;
    }

    /**
     * Get easting
     * @return Returns easting
     */
    public Integer getEasting()
    {
        return easting;
    }

    /**
     * Get find number
     * @return Returns find number
     */
    public Integer getSample()
    {
        return sample;
    }

    /**
     * Returns whether the find was synced
     * @return Returns whether the find was synced
     */
    public boolean getBeenSynced()
    {
        return beenSynced;
    }
}