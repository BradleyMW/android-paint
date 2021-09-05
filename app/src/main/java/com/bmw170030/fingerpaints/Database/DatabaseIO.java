/**
 * DatabaseIO.java
 *
 * Written by Bradley Wersterfer for CS4301.002, Assignment 6, starting April 29, 2021.
 * NetID: bmw170030
 *
 * This class provides access to the underlying database structure and should be passed the
 * SQLiteDatabase object upon creation. At that point, the application will entirely interface with
 * the database through this class, since it provides methods for getting the names of all drawings,
 * executing a query to return all information for a single drawing, and a way to either override or
 * insert a new drawing definition into the tables.
 */

package com.bmw170030.fingerpaints.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bmw170030.fingerpaints.DataTypes.CoordinatePair;
import com.bmw170030.fingerpaints.DataTypes.Line;
import com.bmw170030.fingerpaints.DataTypes.LineStack;

import java.util.ArrayList;

public class DatabaseIO {
    private SQLiteDatabase db;

    /**
     * Public constructor.
     * @param db Either a readable or a writable database to maintain the interface to.
     */
    public DatabaseIO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * Utility function to get all of the drawing names in the database.
     * @return An ArrayList of drawing names.
     */
    public ArrayList<String> getNames() {
        ArrayList<String> names = new ArrayList<>();

        // Construct the query in a safe way
        String[] result_cols = { DBContract.Drawing.COLUMN_NAME_NAME };
        String where = null;
        String[] whereArgs = null;
        String groupBy = null;
        String having = null;
        String order = null;

        // Actually execute the query
        Cursor cursor = db.query(DBContract.Drawing.TABLE_NAME, result_cols, where, whereArgs,
                groupBy, having, order);
        while(cursor.moveToNext()) {
            // Retrieve the name using its contracted column name index
            names.add(cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Drawing.COLUMN_NAME_NAME)));
        }
        cursor.close();
        return names;
    }

    /**
     * Given a unique drawing name, get the unique index and all associated line attributes for it.
     * For each of those lines, get all associated points. Use these parameters to construct a drawing
     * to return to the invoker.
     *
     * @param name String name of the drawing to collect.
     * @return A LineStack object associated to that unique name.
     */
    public LineStack getDrawing(String name) {
        LineStack drawing = new LineStack();

        // Construct the query for the drawing UID in a safe way
        String[] result_cols = { DBContract.Drawing._ID };
        String where = DBContract.Drawing.COLUMN_NAME_NAME + "=?";
        String[] whereArgs = { name };
        String groupBy = null;
        String having = null;
        String order = null;

        // Retrieve the PK for the given drawing name
        Cursor cs_drawing = db.query(DBContract.Drawing.TABLE_NAME, result_cols, where, whereArgs,
                groupBy, having, order);
        while(cs_drawing.moveToNext()) {
            long drawing_id = cs_drawing.getLong(cs_drawing.getColumnIndexOrThrow(DBContract.Drawing._ID));

            // Construct the query for lines in a safe way
            String[] result_lines = { DBContract.Line._ID, DBContract.Line.COLUMN_NAME_COLOR,
                    DBContract.Line.COLUMN_NAME_RADIUS };
            String where_line = DBContract.Line.COLUMN_NAME_DRAWING + "=?";
            String[] whereArgs_line = { String.valueOf(drawing_id) };

            // Fetch each line associated with this drawing UID
            Cursor cs_line = db.query(DBContract.Line.TABLE_NAME, result_lines, where_line, whereArgs_line,
                    groupBy, having, order);
            while(cs_line.moveToNext()) {

                // Reconstruct the metadata for this line
                long line_id = cs_line.getLong(cs_line.getColumnIndexOrThrow(DBContract.Line._ID));
                float radius = cs_line.getFloat(cs_line.getColumnIndexOrThrow(DBContract.Line.COLUMN_NAME_RADIUS));
                int color = cs_line.getInt(cs_line.getColumnIndexOrThrow(DBContract.Line.COLUMN_NAME_COLOR));
                Line new_line = new Line(radius, color);

                // Construct the query for points in a safe way
                String[] result_points = { DBContract.Point.COLUMN_NAME_COORDINATE_1,
                        DBContract.Point.COLUMN_NAME_COORDINATE_2 };
                String where_points = DBContract.Point.COLUMN_NAME_LINE + "=?";
                String[] whereArgs_points = { String.valueOf(line_id) };

                Cursor cs_point = db.query(DBContract.Point.TABLE_NAME, result_points, where_points,
                        whereArgs_points, groupBy, having, order);
                while(cs_point.moveToNext()) {

                    // Reconstruct this point and insert it into this line
                    float x = cs_point.getFloat(cs_point.getColumnIndexOrThrow(DBContract.Point.COLUMN_NAME_COORDINATE_1));
                    float y = cs_point.getFloat(cs_point.getColumnIndexOrThrow(DBContract.Point.COLUMN_NAME_COORDINATE_2));
                    new_line.addPt(new CoordinatePair(x, y));
                }

                // Now that the line has its metadata and all points, add it to the drawing
                drawing.add(new_line);
            }
        }
        return drawing;
    }

    /**
     * Add an entire drawing into the database. This must be propagated throughout the tables such
     * that the DRAWING table receives the name and a unique integer index, the LINE table receives
     * each line from this drawing tied to that drawing index, and each POINT in each line is added
     * noting which line they belong to.
     *
     * @param drawing The entire LineStack object representing a drawing to parse into the database.
     * @param name The name of the drawing to store it under.
     * @return Whether this insertion was successful or not.
     */
    public boolean addDrawing(LineStack drawing, String name) {
        ContentValues cv = new ContentValues();
        cv.put(DBContract.Drawing.COLUMN_NAME_NAME, name);

        // Actually insert the data and get the resulting primary key
        long pk = db.replaceOrThrow(DBContract.Drawing.TABLE_NAME, null, cv);
        for(Line l : drawing.getLines()) {
            addLine(l, pk);
        }
        return true;
    }

    /**
     * Add a given line to the line table in the database. First, the metadata about this line
     * must be stored, and then each point associated with it must also be stored in the Point table.
     *
     * @param line The Line object to store. Tracks details about the used paint.
     * @param uid A unique identifier for this line's associated drawing ID.
     * @return Whether this insertion was successful or not.
     */
    public boolean addLine(Line line, long uid) {
        ContentValues cv = new ContentValues();
        cv.put(DBContract.Line.COLUMN_NAME_COLOR, line.getColor());
        cv.put(DBContract.Line.COLUMN_NAME_RADIUS, line.getRadius());
        cv.put(DBContract.Line.COLUMN_NAME_DRAWING, uid);               // FK to drawing

        // Actually insert the data and get the resulting primary key
        long pk = db.replaceOrThrow(DBContract.Line.TABLE_NAME, null, cv);
        for(CoordinatePair pt : line.getPts()) {
            addCoordinate(pt, pk);
        }
        return true;
    }

    /**
     * Add a given point to the Point table in this database.
     *
     * @param pt CoordinatePair object to store with an (X, Y) representation.
     * @param uid A unique identifier for this point's associated line ID.
     * @return Whether this insertion was successful or not.
     */
    public boolean addCoordinate(CoordinatePair pt, long uid) {
        ContentValues cv = new ContentValues();
        cv.put(DBContract.Point.COLUMN_NAME_COORDINATE_1, pt.getX());
        cv.put(DBContract.Point.COLUMN_NAME_COORDINATE_2, pt.getY());
        cv.put(DBContract.Point.COLUMN_NAME_LINE, uid);                 // FK to line

        // Actually insert the data. No need to track primary key this time.
        db.replaceOrThrow(DBContract.Point.TABLE_NAME, null, cv);
        return true;
    }
}
