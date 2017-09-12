package com.example.regev.talk2me;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Regev on 9/10/2017.
 */

public class Talk2MeDbHelper extends SQLiteOpenHelper {
    // The database name
    private static final String DATABASE_NAME = "talk2me.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public Talk2MeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold waitlist data
        final String SQL_CREATE_TALK2ME_TABLE = "CREATE TABLE " + Talk2MeContract.MemberEntry.TABLE_NAME + " (" +
                Talk2MeContract.MemberEntry.COLUMN_GROUP_PIN + " TEXT NOT NULL, " +
                Talk2MeContract.MemberEntry.COLUMN_GROUP_NAME + " TEXT NOT NULL, " +
                Talk2MeContract.MemberEntry.COLUMN_GROUP_PHOTO + " TEXT, " +
                Talk2MeContract.MemberEntry.COLUMN_USER_NAME + " TEXT NOT NULL, " +
                Talk2MeContract.MemberEntry.COLUMN_USER_PHOTO + " TEXT, " +
                Talk2MeContract.MemberEntry.COLUMN_USER_LOCKED + " BOOL NOT NULL" +
                //"UNIQUE (" + Talk2MeContract.MemberEntry.COLUMN_GROUP_PIN + "," + Talk2MeContract.MemberEntry.COLUMN_USER_NAME+ ")"+
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_TALK2ME_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Talk2MeContract.MemberEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
