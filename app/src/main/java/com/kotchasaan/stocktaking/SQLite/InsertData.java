package com.kotchasaan.stocktaking.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InsertData extends SQLdb {
    SQLiteDatabase db;
    Integer total ;
    public InsertData(Context context) {
        super(context);
    }

    public void InsertCountProdudct(String CodeTag, String IDTag) {
        db = this.getWritableDatabase();
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Cursor cursor = db.rawQuery("select * from MasterTAG where TagCODE = "+ CodeTag,null);
//        Log.d("TEXT", String.valueOf(cursor.getCount()));
        if(cursor.getCount() == 0){
            Log.d("Insert","---------------------------------");
            ContentValues contentValues = new ContentValues();
            contentValues.put("TagDoc",IDTag);
            contentValues.put("TagCODE",CodeTag);
            contentValues.put("TotalCount", 1);
            contentValues.put("TimerCount",formatter.format(date));
            db.insert("MasterTAG",null,contentValues);


        }else{
            Log.d("Update","+++++++++++++++++++++++++++++++");
            while (cursor.moveToNext()){
             total = cursor.getInt(3);
            }
            Integer change_number = total + 1 ;

            ContentValues contentValues = new ContentValues();
            contentValues.put("TotalCount",change_number);
            contentValues.put("TimerCount",formatter.format(date));
            db.update("MasterTAG",contentValues," TagCODE = ?", new String[] {String.valueOf(CodeTag)});
        }

    }

}
