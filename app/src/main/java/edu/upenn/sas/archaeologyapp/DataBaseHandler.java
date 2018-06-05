package edu.upenn.sas.archaeologyapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Data base helper class to create, read and write data
 * Created by eanvith on 16/01/17.
 */

public class DataBaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 10;

    private static final String DATABASE_NAME = "BUCKETDB";

    // Table names
    private static final String FINDS_TABLE_NAME = "bucket";
    private static final String IMAGE_TABLE_NAME = "images";
    private static final String PATHS_TABLE_NAME = "paths";

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

    private static final String KEY_TEAM_MEMBER = "team_member";
    private static final String KEY_BEGIN_LATITUDE = "begin_latitude";
    private static final String KEY_BEGIN_LONGITUDE = "begin_longitude";
    private static final String KEY_BEGIN_ALTITUDE = "begin_altitude";
    private static final String KEY_END_LATITUDE = "end_latitude";
    private static final String KEY_END_LONGITUDE = "end_longitude";
    private static final String KEY_END_ALTITUDE = "end_altitude";
    private static final String KEY_BEGIN_EASTING = "begin_easting";
    private static final String KEY_BEGIN_NORTHING = "begin_northing";
    private static final String KEY_END_EASTING = "end_easting";
    private static final String KEY_END_NORTHING = "end_northing";
    private static final String KEY_BEGIN_TIME = "start_time";
    private static final String KEY_END_TIME = "stop_time";

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

        String CREATE_BUCKET_TABLE = "CREATE TABLE " + FINDS_TABLE_NAME +
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

        String CREATE_PATHS_TABLE = "CREATE TABLE " + PATHS_TABLE_NAME +
                "(" + KEY_TEAM_MEMBER + " TEXT,"
                + KEY_BEGIN_LATITUDE + " FLOAT,"
                + KEY_BEGIN_LONGITUDE + " FLOAT,"
                + KEY_BEGIN_ALTITUDE + " FLOAT,"
                + KEY_END_LATITUDE + " FLOAT,"
                + KEY_END_LONGITUDE + " FLOAT,"
                + KEY_END_ALTITUDE + " FLOAT,"
                + KEY_HEMISPHERE + " TEXT,"
                + KEY_ZONE + " INTEGER,"
                + KEY_BEGIN_EASTING + " INTEGER,"
                + KEY_BEGIN_NORTHING + " INTEGER,"
                + KEY_END_EASTING + " INTEGER,"
                + KEY_END_NORTHING + " INTEGER,"
                + KEY_BEGIN_TIME + " FLOAT,"
                + KEY_END_TIME + " FLOAT,"
                + "PRIMARY KEY ("+ KEY_TEAM_MEMBER +", "+ KEY_BEGIN_TIME +"));"
                ;

        db.execSQL(CREATE_BUCKET_TABLE);
        db.execSQL(CREATE_IMAGE_TABLE);
        db.execSQL(CREATE_PATHS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + FINDS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + IMAGE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PATHS_TABLE_NAME);

        // Create table again
        onCreate(db);

    }

    /**
     * Helper function to drop the previous table and create a new empty table
     */
    public void newTable() {

        SQLiteDatabase db = this.getWritableDatabase();

        try {

            db.execSQL("DROP TABLE IF EXISTS " + FINDS_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + IMAGE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + PATHS_TABLE_NAME);
            onCreate(db);

        } catch(Exception e) {

            e.printStackTrace();

        } finally {
            db.close();
        }

    }

    /**
     * Helper function to remove
     * @param id The id of the element to be removed from the table
     */
    public void removeFindsRow(String id){

        SQLiteDatabase db = this.getWritableDatabase();

        String deleteEntriesQuery = "DELETE FROM " + FINDS_TABLE_NAME + " WHERE " + KEY_ID + " ='" + id + "'" ;
        String deleteImagesQuery = "DELETE FROM " + IMAGE_TABLE_NAME + " WHERE " + KEY_IMAGE_BUCKET + " ='" + id + "'" ;

        Cursor cursor = null;

        try {

            cursor = db.rawQuery(deleteEntriesQuery, null);
            cursor.getCount();

            cursor = db.rawQuery(deleteImagesQuery, null);
            cursor.getCount();

        } catch(Exception e) {

            e.printStackTrace();

        } finally {

            if(cursor!=null){

                cursor.close();

            }

        }

    }

    /**
     * Helper function to remove a path
     * @param teamMember The teamMember who added the path
     * @param startTime The time the path was logged
     */
    public void removePathsRow(String teamMember, long startTime) {

        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "DELETE FROM " + PATHS_TABLE_NAME + " WHERE " + KEY_TEAM_MEMBER + " ='" + teamMember + "' AND " + KEY_BEGIN_TIME + " ='" + startTime + "'";

        Cursor cursor = null;

        try {

            cursor = db.rawQuery(selectQuery, null);
            cursor.getCount();

        } catch(Exception e) {

            e.printStackTrace();

        } finally {

            if (cursor != null) {

                cursor.close();

            }

        }

    }

    /**
     * Helper function to add elements to the table
     * @param entry
     */
    public void addFindsRows(DataEntryElement[] entry) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {

            db.beginTransaction();

            for(int i = 0; i < entry.length; i++) {

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
                int rowsAffected = db.update(FINDS_TABLE_NAME, values, KEY_ID + " ='" + entry[i].getID()+"'", null);

                // If update call fails, rowsAffected will be 0. If not, it means the row was updated
                if(rowsAffected == 0){

                    // If update fails, it means the ID does not exist in table. Create a new row with the ID.
                    values.put(KEY_ID, entry[i].getID());

                    // Also add timestamp for creation of entry
                    values.put(KEY_CREATED_TIMESTAMP, entry[i].getCreatedTimestamp());

                    db.insert(FINDS_TABLE_NAME, null, values);

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
     * Helper function to set a find to synced
     * @param entry
     */
    public void setFindSynced(DataEntryElement entry) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {

            db.beginTransaction();

            // The values to be written in a row
            ContentValues values = new ContentValues();

            values.put(KEY_LATITUDE, entry.getLatitude());
            values.put(KEY_LONGITUDE, entry.getLongitude());
            values.put(KEY_ALTITUDE, entry.getAltitude());
            values.put(KEY_STATUS, entry.getStatus());
            values.put(KEY_MATERIAL, entry.getMaterial());
            values.put(KEY_COMMENT, entry.getComments());
            values.put(KEY_UPDATED_TIMESTAMP, entry.getUpdateTimestamp());
            values.put(KEY_ZONE, entry.getZone());
            values.put(KEY_HEMISPHERE, entry.getHemisphere());
            values.put(KEY_NORTHING, entry.getNorthing());
            values.put(KEY_EASTING, entry.getEasting());
            values.put(KEY_SAMPLE, entry.getSample());

            // Set beenSynced to true
            values.put(KEY_BEEN_SYNCED, 1);

            // Make an update call
            int rowsAffected = db.update(FINDS_TABLE_NAME, values, KEY_ID + " ='" + entry.getID()+"'", null);

            // If update call fails, rowsAffected will be 0. If not, it means the row was updated
            if (rowsAffected == 1){

                // Success

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
     * Helper function to add paths to the table
     * @param entry
     */
    public void addPathsRows(PathElement[] entry) {

        SQLiteDatabase db = this.getWritableDatabase();

        try{

            db.beginTransaction();

            for(int i = 0; i < entry.length; i++)
            {

                // The values to be written in a row
                ContentValues values = new ContentValues();

                values.put(KEY_BEGIN_LATITUDE, entry[i].getBeginLatitude());
                values.put(KEY_BEGIN_LONGITUDE, entry[i].getBeginLongitude());
                values.put(KEY_BEGIN_ALTITUDE, entry[i].getBeginAltitude());
                values.put(KEY_END_LATITUDE, entry[i].getEndLatitude());
                values.put(KEY_END_LONGITUDE, entry[i].getEndLongitude());
                values.put(KEY_END_ALTITUDE, entry[i].getEndAltitude());
                values.put(KEY_HEMISPHERE, entry[i].getHemisphere());
                values.put(KEY_ZONE, entry[i].getZone());
                values.put(KEY_BEGIN_NORTHING, entry[i].getBeginNorthing());
                values.put(KEY_BEGIN_EASTING, entry[i].getBeginEasting());
                values.put(KEY_END_NORTHING, entry[i].getEndNorthing());
                values.put(KEY_END_EASTING, entry[i].getEndEasting());
                values.put(KEY_END_TIME, entry[i].getEndTime());

                // Try to make an update call
                int rowsAffected = db.update(PATHS_TABLE_NAME, values, KEY_TEAM_MEMBER + " ='" + entry[i].getTeamMember() +"' AND " + KEY_BEGIN_TIME + "='" + entry[i].getBeginTime() + "'", null);

                // If update call fails, rowsAffected will be 0. If not, it means the row was updated
                if (rowsAffected == 0) {

                    // If update fails, it means the key does not exist in table. Create a new row with the given teamMember and startTime.
                    values.put(KEY_TEAM_MEMBER, entry[i].getTeamMember());
                    values.put(KEY_BEGIN_TIME, entry[i].getBeginTime());

                    db.insert(PATHS_TABLE_NAME, null, values);

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
    public ArrayList<DataEntryElement> getFindsRows() {

        ArrayList<DataEntryElement> dataEntryElements = new ArrayList<DataEntryElement>();

        String selectQuery = "SELECT  * FROM " + FINDS_TABLE_NAME + " ORDER BY " + KEY_CREATED_TIMESTAMP + " DESC";

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
     * Helper function to get all paths from the table
     * @return An array list of all rows fetched
     */
    public ArrayList<PathElement> getPathsRows() {

        ArrayList<PathElement> pathElements = new ArrayList<PathElement>();

        String selectQuery = "SELECT  * FROM " + PATHS_TABLE_NAME + " ORDER BY " + KEY_BEGIN_TIME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        try{

            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {

                do {

                    PathElement entry = new PathElement(cursor.getString(cursor.getColumnIndex(KEY_TEAM_MEMBER)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_BEGIN_LATITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_BEGIN_LONGITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_BEGIN_ALTITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_END_LATITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_END_LONGITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_END_ALTITUDE)),
                            cursor.getString(cursor.getColumnIndex(KEY_HEMISPHERE)),
                            cursor.getInt(cursor.getColumnIndex(KEY_ZONE)),
                            cursor.getInt(cursor.getColumnIndex(KEY_BEGIN_EASTING)),
                            cursor.getInt(cursor.getColumnIndex(KEY_BEGIN_NORTHING)),
                            cursor.getInt(cursor.getColumnIndex(KEY_END_EASTING)),
                            cursor.getInt(cursor.getColumnIndex(KEY_END_NORTHING)),
                            cursor.getLong(cursor.getColumnIndex(KEY_BEGIN_TIME)),
                            cursor.getLong(cursor.getColumnIndex(KEY_END_TIME)));

                    pathElements.add(entry);

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

        return pathElements;
    }

    /**
     * Helper function to get all rows from the table
     * @return An array list of all rows fetched
     */
    public ArrayList<DataEntryElement> getUnsyncedFindsRows() {

        ArrayList<DataEntryElement> dataEntryElements = new ArrayList<DataEntryElement>();

        String selectQuery = "SELECT  * FROM " + FINDS_TABLE_NAME + " WHERE "+ KEY_BEEN_SYNCED + "=0 ORDER BY " + KEY_CREATED_TIMESTAMP + " DESC";

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
    public DataEntryElement getFindsRow(String id) {

        String selectQuery = "SELECT  * FROM " + FINDS_TABLE_NAME + " WHERE " + KEY_ID + "='" + id + "'";

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
     * Helper function to fetch a single path from table
     * @param teamMember The teamMember who logged the path
     * @param startTime The start time of the paths log
     * @return
     */
    public PathElement getPathsRow(String teamMember, long startTime) {

        String selectQuery = "SELECT  * FROM " + PATHS_TABLE_NAME + " WHERE " + KEY_TEAM_MEMBER + "='" + teamMember + "' AND " + KEY_BEGIN_TIME + "='" + startTime + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        PathElement pathElement = null;

        try{

            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {

                do {

                    PathElement entry = new PathElement(cursor.getString(cursor.getColumnIndex(KEY_TEAM_MEMBER)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_BEGIN_LATITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_BEGIN_LONGITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_BEGIN_ALTITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_END_LATITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_END_LONGITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_END_ALTITUDE)),
                            cursor.getString(cursor.getColumnIndex(KEY_HEMISPHERE)),
                            cursor.getInt(cursor.getColumnIndex(KEY_ZONE)),
                            cursor.getInt(cursor.getColumnIndex(KEY_BEGIN_EASTING)),
                            cursor.getInt(cursor.getColumnIndex(KEY_BEGIN_NORTHING)),
                            cursor.getInt(cursor.getColumnIndex(KEY_END_EASTING)),
                            cursor.getInt(cursor.getColumnIndex(KEY_END_NORTHING)),
                            cursor.getLong(cursor.getColumnIndex(KEY_BEGIN_TIME)),
                            cursor.getLong(cursor.getColumnIndex(KEY_END_TIME)));

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

        return pathElement;

    }

    /**
     * Helper function to delete all rows in the table
     */
    public void deleteAllFindsRows() {

        SQLiteDatabase db = this.getWritableDatabase();

        try {

            db.delete(FINDS_TABLE_NAME, null, null);
            db.delete(IMAGE_TABLE_NAME, null, null);
            db.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    /**
     * Helper function to delete all paths in the table
     */
    public void deleteAllPathsRows() {

        SQLiteDatabase db = this.getWritableDatabase();

        try {

            db.delete(PATHS_TABLE_NAME, null, null);
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

        String selectQuery = "SELECT * FROM " + FINDS_TABLE_NAME + " WHERE " + KEY_ZONE + "='" + zone + "' AND " + KEY_HEMISPHERE + "='" + hemisphere + "' AND " + KEY_NORTHING + "='" + northing + "' AND " + KEY_EASTING + "='" + easting + "' ORDER BY " + KEY_SAMPLE + " DESC";

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
