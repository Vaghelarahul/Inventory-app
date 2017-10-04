package com.example.android.trackstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.trackstore.data.StockContract;


public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    private long id;
    private Uri mContentUri;
    private TextView mNameText;
    private TextView mPriceText;
    private TextView mQuantText;
    private Button mReduceProduct;
    private Button mAddProducts;
    private Button mOrderMore;
    private static final int MaxOrder = 50;
    private int mQuantityInt;
    private String mProductName;
    private ImageView mProductImage;

    private static final int LOADER_EXISTING_ID = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        Intent intent = getIntent();
        mContentUri = intent.getData();
        id = ContentUris.parseId(mContentUri);

        mNameText = (TextView) findViewById(R.id.detail_name);
        mPriceText = (TextView) findViewById(R.id.detail_price_value);
        mQuantText = (TextView) findViewById(R.id.detail_quantity_value);

        mProductImage = (ImageView) findViewById(R.id.product_image);

        mReduceProduct = (Button) findViewById(R.id.reduce_quatity);
        mReduceProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuantityInt != 0) {
                    mQuantityInt = mQuantityInt - 1;
                }

                ContentValues values = new ContentValues();
                values.put(StockContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, mQuantityInt);
                getContentResolver().update(mContentUri, values, null, null);
            }
        });

        mAddProducts = (Button) findViewById(R.id.increase_quantity);
        mAddProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuantityInt = mQuantityInt + 1;

                ContentValues values = new ContentValues();
                values.put(StockContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, mQuantityInt);
                getContentResolver().update(mContentUri, values, null, null);

            }
        });

        mOrderMore = (Button) findViewById(R.id.order_more);
        mOrderMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuantityInt = MaxOrder - mQuantityInt;
                String mailText = "We require this order to be delivered soon." + "\n\n"
                        + "Product Name : " + mProductName + "\n"
                        + "Quantity required : " + mQuantityInt ;

                String subject = "Order: " + mProductName;

                Intent orderIntent = new Intent(Intent.ACTION_SENDTO);
                orderIntent.setData(Uri.parse("mailto:"));
                orderIntent.putExtra(Intent.EXTRA_TEXT, mailText);
                orderIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                startActivity(orderIntent);
            }
        });

        getLoaderManager().initLoader(LOADER_EXISTING_ID, null, this);
    }


    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.delete_message);

        builder.setPositiveButton(R.string.delete_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int rowsDeleted = getContentResolver().delete(mContentUri, null, null);

                if (rowsDeleted == 0) {
                    Toast.makeText(DetailsActivity.this, "Unable to removed whole record", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailsActivity.this, "Removed this products", Toast.LENGTH_SHORT).show();
                }

                finish();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void editProduct(){

        Intent editIntent = new Intent(DetailsActivity.this, AddProductActivity.class);
        editIntent.setData(mContentUri);
        startActivity(editIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_action, menu);

        MenuItem saveItem = menu.findItem(R.id.save_product);
        saveItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delet_product:
                showDeleteConfirmationDialog();
                break;
            case R.id.edit_product:
                editProduct();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                StockContract.ProductEntry._ID,
                StockContract.ProductEntry.COLUMN_PRODUCT_NAME,
                StockContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                StockContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                StockContract.ProductEntry.COLUMN_PRODUCT_IMAGE
        };

        return new CursorLoader(
                this,
                mContentUri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {

            mProductName = cursor.getString(cursor.getColumnIndex(StockContract.ProductEntry.COLUMN_PRODUCT_NAME));
            int priceInt = cursor.getInt(cursor.getColumnIndex(StockContract.ProductEntry.COLUMN_PRODUCT_PRICE));
            String price = String.valueOf(priceInt);
            mQuantityInt = cursor.getInt(cursor.getColumnIndex(StockContract.ProductEntry.COLUMN_PRODUCT_QUANTITY));
            byte[] imageByte = cursor.getBlob(cursor.getColumnIndex(StockContract.ProductEntry.COLUMN_PRODUCT_IMAGE));
            Bitmap bitmap = ImageUtils.getImage(imageByte);

            String quantity;
            if (mQuantityInt == 0) {
                quantity = "NA";
            } else {
                quantity = String.valueOf(mQuantityInt);
            }

            mNameText.setText(mProductName);
            mPriceText.setText(price);
            mQuantText.setText(quantity);
            mProductImage.setImageBitmap(bitmap);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNameText.setText("");
        mPriceText.setText("");
        mQuantText.setText("");
        mProductImage.setImageResource(R.drawable.no_image_blank);

    }
}
