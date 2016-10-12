package com.android.ipshita.inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ipshita.inventory.data.InventoryContract;
import com.android.ipshita.inventory.data.InventoryHelper;

import java.io.FileDescriptor;
import java.io.IOException;

public class DescriptionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int INVENTORY_LOADER = 1;
    InventoryHelper mDbHelper;
    private Uri currentUri;
    String currentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        Intent intent = getIntent();
        currentUri = intent.getData();
        mDbHelper = new InventoryHelper(this);
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);

        Button orderButton = (Button) findViewById(R.id.order_button);
        final EditText newOrderQuantity = (EditText) findViewById(R.id.order_amount_text);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newOrderQuantity.getText().toString().trim().length() != 0) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_SUBJECT,
                            "Order Information");
                    intent.putExtra(Intent.EXTRA_TEXT, newOrderQuantity.getText().toString());
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Order field cannot be blank", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    public void buttonOnClick(View view) {
        TextView textQuantity = (TextView) findViewById(R.id.text_info_quantity);
        String quantityString = textQuantity.getText().toString();
        int quantityInt = Integer.parseInt(quantityString);
        int id = view.getId();
        switch (id) {
            case R.id.button_quantity_plus:
                quantityInt++;
                break;
            case R.id.button_quantity_minus:
                if (quantityInt != 0) {
                    quantityInt--;
                } else
                    Toast.makeText(this, "Quantity can't be negative", Toast.LENGTH_SHORT).show();
                break;
        }

        textQuantity.setText(Integer.toString(quantityInt));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_description, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                updateDetails();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {InventoryContract.InventoryEntry.COLUMN_ITEM_ID,
                InventoryContract.InventoryEntry.COLUMN_ITEM_NAME,
                InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_ITEM_SOLD,
                InventoryContract.InventoryEntry.COLUMN_ITEM_PIC};
        return new CursorLoader(this, currentUri,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (!cursor.isClosed()) {
            TextView textName = (TextView) findViewById(R.id.text_info_name);
            EditText editTextPrice = (EditText) findViewById(R.id.text_info_price);
            TextView textQuantity = (TextView) findViewById(R.id.text_info_quantity);
            TextView textSold = (TextView) findViewById(R.id.text_info_sold);
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY);
            int imageColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_PIC);
            int soldColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_SOLD);
            cursor.moveToNext();
            currentName = cursor.getString(nameColumnIndex);
            int currentPrice = cursor.getInt(priceColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            int currentSold = cursor.getInt(soldColumnIndex);

            String imageUriString = cursor.getString(imageColumnIndex);
            Uri imageUri = Uri.parse(imageUriString);
            Bitmap bitmap = getBitmapFromUri(imageUri);
            ImageView imageView = (ImageView) findViewById(R.id.image_info_view);
            imageView.setImageBitmap(bitmap);
            textName.setText(currentName);
            textSold.setText(String.valueOf(currentSold));
            editTextPrice.setText(String.valueOf(currentPrice));
            textQuantity.setText(String.valueOf(currentQuantity));
            cursor.close();
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = null;
            if (parcelFileDescriptor != null) {
                fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            }
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            if (parcelFileDescriptor != null) {
                parcelFileDescriptor.close();
            }
            return image;
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)  {
        loader.reset();
    }

    void updateDetails(){
            EditText editTextPrice = (EditText) findViewById(R.id.text_info_price);
            TextView textQuantity = (TextView) findViewById(R.id.text_info_quantity);
            EditText editTextSale = (EditText) findViewById(R.id.edit_text_sale);
            EditText editTextShipment = (EditText) findViewById(R.id.edit_text_shipment);
            TextView textSold = (TextView) findViewById(R.id.text_info_sold);
            int currentQuantity = Integer.parseInt(textQuantity.getText().toString());
            int newPrice = 0;
            if (editTextPrice.getText().toString().trim().length() != 0) {
                newPrice = Integer.parseInt(editTextPrice.getText().toString().trim());
            }
            int newSale = 0;
            if (editTextSale.getText().toString().trim().length() != 0) {
                newSale = Integer.parseInt(editTextSale.getText().toString().trim());
            }
            int newShippment = 0;
            if (editTextShipment.getText().toString().length() != 0) {
                newShippment = Integer.parseInt(editTextShipment.getText().toString().trim());
            }

            int totalSold = Integer.parseInt(textSold.getText().toString().trim());
            int updatedSold = totalSold + newSale;
            int newQuantity = currentQuantity - newSale + newShippment;
            if (newQuantity >= 0) {
                if (editTextPrice.getText().toString().trim().length() == 0) {
                    Toast.makeText(this, "Price field can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE, newPrice);
                    values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY, newQuantity);
                    values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_SOLD, updatedSold);

                    int result = getContentResolver().update(currentUri, values, null, null);

                    if (result == 0) {
                        Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.details_quantity_negative),
                        Toast.LENGTH_SHORT).show();
            }

        }
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        int result = getContentResolver().delete(currentUri, null, null);
        if (result == 0) {
            Toast.makeText(this, "Delete Unsuccessful",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Entry Deleted",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
