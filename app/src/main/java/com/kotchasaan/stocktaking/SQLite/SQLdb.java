package com.kotchasaan.stocktaking.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SQLdb extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "stock.db";
    public SQLdb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE MasterTAG (TagID INTEGER PRIMARY KEY," +
                "TagDoc TEXT(100)," +
                "TagCODE TEXT(100)," +
                "TotalCount INTEGER," +
                "TimerCount DATETIME DEFAULT CURRENT_TIMESTAMP)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
