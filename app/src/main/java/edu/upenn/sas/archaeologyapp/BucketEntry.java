package edu.upenn.sas.archaeologyapp;

/**
 * This class contains all the attributes of a bucket list entry
 * Created by eanvith on 30/12/16.
 */

public class BucketEntry {

    /**
     * The title of the bucket entry
     */
    private final String title;

    /**
     * Constructor
     * @param _title The title of the bucket entry
     */
    public BucketEntry(String _title) {

        this.title = _title;

    }

    /**
     * Getter for bucket entry title
     * @return The title of the bucket entry
     */
    public String getTitle() {

        return title;

    }

}
