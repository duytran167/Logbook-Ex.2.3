package com.example.logbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.widget.Toast;
import androidx.annotation.Nullable;

class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "ImageLibrary.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "my_image";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_ImageUrl = "Image_Url";



    MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ImageUrl + " TEXT " + " );";
        db.execSQL(query);
    }


    void addBook(String ImageUrl){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ImageUrl, ImageUrl);

        long result = db.insert(TABLE_NAME,null, cv);
        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public void getFromData(){
         SQLiteDatabase db = this.getWritableDatabase();
         ContentValues value1 = new ContentValues();
        ContentValues value2 = new ContentValues();
        ContentValues value3 = new ContentValues();

        value1.put(COLUMN_ImageUrl, "https://thuthuatphanmem.vn/uploads/2018/09/11/hinh-anh-dep-6_044127357.jpg");
        value2.put(COLUMN_ImageUrl, "https://img.thuthuatphanmem.vn/uploads/2018/10/09/anh-dep-nhat-the-gioi-ve-thien-nhien_041753462.jpg");
        value3.put(COLUMN_ImageUrl, "https://img.thuthuatphanmem.vn/uploads/2018/10/09/hinh-anh-thien-nhien-bien-dao-dep-nhat_041755353.jpg");

        db.insertOrThrow(TABLE_NAME, null, value1);
        db.insertOrThrow(TABLE_NAME, null, value2);
        db.insertOrThrow(TABLE_NAME, null, value3);

     }



}