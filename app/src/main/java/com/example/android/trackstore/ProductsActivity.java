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
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.trackstore.data.StockContract.ProductEntry;

public class ProductsActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ProductsActivity.class.getSimpleName();
    private static final int LOADER_ID = 1;

    private ListView mListView;
    private ProductCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        mListView = (ListView) findViewById(R.id.list_view);
        mCursorAdapter = new ProductCursorAdapter(this, null);
        mListView.setAdapter(mCursorAdapter);

        View emptyView = (View) findViewById(R.id.empty_view);
        mListView.setEmptyView(emptyView);

        Button button = (Button) findViewById(R.id.add_product);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductsActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ProductsActivity.this, DetailsActivity.class);
                intent.putExtra("id", String.valueOf(id));
                Uri uriIntent = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                intent.setData(uriIntent);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
    }

    private void insertDummyProduct() {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_products);
        byte[] dummyImage = ImageUtils.getImageByte(bitmap);

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Hp wireless mouse");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, "659");
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, "25");
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, dummyImage);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.delete_all_message);

        builder.setPositiveButton(R.string.delete_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);

                if (rowsDeleted == 0) {
                    Toast.makeText(ProductsActivity.this, "Unable to removed whole record", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductsActivity.this, "Removed all products", Toast.LENGTH_SHORT).show();
                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_option_dropdown, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.insert_dummy:
                insertDummyProduct();
                break;

            case R.id.delete_all:
                showDeleteConfirmationDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY
        };

        return new CursorLoader(
                this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.changeCursor(null);
    }


}
