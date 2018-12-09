/*
 * Copyright (c) 2018. HuanLu Robotics. Todos los derechos reservados / All rigths reserved.
 */

package com.huanlurobotics.incidentsreport.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.huanlurobotics.incidentsreport.database.IncidentDbSchema.IncidentTable;

public class IncidentBaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME="incidentBase.db";

    // SQL SENTENCES
    private static final String DATABASE_CREATE_SQL_SENTENCES =
            "create table " + IncidentTable.NAME + " (" +
                    " _id integer primary key autoincrement, " +
                    IncidentTable.Cols.UUID + ", " +
                    IncidentTable.Cols.TITLE + " text not null, " +
                    IncidentTable.Cols.DETAILS + " text, " +
                    IncidentTable.Cols.DATE + ", " +
                    IncidentTable.Cols.SOLVED + ", " +
                    IncidentTable.Cols.SUSPECT + " text, " +
                    IncidentTable.Cols.SUSPECTPHONE + " text " +
                    ")";
    private static final String DATABASE_DROP_SQL_SENTENCES =
            "drop table if exists " + IncidentTable.NAME;

    // constructor
    public IncidentBaseHelper (Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTables(db);
        createTables(db);
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SQL_SENTENCES);
    }
    private void dropTables(SQLiteDatabase db){
        db.execSQL(DATABASE_DROP_SQL_SENTENCES );
    }

}
