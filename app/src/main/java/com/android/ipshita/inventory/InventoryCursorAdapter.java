package com.android.ipshita.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.android.ipshita.inventory.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter{

    InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView ProductName = (TextView) view.findViewById(R.id.edit_name);
        TextView ProductPrice=(TextView)view.findViewById(R.id.edit_price);
        TextView ProductQty=(TextView)view.findViewById(R.id.edit_qty);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);

        String name=cursor.getString(nameColumnIndex);
        int price=cursor.getInt(priceColumnIndex);
        int qty=cursor.getInt(quantityColumnIndex);

        ProductName.setText(name);
        ProductPrice.setText(price+"");
        ProductQty.setText(qty+"");

    }
}
