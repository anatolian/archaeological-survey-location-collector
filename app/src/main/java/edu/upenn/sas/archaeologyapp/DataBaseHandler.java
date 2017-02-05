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

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "BUCKETDB";

    // Table name
    private static final String TABLE_NAME = "bucket";

    // Table Columns names
    private static final String KEY_ID = "bucket_id";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_IMAGE_PATH = "image_path";
    private static final String KEY_MATERIAL = "material";
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_CREATED_TIMESTAMP = "created_timestamp";

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
                + KEY_IMAGE_PATH + " TEXT,"
                + KEY_MATERIAL + " TEXT,"
                + KEY_COMMENT + " TEXT,"
                + KEY_CREATED_TIMESTAMP + " INTEGER)"
                ;


        db.execSQL(CREATE_BUCKET_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

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
    private void removeRow(String id){

        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "DELETE FROM " + TABLE_NAME + " WHERE " + KEY_ID + " ='" + id + "'" ;

        Cursor cursor = null;

        try{

            cursor = db.rawQuery(selectQuery, null);
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
                values.put(KEY_IMAGE_PATH, entry[i].getImagePath());
                values.put(KEY_MATERIAL, entry[i].getMaterial());
                values.put(KEY_COMMENT, entry[i].getComments());

                // Try to make an update call
                int rowsAffected = db.update(TABLE_NAME, values, KEY_ID + " ='" + entry[i].getID()+"'", null);

                // If update call fails, rowsAffected will be 0. If not, it means the row was updated
                if(rowsAffected == 0){

                    // If update fails, it means the ID does not exist in table. Create a new row with the ID.
                    values.put(KEY_ID, entry[i].getID());

                    // Also add timestamp for creation of entry
                    values.put(KEY_CREATED_TIMESTAMP, entry[i].getCreatedTimestamp());

                    db.insert(TABLE_NAME, null, values);

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

        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        try{

            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {

                do {

                    DataEntryElement entry = new DataEntryElement(cursor.getString(cursor.getColumnIndex(KEY_ID)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),
                            cursor.getString(cursor.getColumnIndex(KEY_IMAGE_PATH)),
                            cursor.getString(cursor.getColumnIndex(KEY_MATERIAL)),
                            cursor.getString(cursor.getColumnIndex(KEY_COMMENT)),
                            cursor.getLong(cursor.getColumnIndex(KEY_CREATED_TIMESTAMP)));

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
                            cursor.getString(cursor.getColumnIndex(KEY_IMAGE_PATH)),
                            cursor.getString(cursor.getColumnIndex(KEY_MATERIAL)),
                            cursor.getString(cursor.getColumnIndex(KEY_COMMENT)),
                            cursor.getLong(cursor.getColumnIndex(KEY_CREATED_TIMESTAMP)));

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
            db.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}
