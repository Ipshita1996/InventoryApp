package com.android.ipshita.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.android.ipshita.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by Ipshita on 12-10-2016.
 */

public class InventoryProvider extends ContentProvider {

    public static final int ITEMS = 1;
    public static final int ITEM_ID = 2;
    public static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, ITEMS);
        mUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", ITEM_ID);
    }

    InventoryHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = mUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, null, null,
                        null, null, sortOrder);
                break;
            case ITEM_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertNewItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertNewItem(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String name = values.getAsString(InventoryEntry.COLUMN_ITEM_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Item requires a name");
        }
        Integer price = values.getAsInteger(InventoryEntry.COLUMN_ITEM_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Item requires a price");
        }
        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_ITEM_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Item requires a quantity");
        }
        Integer sale = values.getAsInteger(InventoryEntry.COLUMN_ITEM_SALE);
        if (sale == null || !InventoryEntry.isValidSale(sale)) {
            throw new IllegalArgumentException("Item requires valid sale");
        }
        long id = db.insert(InventoryEntry.TABLE_NAME, null, values);

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = mUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case ITEMS:
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case ITEM_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues
            , String selection, String[] selectionArgs) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return updateItem(uri, contentValues);

            case ITEM_ID:
                return updateItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values) {
        String selection = InventoryEntry.COLUMN_ITEM_ID + "=?";
        String[] selectionArgs = {uri.getLastPathSegment()};
        if (values.containsKey(InventoryEntry.COLUMN_ITEM_NAME)) {
            String name = values.getAsString(InventoryEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_ITEM_PRICE)) {
            Integer price = values.getAsInteger(InventoryEntry.COLUMN_ITEM_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Item requires valid price");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_ITEM_QUANTITY)) {
            Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_ITEM_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Item requires valid quantity");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_ITEM_SALE)) {
            Integer sale = values.getAsInteger(InventoryEntry.COLUMN_ITEM_SALE);
            if (sale == null || !InventoryEntry.isValidSale(sale)) {
                throw new IllegalArgumentException("Item requires valid quantity");
            }
        }

        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
