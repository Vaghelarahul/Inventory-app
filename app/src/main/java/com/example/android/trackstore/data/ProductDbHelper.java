package com.example.android.trackstore.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.trackstore.data.StockContract.ProductEntry;

public class ProductDbHelper extends SQLiteOpenHelper{

    private static final String LOD_TAG = ProductDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "productStore.db";

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.e(LOD_TAG, "before execution");
        String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER, "
                + ProductEntry.COLUMN_PRODUCT_IMAGE + " BLOB );";

        db.execSQL(SQL_CREATE_PRODUCT_TABLE);
        Log.e(LOD_TAG, "after execution");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
