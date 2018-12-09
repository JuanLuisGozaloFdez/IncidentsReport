/*
 * Copyright (c) 2018. HuanLu Robotics. Todos los derechos reservados / All rigths reserved.
 */

package com.huanlurobotics.incidentsreport.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.huanlurobotics.incidentsreport.Incident;
import com.huanlurobotics.incidentsreport.database.IncidentDbSchema.IncidentTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class IncidentLab {

    private static final String TAG = "IncidentsReport";

    private static IncidentLab sIncidentLab;
    // private List<Incident> mIncidents; // not required with DB
    private Context mContext;
    private SQLiteDatabase mDatabase;

    /*
    IncidentLab Singleton
     */
    public static IncidentLab get (Context context) {
        if (sIncidentLab == null) {
            sIncidentLab = new IncidentLab (context);
        }
        return sIncidentLab;
    }

    private IncidentLab (Context context) {
        openDB(context);
        // mIncidents = new ArrayList<>();// not required with DB
        // TODO llevar esta función a una Unit Test como parte del pre-test
         /* // Generate a set of examples incidents, not required with the ADD function and, later, DB
         for (int i = 0; i<100; i++) {
             Incident incident = new Incident();
             incident.setTitle("Incident #" + i);
             incident.setSolved(i%2 ==0); // uno true, otro false
             mIncidents.add(incident);
         }
         */
    }

    // methods
    public void  openDB (Context context) throws SQLException {
        mContext = context.getApplicationContext();
        mDatabase = new IncidentBaseHelper(mContext).getWritableDatabase();
    }

    public void closeDB() {
        mDatabase.close();
    }


    public List<Incident> getIncidents() {

        //return mIncidents;// not required with DB
        List<Incident> incidents = new ArrayList<>();

        IncidentCursorWrapper cursor = queryIncidents(null,null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                incidents.add(cursor.getIncident());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return incidents;
    }

    public Incident getIncident(UUID id) {
        /* // not required with DB
        for (Incident incident: mIncidents) {
            if (incident.getId().equals(id)) {
                return incident;
            }
        }
        return null;
        */
        IncidentCursorWrapper cursor = queryIncidents(IncidentTable.Cols.UUID + " = ?",
                new String[] { id.toString()}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getIncident();
        } finally {
            cursor.close();
        }
    }

    public void addIncident (Incident c) {

        Log.d(TAG, "IncidentLab.addIncident started with Incident "+c.toString());
        // mIncidents.add(c); // not required with DB
        ContentValues values = getContentValues(c);
        Log.d(TAG, "IncidentLab.addIncident values= "+ values.toString());
        mDatabase.insert(IncidentTable.NAME, null, values);
        Log.d(TAG, "IncidentLab.addIncident done");

    }

    public void updateIncident (Incident incident) {
        String uuidString = incident.getId().toString();
        ContentValues values = getContentValues(incident);

        mDatabase.update(IncidentTable.NAME,
                values,
                IncidentTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    public void deleteIncident (Incident incident) {
        String uuidString = incident.getId().toString();

        mDatabase.delete(IncidentTable.NAME,
                IncidentTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private IncidentCursorWrapper queryIncidents (String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                IncidentTable.NAME,
                null, //Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        // return cursor;
        IncidentCursorWrapper mIncidentCursorWrapper = new IncidentCursorWrapper(cursor);
        cursor.close();
        return mIncidentCursorWrapper;
    }

    // location of the pictures of the incident
    public File getPhotoFile (Incident incident)  {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir == null) {
            return null;
        }
        return new File(externalFilesDir, incident.getPhotoFilename());
    }

    private static ContentValues getContentValues(Incident incident) {
        ContentValues values = new ContentValues();
        values.put(IncidentTable.Cols.UUID, incident.getId().toString());
        values.put(IncidentTable.Cols.TITLE, incident.getTitle());
        values.put(IncidentTable.Cols.DETAILS, incident.getDetails());
        values.put(IncidentTable.Cols.DATE, incident.getDate().getTime());
        values.put(IncidentTable.Cols.SOLVED, incident.isSolved()? 1 : 0 );
        values.put(IncidentTable.Cols.SUSPECT, incident.getSuspect());
        values.put(IncidentTable.Cols.SUSPECTPHONE, incident.getSuspectPhone());
        return values;
    }


}

