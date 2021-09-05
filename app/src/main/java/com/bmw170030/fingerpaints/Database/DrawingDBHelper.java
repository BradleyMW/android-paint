/**
 * DrawingDBHelper.java
 *
 * Written by Bradley Wersterfer for CS4301.002, Assignment 6, starting April 29, 2021.
 * NetID: bmw170030
 *
 * This is a SQLiteOpenHelper implementation for the LineStack class of drawings to be stored in
 * the DBContract's defined schema. It manages creating the database initially when it does not exist
 * and upgrading the database when the version number is outdated.
 */

package com.bmw170030.fingerpaints.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DrawingDBHelper extends SQLiteOpenHelper {

    // Call the SQLite helper's initialization according to the contract's parameters.
    // Leave the factory null to use the default option.
    public DrawingDBHelper(Context context) {
        super(context, DBContract.DATABASE_NAME, null, DBContract.DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Invoke the contract's create methods to initialize the database
        createDatabase(db);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Since the data is relatively ephemeral, if the database version has increased, then go
        // ahead and clear out all of the old database and recreate it.
        clearDatabase(db);
        createDatabase(db);
    }

    /**
     * Clear out the entire database. Only use when all data should be deleted.
     * @param db The database to clear.
     */
    public void clearDatabase(SQLiteDatabase db) {
        db.execSQL(DBContract.Point.SQL_DROP_TABLE);
        db.execSQL(DBContract.Line.SQL_DROP_TABLE);
        db.execSQL(DBContract.Drawing.SQL_DROP_TABLE);
    }

    /**
     * Utility function to create each table in the database.
     * @param db The database to add these tables into.
     */
    public void createDatabase(SQLiteDatabase db) {
        db.execSQL(DBContract.Drawing.SQL_CREATE_TABLE);
        db.execSQL(DBContract.Line.SQL_CREATE_TABLE);
        db.execSQL(DBContract.Point.SQL_CREATE_TABLE);
    }
}
