package com.android.ipshita.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ipshita.inventory.data.InventoryContract;

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
        TextView textName = (TextView) view.findViewById(R.id.name);
        TextView textPrice = (TextView) view.findViewById(R.id.price);
        TextView textQuantity = (TextView) view.findViewById(R.id.quantity);
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY);
        int soldColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_SOLD);

        String currentName = cursor.getString(nameColumnIndex);
        final int currentPrice = cursor.getInt(priceColumnIndex);
        int currentQuantity = cursor.getInt(quantityColumnIndex);
        int currentSold = cursor.getInt(soldColumnIndex);

        textName.setText(currentName);

        String newPriceString = Integer.toString(currentPrice);
        textPrice.setText(newPriceString);

        String quantityStringFormatted = Integer.toString(currentQuantity);
        textQuantity.setText(quantityStringFormatted);

        final int nCurrentQuantity = currentQuantity;
        final int nsoldQuantity = currentSold;
        final Context nContext = context;

        int idColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_ID);

        final int rowId = cursor.getInt(idColumnIndex);
        Button buttonSale = (Button) view.findViewById(R.id.sale_button);
        buttonSale.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                updateUI(nCurrentQuantity, nsoldQuantity, nContext, rowId);
            }
        });

    }

    private void updateUI(int currentQuantity, int soldQuantity, Context context, int id) {
        if (currentQuantity != 0) {
            currentQuantity--;
            soldQuantity++;
            ContentValues values = new ContentValues();
            values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY, currentQuantity);
            values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_SOLD, soldQuantity);
            Uri newUri = Uri.withAppendedPath(InventoryContract.InventoryEntry.CONTENT_URI, Integer.toString(id));

            int rows = context.getContentResolver().update(newUri, values, null, null);
            if (rows == 0) {
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "No items left", Toast.LENGTH_SHORT).show();
        }

    }

    }
