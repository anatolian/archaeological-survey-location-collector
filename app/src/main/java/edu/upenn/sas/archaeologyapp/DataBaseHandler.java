package edu.upenn.sas.archaeologyapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.R.attr.id;

/**
 * Data base helper class to create, read and write data
 * Created by eanvith on 16/01/17.
 */

public class DataBaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 7;

    private static final String DATABASE_NAME = "BUCKETDB";

    // Table names
    private static final String TABLE_NAME = "bucket";
    private static final String IMAGE_TABLE_NAME = "images";

    // Table Columns names
    private static final String KEY_ID = "bucket_id";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_ALTITUDE = "altitude";
    private static final String KEY_STATUS = "status";
    private static final String KEY_MATERIAL = "material";
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_CREATED_TIMESTAMP = "created_timestamp";
    private static final String KEY_UPDATED_TIMESTAMP = "updated_timestamp";
    private static final String KEY_ZONE = "zone";
    private static final String KEY_HEMISPHERE = "hemisphere";
    private static final String KEY_NORTHING = "northing";
    private static final String KEY_EASTING = "easting";
    private static final String KEY_SAMPLE = "sample";
    private static final String KEY_BEEN_SYNCED = "been_synced";

    private static final String KEY_IMAGE_ID = "image_name";
    private static final String KEY_IMAGE_BUCKET = "image_bucket";

    /**
     * Constructor
     * @param context The current app context
     */
    public DataBaseHandler(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_BUCKET_TABLE = "CREATE TABLE " + TABLE_NAME +
                "(" + KEY_ID + " TEXT PRIMARY KEY,"
                + KEY_LATITUDE + " FLOAT,"
                + KEY_LONGITUDE + " FLOAT,"
                + KEY_ALTITUDE + " FLOAT,"
                + KEY_STATUS + " TEXT,"
                + KEY_MATERIAL + " TEXT,"
                + KEY_COMMENT + " TEXT,"
                + KEY_UPDATED_TIMESTAMP + " INTEGER,"
                + KEY_CREATED_TIMESTAMP + " INTEGER,"
                + KEY_ZONE + " INTEGER,"
                + KEY_HEMISPHERE + " TEXT,"
                + KEY_NORTHING + " INTEGER,"
                + KEY_EASTING + " INTEGER,"
                + KEY_SAMPLE + " INTEGER,"
                + KEY_BEEN_SYNCED + " INTEGER)"
                ;

        String CREATE_IMAGE_TABLE = "CREATE TABLE " + IMAGE_TABLE_NAME +
                "(" + KEY_IMAGE_ID + " TEXT PRIMARY KEY,"
                + KEY_IMAGE_BUCKET + " TEXT)"
                ;

        db.execSQL(CREATE_BUCKET_TABLE);
        db.execSQL(CREATE_IMAGE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + IMAGE_TABLE_NAME);

        // Create table again
        onCreate(db);

    }

    /**
     * Helper function to drop the previous table and create a new empty table
     */
    public void newTable() {

        SQLiteDatabase db = this.getWritableDatabase();

        try{

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + IMAGE_TABLE_NAME);
            onCreate(db);

        }
        catch(Exception e){

            e.printStackTrace();

        }
        finally{
            db.close();
        }

    }

    /**
     * Helper function to remove
     * @param id The id of the element to be removed from the table
     */
    public void removeRow(String id){

        SQLiteDatabase db = this.getWritableDatabase();

        String deleteEntriesQuery = "DELETE FROM " + TABLE_NAME + " WHERE " + KEY_ID + " ='" + id + "'" ;
        String deleteImagesQuery = "DELETE FROM " + IMAGE_TABLE_NAME + " WHERE " + KEY_IMAGE_BUCKET + " ='" + id + "'" ;

        Cursor cursor = null;

        try{

            cursor = db.rawQuery(deleteEntriesQuery, null);
            cursor.getCount();

            cursor = db.rawQuery(deleteImagesQuery, null);
            cursor.getCount();

        }
        catch(Exception e){

            e.printStackTrace();

        }
        finally{

            if(cursor!=null){

                cursor.close();

            }

        }

    }

    /**
     * Helper function to add elements to the table
     * @param entry
     */
    public void addRows(DataEntryElement[] entry) {

        SQLiteDatabase db = this.getWritableDatabase();

        try{

            db.beginTransaction();

            for(int i=0;i<entry.length;i++)
            {

                // The values to be written in a row
                ContentValues values = new ContentValues();

                values.put(KEY_LATITUDE, entry[i].getLatitude());
                values.put(KEY_LONGITUDE, entry[i].getLongitude());
                values.put(KEY_ALTITUDE, entry[i].getAltitude());
                values.put(KEY_STATUS, entry[i].getStatus());
                values.put(KEY_MATERIAL, entry[i].getMaterial());
                values.put(KEY_COMMENT, entry[i].getComments());
                values.put(KEY_UPDATED_TIMESTAMP, entry[i].getUpdateTimestamp());
                values.put(KEY_ZONE, entry[i].getZone());
                values.put(KEY_HEMISPHERE, entry[i].getHemisphere());
                values.put(KEY_NORTHING, entry[i].getNorthing());
                values.put(KEY_EASTING, entry[i].getEasting());
                values.put(KEY_SAMPLE, entry[i].getSample());
                values.put(KEY_BEEN_SYNCED, entry[i].getBeenSynced() ? 1 : 0);


                // Try to make an update call
                int rowsAffected = db.update(TABLE_NAME, values, KEY_ID + " ='" + entry[i].getID()+"'", null);

                // If update call fails, rowsAffected will be 0. If not, it means the row was updated
                if(rowsAffected == 0){

                    // If update fails, it means the ID does not exist in table. Create a new row with the ID.
                    values.put(KEY_ID, entry[i].getID());

                    // Also add timestamp for creation of entry
                    values.put(KEY_CREATED_TIMESTAMP, entry[i].getCreatedTimestamp());

                    db.insert(TABLE_NAME, null, values);

                    // Add associated images to the image table
                    addImages(entry[i].getID(), entry[i].getImagePaths());

                } else {
                    // Update successful, delete and re-add images
                    deleteImages(entry[i].getID());
                    addImages(entry[i].getID(), entry[i].getImagePaths());
                }

            }

            db.setTransactionSuccessful();
            db.endTransaction();

        }
        catch(Exception e){

            e.printStackTrace();

        }
        finally{

            db.close();

        }
    }

    /**
     * Helper function to get all rows from the table
     * @return An array list of all rows fetched
     */
    public ArrayList<DataEntryElement> getRows() {

        ArrayList<DataEntryElement> dataEntryElements = new ArrayList<DataEntryElement>();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + KEY_CREATED_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        try{

            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {

                do {
                    DataEntryElement entry = new DataEntryElement(cursor.getString(cursor.getColumnIndex(KEY_ID)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_ALTITUDE)),
                            cursor.getString(cursor.getColumnIndex(KEY_STATUS)),
                            getImages(cursor.getString(cursor.getColumnIndex(KEY_ID))),
                            cursor.getString(cursor.getColumnIndex(KEY_MATERIAL)),
                            cursor.getString(cursor.getColumnIndex(KEY_COMMENT)),
                            cursor.getLong(cursor.getColumnIndex(KEY_CREATED_TIMESTAMP)),
                            cursor.getLong(cursor.getColumnIndex(KEY_UPDATED_TIMESTAMP)),
                            cursor.getInt(cursor.getColumnIndex(KEY_ZONE)),
                            cursor.getString(cursor.getColumnIndex(KEY_HEMISPHERE)),
                            cursor.getInt(cursor.getColumnIndex(KEY_NORTHING)),
                            cursor.getInt(cursor.getColumnIndex(KEY_EASTING)),
                            cursor.getInt(cursor.getColumnIndex(KEY_SAMPLE)),
                            cursor.getInt(cursor.getColumnIndex(KEY_BEEN_SYNCED))>0);


                    dataEntryElements.add(entry);
                } while (cursor.moveToNext());

            }

        }
        catch(Exception e) {

            e.printStackTrace();

        }
        finally {

            if(cursor!=null){

                cursor.close();

            }

            db.close();

        }

        return dataEntryElements;
    }

    /**
     * Helper function to get all rows from the table
     * @return An array list of all rows fetched
     */
    public ArrayList<DataEntryElement> getUnsyncedRows() {

        ArrayList<DataEntryElement> dataEntryElements = new ArrayList<DataEntryElement>();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE "+ KEY_BEEN_SYNCED + "=0 ORDER BY " + KEY_CREATED_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        try{

            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {

                do {
                    DataEntryElement entry = new DataEntryElement(cursor.getString(cursor.getColumnIndex(KEY_ID)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_ALTITUDE)),
                            cursor.getString(cursor.getColumnIndex(KEY_STATUS)),
                            getImages(cursor.getString(cursor.getColumnIndex(KEY_ID))),
                            cursor.getString(cursor.getColumnIndex(KEY_MATERIAL)),
                            cursor.getString(cursor.getColumnIndex(KEY_COMMENT)),
                            cursor.getLong(cursor.getColumnIndex(KEY_CREATED_TIMESTAMP)),
                            cursor.getLong(cursor.getColumnIndex(KEY_UPDATED_TIMESTAMP)),
                            cursor.getInt(cursor.getColumnIndex(KEY_ZONE)),
                            cursor.getString(cursor.getColumnIndex(KEY_HEMISPHERE)),
                            cursor.getInt(cursor.getColumnIndex(KEY_NORTHING)),
                            cursor.getInt(cursor.getColumnIndex(KEY_EASTING)),
                            cursor.getInt(cursor.getColumnIndex(KEY_SAMPLE)),
                            cursor.getInt(cursor.getColumnIndex(KEY_BEEN_SYNCED))>0);


                    dataEntryElements.add(entry);
                } while (cursor.moveToNext());

            }

        }
        catch(Exception e) {

            e.printStackTrace();

        }
        finally {

            if(cursor!=null){

                cursor.close();

            }

            db.close();

        }

        return dataEntryElements;
    }

    /**
     * Helper function to fetch a single row from table
     * @param id The id of the row to be fetched
     * @return
     */
    public DataEntryElement getRow(String id) {

        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + KEY_ID + "='" + id + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        DataEntryElement dataEntryElement = null;

        try{

            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {

                do {
                    dataEntryElement = new DataEntryElement(cursor.getString(cursor.getColumnIndex(KEY_ID)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_ALTITUDE)),
                            cursor.getString(cursor.getColumnIndex(KEY_STATUS)),
                            getImages(cursor.getString(cursor.getColumnIndex(KEY_ID))),
                            cursor.getString(cursor.getColumnIndex(KEY_MATERIAL)),
                            cursor.getString(cursor.getColumnIndex(KEY_COMMENT)),
                            cursor.getLong(cursor.getColumnIndex(KEY_CREATED_TIMESTAMP)),
                            cursor.getLong(cursor.getColumnIndex(KEY_UPDATED_TIMESTAMP)),
                            cursor.getInt(cursor.getColumnIndex(KEY_ZONE)),
                            cursor.getString(cursor.getColumnIndex(KEY_HEMISPHERE)),
                            cursor.getInt(cursor.getColumnIndex(KEY_NORTHING)),
                            cursor.getInt(cursor.getColumnIndex(KEY_EASTING)),
                            cursor.getInt(cursor.getColumnIndex(KEY_SAMPLE)),
                            cursor.getInt(cursor.getColumnIndex(KEY_BEEN_SYNCED))>0);

                } while (cursor.moveToNext());

            }

        }
        catch(Exception e){

            e.printStackTrace();

        }
        finally{

            if(cursor!=null){

                cursor.close();

            }

            db.close();

        }

        return dataEntryElement;

    }

    /**
     * Helper function to delete all rows in the table
     */
    public void deleteAllRows() {

        SQLiteDatabase db = this.getWritableDatabase();

        try {

            db.delete(TABLE_NAME, null, null);
            db.delete(IMAGE_TABLE_NAME, null, null);
            db.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void addImages(String entryID, ArrayList<String> imagePaths) {
        SQLiteDatabase db = this.getWritableDatabase();

        try{

            db.beginTransaction();

            for(String imagePath : imagePaths) {

                // The values to be written in a row
                ContentValues values = new ContentValues();

                values.put(KEY_IMAGE_ID, imagePath);
                values.put(KEY_IMAGE_BUCKET, entryID);

                db.insert(IMAGE_TABLE_NAME, null, values);

            }

            db.setTransactionSuccessful();
            db.endTransaction();

        }
        catch(Exception e){

            e.printStackTrace();

        }
    }

    public void deleteImages(String entryID) {

        SQLiteDatabase db = this.getWritableDatabase();

        String deleteImagesQuery = "DELETE FROM " + IMAGE_TABLE_NAME + " WHERE " + KEY_IMAGE_BUCKET + " ='" + entryID + "'" ;

        Cursor cursor = null;

        try{

            cursor = db.rawQuery(deleteImagesQuery, null);
            cursor.getCount();

        }
        catch(Exception e){

            e.printStackTrace();

        }
        finally{

            if(cursor!=null){

                cursor.close();

            }

        }

    }

    public ArrayList<String> getImages(String entryID) {

        String selectQuery = "SELECT  * FROM " + IMAGE_TABLE_NAME + " WHERE " + KEY_IMAGE_BUCKET + "='" + entryID + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        ArrayList<String> imagePaths = new ArrayList<String>();

        try{

            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {

                do {
                    imagePaths.add(cursor.getString(cursor.getColumnIndex(KEY_IMAGE_ID)));

                } while (cursor.moveToNext());

            }

        }
        catch(Exception e){

            e.printStackTrace();

        }
        finally{

            if(cursor!=null){

                cursor.close();

            }

            db.close();

        }

        return imagePaths;

    }

    /**
     * Helper function to fetch a single row from table
     * @param zone The zone of this bucket, UTM
     * @param hemisphere The hemipshere of this bucket
     * @param northing The northing of this bucket
     * @param easting The easting of this bucket
     * @return the highest sample number found in the local db for this bucket
     */
    public Integer getLastSampleFromBucket(Integer zone, String hemisphere, Integer northing, Integer easting) {

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ZONE + "='" + zone + "' AND " + KEY_HEMISPHERE + "='" + hemisphere + "' AND " + KEY_NORTHING + "='" + northing + "' AND " + KEY_EASTING + "='" + easting + "' ORDER BY " + KEY_SAMPLE + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        Integer highestSampleNum = 0;

        try{

            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {

                highestSampleNum = cursor.getInt(cursor.getColumnIndex(KEY_SAMPLE));

            }

        }
        catch(Exception e){

            e.printStackTrace();

        }
        finally{

            if(cursor!=null){

                cursor.close();

            }

            db.close();

        }

        return highestSampleNum;

    }

}
