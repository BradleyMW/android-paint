/**
 * DBContract.java
 *
 * Written by Bradley Wersterfer for CS4301.002, Assignment 6, starting April 29, 2021.
 * NetID: bmw170030
 *
 * This is a collection of static classes describing the database schema for the FingerPaints program.
 * It contains the database name and current version (increment this number to reset the database
 * following any changes to the schema), along with a static class representing each table or entity
 * in the database. Each of these contain fields describing column names and SQL statements to
 * either destroy or create that table, making it easy to change the database structure from this
 * file alone.
 */

package com.bmw170030.fingerpaints.Database;

import android.provider.BaseColumns;

public final class DBContract {
    // Define this database's name and version number for consistent referencing
    public static final String DATABASE_NAME = "Drawings.db";
    public static final int DATABASE_VERSION = 4;

    // Instantiating the contract class should not be possible thanks to the private constructor
    private DBContract() {}

    // Table containing holistic drawing information
    public static class Drawing implements BaseColumns {

        public static final String TABLE_NAME = "Drawing";
        public static final String COLUMN_NAME_NAME = "drawingName";

        public static final int MAX_NAME_LENGTH = 100;
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + Drawing.TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        Drawing.COLUMN_NAME_NAME + " TEXT UNIQUE);";
        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + Drawing.TABLE_NAME + ";";
    }

    // Table containing line information
    public static class Line implements BaseColumns {

        public static final String TABLE_NAME = "Line";
        public static final String COLUMN_NAME_COLOR = "color";
        public static final String COLUMN_NAME_RADIUS = "radius";

        // Foreign key for the drawing that this line is a part of
        public static final String COLUMN_NAME_DRAWING = "belongsToDrawing";
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + Line.TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        Line.COLUMN_NAME_COLOR + " TEXT," +
                        Line.COLUMN_NAME_RADIUS + " TEXT," +
                        Line.COLUMN_NAME_DRAWING + " LONG, " +
                        "FOREIGN KEY (" + Line.COLUMN_NAME_DRAWING + ") REFERENCES "
                        + Drawing.TABLE_NAME + "(_ID));";
        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + Line.TABLE_NAME + ";";
    }

    // Table containing individual point information
    public static class Point implements BaseColumns {

        public static final String TABLE_NAME = "Point";
        public static final String COLUMN_NAME_COORDINATE_1 = "x";
        public static final String COLUMN_NAME_COORDINATE_2 = "y";

        // Foreign key for the line that this is a point of
        public static final String COLUMN_NAME_LINE = "belongsToLine";
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + Point.TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        Point.COLUMN_NAME_COORDINATE_1 + " FLOAT," +
                        Point.COLUMN_NAME_COORDINATE_2 + " FLOAT," +
                        Point.COLUMN_NAME_LINE + " INTEGER, " +
                        "FOREIGN KEY (" + Point.COLUMN_NAME_LINE + ") REFERENCES "
                        + Line.TABLE_NAME + "(_ID));";
        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + Point.TABLE_NAME + ";";
    }
}
