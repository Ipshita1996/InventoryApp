package com.android.ipshita.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.android.ipshita.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by Ipshita on 12-10-2016.
 */

public class InventoryHelper extends SQLiteOpenHelper {


    private static final String DB_NAME = "inventory";
    private static final int DB_VERSION = 1;

    public InventoryHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE_ITEMS = "CREATE TABLE " +
                InventoryEntry.TABLE_NAME + "(" +
                InventoryEntry.COLUMN_ITEM_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                InventoryEntry.COLUMN_ITEM_NAME +
                " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_ITEM_PRICE +
                " INTEGER NOT NULL, " +
                InventoryEntry.COLUMN_ITEM_QUANTITY +
                " INTEGER NOT NULL DEFAULT 0, " +
                InventoryEntry.COLUMN_ITEM_SALE +
                " INTEGER NOT NULL);";
        sqLiteDatabase.execSQL(CREATE_TABLE_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
