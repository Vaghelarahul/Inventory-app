package com.example.android.trackstore;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.trackstore.data.StockContract;

import java.io.IOException;
import java.io.InputStream;

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = AddProductActivity.class.getSimpleName();
    private static final int SELECT_PICTURE = 100;

    private EditText mNameEdit;
    private EditText mPriceEdit;
    private EditText mQuantityEdit;
    private Uri mEditUri;
    private ImageView mImageView;
    private Bitmap mBitmap;

    private static final int LOADER_EDIT_ID = 111;
    private boolean mProductHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product_activity);

        mNameEdit = (EditText) findViewById(R.id.edit_name);
        mPriceEdit = (EditText) findViewById(R.id.edit_price);
        mQuantityEdit = (EditText) findViewById(R.id.edit_quantity);

        mNameEdit.setOnTouchListener(mTouchListener);
        mPriceEdit.setOnTouchListener(mTouchListener);
        mQuantityEdit.setOnTouchListener(mTouchListener);

        mImageView = (ImageView) findViewById(R.id.image_edit);
        mImageView.setOnClickListener(this);

        Intent intent = getIntent();
        mEditUri = intent.getData();

        if (mEditUri != null) {
            setTitle(R.string.edit_intent);
            getLoaderManager().initLoader(LOADER_EDIT_ID, null, this);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        mBitmap = BitmapFactory.decodeStream(inputStream);
                        mImageView.setImageBitmap(mBitmap);
                    } catch (IOException i) {
                    }
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addProduct() {

        String name = mNameEdit.getText().toString().trim();
        String priceString = mPriceEdit.getText().toString().trim();
        String quantityString = mQuantityEdit.getText().toString().trim();

        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        byte[] image = ImageUtils.getImageByte(bitmap);

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, "Fill all mandatory fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }

        int price = Integer.parseInt(priceString);

        ContentValues values = new ContentValues();
        values.put(StockContract.ProductEntry.COLUMN_PRODUCT_NAME, name);
        values.put(StockContract.ProductEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(StockContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(StockContract.ProductEntry.COLUMN_PRODUCT_IMAGE, image);


        if (mEditUri != null) {
            getContentResolver().update(mEditUri, values, null, null);
            return;
        }
        getContentResolver().insert(StockContract.ProductEntry.CONTENT_URI, values);
    }


    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_message);
        builder.setPositiveButton(R.string.discard_button, discardButtonListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                finish();
            }
        };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_action, menu);

        MenuItem deleteItem = menu.findItem(R.id.delet_product);
        MenuItem editIten = menu.findItem(R.id.edit_product);
        deleteItem.setVisible(false);
        editIten.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save_product:
                addProduct();
                finish();
                return true;

            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                DialogInterface.OnClickListener discardClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        NavUtils.navigateUpFromSameTask(AddProductActivity.this);
                    }
                };

                showUnsavedChangesDialog(discardClickListener);
                return true;
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
                mEditUri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {

            String name = cursor.getString(cursor.getColumnIndex(StockContract.ProductEntry.COLUMN_PRODUCT_NAME));
            int priceInt = cursor.getInt(cursor.getColumnIndex(StockContract.ProductEntry.COLUMN_PRODUCT_PRICE));
            String price = String.valueOf(priceInt);
            int quantityInt = cursor.getInt(cursor.getColumnIndex(StockContract.ProductEntry.COLUMN_PRODUCT_QUANTITY));
            byte[] imageByte = cursor.getBlob(cursor.getColumnIndex(StockContract.ProductEntry.COLUMN_PRODUCT_IMAGE));

            String quantity = String.valueOf(quantityInt);
            Bitmap bitmap = ImageUtils.getImage(imageByte);

            mNameEdit.setText(name);
            mPriceEdit.setText(price);
            mQuantityEdit.setText(quantity);
            mImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNameEdit.setText("");
        mPriceEdit.setText("");
        mQuantityEdit.setText("");
        mImageView.setImageResource(R.drawable.no_image_blank);

    }
}
