/*
 * Copyright (c) 2018. HuanLu Robotics. Todos los derechos reservados / All rigths reserved.
 */

package com.huanlurobotics.incidentsreport.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.huanlurobotics.incidentsreport.Incident;
import com.huanlurobotics.incidentsreport.database.IncidentDbSchema.IncidentTable;

import java.util.Date;
import java.util.UUID;

public class IncidentCursorWrapper extends CursorWrapper {
    public IncidentCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Incident getIncident() {
        String uuidString = getString(getColumnIndex(IncidentTable.Cols.UUID));
        String title = getString(getColumnIndex(IncidentTable.Cols.TITLE));
        String details = getString(getColumnIndex(IncidentTable.Cols.DETAILS));
        long date = getLong(getColumnIndex(IncidentTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(IncidentTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(IncidentTable.Cols.SUSPECT));
        String suspectPhone = getString(getColumnIndex(IncidentTable.Cols.SUSPECTPHONE));

        Incident incident = new Incident(UUID.fromString(uuidString));
        incident.setTitle(title);
        incident.setDetails(details);
        incident.setDate(new Date(date)); // Date from java.util.Date although we are using DB
        incident.setSolved(isSolved!=0); // Please, note that isSolved is retrieved as integer from DB.
        incident.setSuspect(suspect);
        incident.setSuspectPhone(suspectPhone);
        return incident;
    }

}
