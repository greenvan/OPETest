package tk.greenvan.opetest.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import tk.greenvan.opetest.db.DBContract.AnswerEntry;

public class DBHelper extends SQLiteOpenHelper {
    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "opetest.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the user answers table
        // TODO hacer la primary key conjunto de username, testid y questionid
        String SQL_CREATE_USER_ANSWER_TABLE = "CREATE TABLE " + AnswerEntry.TABLE_NAME + " ("
                //   + AnswerEntry._ID + "	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + AnswerEntry.COLUMN_USER_NAME + "	TEXT NOT NULL DEFAULT 'anonymous', "
                + AnswerEntry.COLUMN_TEST_ID + "	TEXT NOT NULL, "
                + AnswerEntry.COLUMN_QUESTION_ID + "	INTEGER NOT NULL, "
                + AnswerEntry.COLUMN_ANSWER_STATE + "	INTEGER NOT NULL DEFAULT 0, "
                + AnswerEntry.COLUMN_LAST_ACCESS + "	INTEGER DEFAULT -1, "
                + AnswerEntry.COLUMN_SELECTION + "	TEXT DEFAULT NULL);";
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_USER_ANSWER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
