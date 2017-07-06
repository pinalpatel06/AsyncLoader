package com.knoxpo.runtracker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import com.knoxpo.runtracker.model.Run;

import java.util.Date;

/**
 * Created by Tejas Sherdiwala on 12/6/2016.
 * &copy; Knoxpo
 */

public class RunDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "runs.sqlite";
    private static final int VERSION = 1;

    //Run table & its column
    private static final String TABLE_RUN = "run";
    private static final String COLUMN_RUN_ID = "_id";
    private static final String COLUMN_RUN_START_DATE = "start_date";


    //Location table & its column
    private static final String TABLE_LOCATION = "location";
    private static final String
            COLUMN_LOCATION_LATITUDE = "latitude",
            COLUMN_LOCATION_LONGITUDE = "longitude",
            COLUMN_LOCATION_ALTITUDE = "altitude",
            COLUMN_LOCATION_TIMESTAMP = "timestamp",
            COLUMN_LOCATION_PROVIDER = "provider",
            COLUMN_LOCATION_RUN_ID = "run_id";

    public RunDatabaseHelper(Context context){
        super(context, DB_NAME,null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+ TABLE_RUN + "(" +
            "_id integer primary key autoincrement," + COLUMN_RUN_START_DATE+" integer)");

        sqLiteDatabase.execSQL("create table "+ TABLE_LOCATION + "(" +
                COLUMN_LOCATION_TIMESTAMP + " integer," + COLUMN_LOCATION_LATITUDE + " real," +
                COLUMN_LOCATION_LONGITUDE + " real," + COLUMN_LOCATION_ALTITUDE+ " real," +
                COLUMN_LOCATION_PROVIDER + " varchar(100)," + COLUMN_LOCATION_RUN_ID + " integer references run(_id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long insertRun(Run run){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_RUN_START_DATE , run.getStartDate().getTime());
        return getWritableDatabase().insert(TABLE_RUN,null,contentValues);
    }

    public long insertLocation(long runId, Location location){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LOCATION_LATITUDE,location.getLatitude());
        cv.put(COLUMN_LOCATION_LONGITUDE,location.getLongitude());
        cv.put(COLUMN_LOCATION_ALTITUDE,location.getAltitude());
        cv.put(COLUMN_LOCATION_TIMESTAMP,location.getTime());
        cv.put(COLUMN_LOCATION_PROVIDER,location.getProvider());
        cv.put(COLUMN_LOCATION_RUN_ID,runId);
        return getWritableDatabase().insert(TABLE_LOCATION,null,cv);
    }

    public RunCursor queryRuns(){
        Cursor wrapped = getReadableDatabase().query(TABLE_RUN,null,null,null,null,null,COLUMN_RUN_START_DATE + " asc");

        return new RunCursor(wrapped);
    }

    public RunCursor queryRun(long id){
        Cursor wrapped = getReadableDatabase().query(TABLE_RUN,
                null,
                COLUMN_RUN_ID + "= ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                "1");

        return new RunCursor(wrapped);

    }

    public LocationCursor queryLastLocationForRun(long runId){
        Cursor wrapped = getReadableDatabase().query(TABLE_LOCATION,
                null,
                COLUMN_LOCATION_RUN_ID + "= ?",
                new String[]{String.valueOf(runId)},
                null,
                null,
                COLUMN_LOCATION_TIMESTAMP + " desc",
                "1");
        return new LocationCursor(wrapped);
    }
    public static class RunCursor extends CursorWrapper{

        public RunCursor(Cursor cursor){
            super(cursor);
        }

        public Run getRun(){
            if(isBeforeFirst() || isAfterLast()){
                return null;
            }else{
                Run run = new Run();
                long runId = getLong(getColumnIndex(COLUMN_RUN_ID));
                run.setId(runId);

                long startDate = getLong(getColumnIndex(COLUMN_RUN_START_DATE));
                run.setStartDate(new Date(startDate));
                return run;
            }
        }
    }

    public static class LocationCursor extends CursorWrapper{
        public LocationCursor(Cursor cursor){
            super(cursor);
        }

        public Location getLocation(){
            if(isBeforeFirst() || isAfterLast()){
                return null;
            }else{
                String provider = getString(getColumnIndex(COLUMN_LOCATION_PROVIDER));

                Location loc = new Location(provider);
                loc.setLongitude(getDouble(getColumnIndex(COLUMN_LOCATION_LONGITUDE)));
                loc.setLatitude(getDouble(getColumnIndex(COLUMN_LOCATION_LATITUDE)));
                loc.setAltitude(getDouble(getColumnIndex(COLUMN_LOCATION_ALTITUDE)));
                loc.setTime(getLong(getColumnIndex(COLUMN_LOCATION_TIMESTAMP)));
                return loc;
            }
        }
    }
}
