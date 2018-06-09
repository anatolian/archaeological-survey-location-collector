package edu.upenn.sas.archaeologyapp.services;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import edu.upenn.sas.archaeologyapp.models.DataEntryElement;
import edu.upenn.sas.archaeologyapp.models.PathElement;
/**
 * Data base helper class to create, read and write data
 * Created by eanvith on 16/01/17.
 */
public class DatabaseHandler extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 13;
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
    private static final String KEY_AR_RATIO = "AR_ratio";
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
    private static final String KEY_BEGIN_STATUS = "begin_status";
    private static final String KEY_END_STATUS = "end_status";
    private static final String KEY_BEGIN_AR_RATIO = "begin_AR_ratio";
    private static final String KEY_END_AR_RATIO = "end_AR_ratio";
    private static final String KEY_IMAGE_ID = "image_name";
    private static final String KEY_IMAGE_BUCKET = "image_bucket";
    /**
     * Constructor
     * @param context The current app context
     */
    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Activity launched
     * @param db - database
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_BUCKET_TABLE = "CREATE TABLE " + FINDS_TABLE_NAME + "(" + KEY_ID + " TEXT PRIMARY KEY,"
                + KEY_LATITUDE + " FLOAT," + KEY_LONGITUDE + " FLOAT," + KEY_ALTITUDE + " FLOAT,"
                + KEY_STATUS + " TEXT," + KEY_AR_RATIO + " FLOAT," + KEY_MATERIAL + " TEXT,"
                + KEY_COMMENT + " TEXT," + KEY_UPDATED_TIMESTAMP + " INTEGER," + KEY_CREATED_TIMESTAMP + " INTEGER,"
                + KEY_ZONE + " INTEGER," + KEY_HEMISPHERE + " TEXT," + KEY_NORTHING + " INTEGER,"
                + KEY_EASTING + " INTEGER," + KEY_SAMPLE + " INTEGER," + KEY_BEEN_SYNCED + " INTEGER)";
        String CREATE_IMAGE_TABLE = "CREATE TABLE " + IMAGE_TABLE_NAME + "(" + KEY_IMAGE_ID + " TEXT PRIMARY KEY,"
                + KEY_IMAGE_BUCKET + " TEXT)";
        String CREATE_PATHS_TABLE = "CREATE TABLE " + PATHS_TABLE_NAME + "(" + KEY_TEAM_MEMBER + " TEXT,"
                + KEY_BEGIN_LATITUDE + " FLOAT," + KEY_BEGIN_LONGITUDE + " FLOAT," + KEY_BEGIN_ALTITUDE + " FLOAT,"
                + KEY_END_LATITUDE + " FLOAT," + KEY_END_LONGITUDE + " FLOAT," + KEY_END_ALTITUDE + " FLOAT,"
                + KEY_HEMISPHERE + " TEXT," + KEY_ZONE + " INTEGER," + KEY_BEGIN_EASTING + " INTEGER,"
                + KEY_BEGIN_NORTHING + " INTEGER," + KEY_END_EASTING + " INTEGER," + KEY_END_NORTHING + " INTEGER,"
                + KEY_BEGIN_TIME + " FLOAT," + KEY_END_TIME + " FLOAT," + KEY_BEGIN_STATUS + " TEXT,"
                + KEY_END_STATUS + " TEXT," + KEY_BEGIN_AR_RATIO + " FLOAT," + KEY_END_AR_RATIO + " FLOAT,"
                + KEY_BEEN_SYNCED + " INTEGER," + "PRIMARY KEY ("+ KEY_TEAM_MEMBER +", "+ KEY_BEGIN_TIME +"));";
        db.execSQL(CREATE_BUCKET_TABLE);
        db.execSQL(CREATE_IMAGE_TABLE);
        db.execSQL(CREATE_PATHS_TABLE);
    }

    /**
     * Upgrade database version
     * @param db - database
     * @param oldVersion - old version number
     * @param newVersion - new version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + FINDS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + IMAGE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PATHS_TABLE_NAME);
        // Create table again
        onCreate(db);
    }

    /**
     * Helper function to add elements to the table
     * @param entry - entry to add
     */
    public void addFindsRows(DataEntryElement[] entry)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            db.beginTransaction();
            for (DataEntryElement e: entry)
            {
                // The values to be written in a row
                ContentValues values = new ContentValues();
                values.put(KEY_LATITUDE, e.getLatitude());
                values.put(KEY_LONGITUDE, e.getLongitude());
                values.put(KEY_ALTITUDE, e.getAltitude());
                values.put(KEY_STATUS, e.getStatus());
                values.put(KEY_AR_RATIO, e.getARRatio());
                values.put(KEY_MATERIAL, e.getMaterial());
                values.put(KEY_COMMENT, e.getComments());
                values.put(KEY_UPDATED_TIMESTAMP, e.getUpdateTimestamp());
                values.put(KEY_ZONE, e.getZone());
                values.put(KEY_HEMISPHERE, e.getHemisphere());
                values.put(KEY_NORTHING, e.getNorthing());
                values.put(KEY_EASTING, e.getEasting());
                values.put(KEY_SAMPLE, e.getSample());
                values.put(KEY_BEEN_SYNCED, e.getBeenSynced() ? 1 : 0);
                // Try to make an update call
                int rowsAffected = db.update(FINDS_TABLE_NAME, values, KEY_ID + " ='" + e.getID()+"'", null);
                // If update call fails, rowsAffected will be 0. If not, it means the row was updated
                if (rowsAffected == 0)
                {
                    // If update fails, it means the ID does not exist in table. Create a new row with the ID.
                    values.put(KEY_ID, e.getID());
                    // Also add timestamp for creation of entry
                    values.put(KEY_CREATED_TIMESTAMP, e.getCreatedTimestamp());
                    db.insert(FINDS_TABLE_NAME, null, values);
                    // Add associated images to the image table
                    addImages(e.getID(), e.getImagePaths());
                }
                else
                {
                    // Update successful, delete and re-add images
                    deleteImages(e.getID());
                    addImages(e.getID(), e.getImagePaths());
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            db.close();
        }
    }

    /**
     * Helper function to set a find to synced
     * @param entry - synced entry
     */
    public void setFindSynced(DataEntryElement entry)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            db.beginTransaction();
            // The values to be written in a row
            ContentValues values = new ContentValues();
            values.put(KEY_LATITUDE, entry.getLatitude());
            values.put(KEY_LONGITUDE, entry.getLongitude());
            values.put(KEY_ALTITUDE, entry.getAltitude());
            values.put(KEY_STATUS, entry.getStatus());
            values.put(KEY_AR_RATIO, entry.getARRatio());
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
            db.update(FINDS_TABLE_NAME, values, KEY_ID + " ='" + entry.getID()+"'", null);
            // If update call fails, rowsAffected will be 0. If not, it means the row was updated
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            db.close();
        }
    }

    /**
     * Helper function to add paths to the table
     * @param entry - entry to add
     */
    public void addPathsRows(PathElement[] entry)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            db.beginTransaction();
            for (PathElement e: entry)
            {
                // The values to be written in a row
                ContentValues values = new ContentValues();
                values.put(KEY_BEGIN_LATITUDE, e.getBeginLatitude());
                values.put(KEY_BEGIN_LONGITUDE, e.getBeginLongitude());
                values.put(KEY_BEGIN_ALTITUDE, e.getBeginAltitude());
                values.put(KEY_BEGIN_STATUS, e.getBeginStatus());
                values.put(KEY_BEGIN_AR_RATIO, e.getBeginARRatio());
                values.put(KEY_END_LATITUDE, e.getEndLatitude());
                values.put(KEY_END_LONGITUDE, e.getEndLongitude());
                values.put(KEY_END_ALTITUDE, e.getEndAltitude());
                values.put(KEY_END_STATUS, e.getEndStatus());
                values.put(KEY_END_AR_RATIO, e.getEndARRatio());
                values.put(KEY_HEMISPHERE, e.getHemisphere());
                values.put(KEY_ZONE, e.getZone());
                values.put(KEY_BEGIN_NORTHING, e.getBeginNorthing());
                values.put(KEY_BEGIN_EASTING, e.getBeginEasting());
                values.put(KEY_END_NORTHING, e.getEndNorthing());
                values.put(KEY_END_EASTING, e.getEndEasting());
                values.put(KEY_END_TIME, e.getEndTime());
                values.put(KEY_BEEN_SYNCED, e.getBeenSynced() ? 1 : 0);
                // Try to make an update call
                int rowsAffected = db.update(PATHS_TABLE_NAME, values, KEY_TEAM_MEMBER + " ='"
                        + e.getTeamMember() +"' AND " + KEY_BEGIN_TIME + "='" + e.getBeginTime() + "'", null);
                // If update call fails, rowsAffected will be 0. If not, it means the row was updated
                if (rowsAffected == 0)
                {
                    // If update fails, it means the key does not exist in table. Create a new row
                    // with the given teamMember and startTime.
                    values.put(KEY_TEAM_MEMBER, e.getTeamMember());
                    values.put(KEY_BEGIN_TIME, e.getBeginTime());
                    db.insert(PATHS_TABLE_NAME, null, values);
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            db.close();
        }
    }

    /**
     * Helper function to set a path to synced
     * @param entry - synced entry
     */
    public void setPathSynced(PathElement entry)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            db.beginTransaction();
            // The values to be written in a row
            ContentValues values = new ContentValues();
            values.put(KEY_BEGIN_LATITUDE, entry.getBeginLatitude());
            values.put(KEY_BEGIN_LONGITUDE, entry.getBeginLongitude());
            values.put(KEY_BEGIN_ALTITUDE, entry.getBeginAltitude());
            values.put(KEY_BEGIN_STATUS, entry.getBeginStatus());
            values.put(KEY_BEGIN_AR_RATIO, entry.getBeginARRatio());
            values.put(KEY_END_LATITUDE, entry.getEndLatitude());
            values.put(KEY_END_LONGITUDE, entry.getEndLongitude());
            values.put(KEY_END_ALTITUDE, entry.getEndAltitude());
            values.put(KEY_END_STATUS, entry.getEndStatus());
            values.put(KEY_END_AR_RATIO, entry.getEndARRatio());
            values.put(KEY_HEMISPHERE, entry.getHemisphere());
            values.put(KEY_ZONE, entry.getZone());
            values.put(KEY_BEGIN_NORTHING, entry.getBeginNorthing());
            values.put(KEY_BEGIN_EASTING, entry.getBeginEasting());
            values.put(KEY_END_NORTHING, entry.getEndNorthing());
            values.put(KEY_END_EASTING, entry.getEndEasting());
            values.put(KEY_END_TIME, entry.getEndTime());
            // Set beenSynced to true
            values.put(KEY_BEEN_SYNCED, 1);
            // Try to make an update call
            db.update(PATHS_TABLE_NAME, values, KEY_TEAM_MEMBER + " ='" + entry.getTeamMember()
                    + "' AND " + KEY_BEGIN_TIME + "='" + entry.getBeginTime() + "'", null);
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            db.close();
        }
    }

    /**
     * Helper function to get all rows from the table
     * @return An array list of all rows fetched
     */
    public ArrayList<DataEntryElement> getUnsyncedFindsRows()
    {
        ArrayList<DataEntryElement> dataEntryElements = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + FINDS_TABLE_NAME + " WHERE "+ KEY_BEEN_SYNCED +
                "=0 ORDER BY " + KEY_CREATED_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try
        {
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst())
            {
                do
                {
                    DataEntryElement entry = new DataEntryElement(cursor.getString(cursor.getColumnIndex(KEY_ID)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_ALTITUDE)),
                            cursor.getString(cursor.getColumnIndex(KEY_STATUS)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_AR_RATIO)),
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
                }
                while (cursor.moveToNext());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
            db.close();
        }
        return dataEntryElements;
    }

    /**
     * Helper function to get all unsynced paths from the database
     * @return An array list of all rows fetched
     */
    public ArrayList<PathElement> getUnsyncedPathsRows()
    {
        ArrayList<PathElement> pathElements = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + PATHS_TABLE_NAME + " WHERE " + KEY_BEEN_SYNCED + "=0 ORDER BY " + KEY_BEGIN_TIME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try
        {
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst())
            {
                do
                {
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
                            cursor.getLong(cursor.getColumnIndex(KEY_END_TIME)),
                            cursor.getString(cursor.getColumnIndex(KEY_BEGIN_STATUS)),
                            cursor.getString(cursor.getColumnIndex(KEY_END_STATUS)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_BEGIN_AR_RATIO)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_END_AR_RATIO)),
                            cursor.getInt(cursor.getColumnIndex(KEY_BEEN_SYNCED))>0);
                    pathElements.add(entry);
                }
                while (cursor.moveToNext());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
            db.close();
        }
        return pathElements;
    }

    private void addImages(String entryID, ArrayList<String> imagePaths)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            db.beginTransaction();
            for (String imagePath: imagePaths)
            {
                // The values to be written in a row
                ContentValues values = new ContentValues();
                values.put(KEY_IMAGE_ID, imagePath);
                values.put(KEY_IMAGE_BUCKET, entryID);
                db.insert(IMAGE_TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Delete images
     * @param entryID - entry to delete
     */
    private void deleteImages(String entryID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteImagesQuery = "DELETE FROM " + IMAGE_TABLE_NAME + " WHERE " + KEY_IMAGE_BUCKET + " ='" + entryID + "'" ;
        Cursor cursor = null;
        try
        {
            cursor = db.rawQuery(deleteImagesQuery, null);
            cursor.getCount();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
    }

    /**
     * Get images
     * @param entryID - item whose images to fetch
     * @return Returns the images
     */
    private ArrayList<String> getImages(String entryID)
    {
        String selectQuery = "SELECT  * FROM " + IMAGE_TABLE_NAME + " WHERE " + KEY_IMAGE_BUCKET + "='" + entryID + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        ArrayList<String> imagePaths = new ArrayList<>();
        try
        {
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst())
            {
                do
                {
                    imagePaths.add(cursor.getString(cursor.getColumnIndex(KEY_IMAGE_ID)));
                }
                while (cursor.moveToNext());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (cursor != null)
            {
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
    public Integer getLastSampleFromBucket(Integer zone, String hemisphere, Integer northing, Integer easting)
    {
        String selectQuery = "SELECT * FROM " + FINDS_TABLE_NAME + " WHERE " + KEY_ZONE + "='" + zone + "' AND "
                + KEY_HEMISPHERE + "='" + hemisphere + "' AND " + KEY_NORTHING + "='" + northing + "' AND "
                + KEY_EASTING + "='" + easting + "' ORDER BY " + KEY_SAMPLE + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        Integer highestSampleNum = 0;
        try
        {
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst())
            {
                highestSampleNum = cursor.getInt(cursor.getColumnIndex(KEY_SAMPLE));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
            db.close();
        }
        return highestSampleNum;
    }
}