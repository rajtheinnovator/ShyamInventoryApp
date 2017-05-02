package com.example.android.inventoryapp.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.inventoryapp.data.Contract.ProductEntry;

public class DBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = DBHelper.class.getSimpleName();

// if you change the data base schema, you must increment the data  base version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "products.db";
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // This is called when data base is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create a string that contains the SQL statement to create the pets table.
        String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + "(" + ProductEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                ProductEntry.COLUMN_PRICE + " INTEGER NOT NULL, " + ProductEntry.COLUMN_CURRENT_QUANTITY + " INTEGER DEFAULT 0, " +
                ProductEntry.COLUMN_PRODUCT_IMAGE + " INTEGER);";
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1; so there is nothing to be done here
    }
}
