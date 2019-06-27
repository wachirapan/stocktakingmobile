package com.kotchasaan.stocktaking.SQLite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class QueryData extends SQLdb {
    SQLiteDatabase db ;
    Cursor cursor ;
    public QueryData(Context context) {
        super(context);
    }
    public Cursor create_datastock()
    {
        db = getWritableDatabase();
        cursor = db.rawQuery("select * from MasterTAG ORDER BY TimerCount DESC",null);
        return cursor ;
    }
}
