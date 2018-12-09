/*
 * Copyright (c) 2018. HuanLu Robotics. Todos los derechos reservados / All rigths reserved.
 */

package com.huanlurobotics.incidentsreport.database;

public class IncidentDbSchema {
    public static final class IncidentTable {
        public static final String NAME = "incidents";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DETAILS = "details";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
            public static final String SUSPECTPHONE = "suspect_phone";
        }
    }
}
