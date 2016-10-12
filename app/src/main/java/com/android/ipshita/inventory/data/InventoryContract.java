package com.android.ipshita.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ipshita on 12-10-2016.
 */

public final class InventoryContract {
    static final String CONTENT_AUTHORITY = "com.android.ipshita.inventory";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String PATH_INVENTORY = "inventory";

    public static final class InventoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        static final String TABLE_NAME = "items";

        public static final String COLUMN_ITEM_ID = BaseColumns._ID;
        public static final String COLUMN_ITEM_NAME = "name";
        public static final String COLUMN_ITEM_PRICE = "price";
        public static final String COLUMN_ITEM_QUANTITY = "quantity";
        public final static String COLUMN_ITEM_SALE = "sale";

        public static final int SALE_UNKNOWN = 0;
        public static final int SALE_SOLD = 1;
        public static final int SALE_AVLBL = 2;

        public static boolean isValidSale(int sale) {
            if (sale == SALE_UNKNOWN || sale == SALE_SOLD || sale == SALE_AVLBL) {
                return true;
            }
            return false;
        }
    }
}
