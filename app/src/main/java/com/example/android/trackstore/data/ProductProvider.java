package com.example.android.trackstore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;


public class ProductProvider extends ContentProvider {

    private static final String LOG_TAG = ProductProvider.class.getSimpleName();

    private ProductDbHelper mDbHelper;

    private static final int PRODUCT = 100;
    private static final int PRODUCT_ID = 200;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_PRODUCT, PRODUCT);
        sUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_PRODUCT + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return StockContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return StockContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown uri" + uri + "with match" + match);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                cursor = database.query(StockContract.ProductEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = StockContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(StockContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("can not query with uri " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("unable to insert for uri " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {

        String name = values.getAsString(StockContract.ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires name");
        }

        Integer price = values.getAsInteger(StockContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price < 0) {
            throw new IllegalArgumentException("Product requires its price");
        }

        Integer quantity = values.getAsInteger(StockContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity < 0) {
            throw new IllegalArgumentException("Product requires available quantity");
        }

        byte[] image = values.getAsByteArray(StockContract.ProductEntry.COLUMN_PRODUCT_IMAGE);
        if(image == null){
            throw new IllegalArgumentException("Product requires image");
        }


        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(StockContract.ProductEntry.TABLE_NAME, null, values);

        if (id == -1) {
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                rowsDeleted = database.delete(StockContract.ProductEntry.TABLE_NAME, null, null);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case PRODUCT_ID:
                selection = StockContract.ProductEntry._ID + " =?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(StockContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Unable to delete whole database");
        }

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                selection = StockContract.ProductEntry._ID + " =?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Unable to update this product");
        }
    }




    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(StockContract.ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(StockContract.ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires name");
            }
        }

        if (values.containsKey(StockContract.ProductEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(StockContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price < 0) {
                throw new IllegalArgumentException("Product requires its price");
            }
        }

        if (values.containsKey(StockContract.ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(StockContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity < 0) {
                throw new IllegalArgumentException("Product requires available quantity");
            }
        }

        if(values.containsKey(StockContract.ProductEntry.COLUMN_PRODUCT_IMAGE)){
            byte[] image = values.getAsByteArray(StockContract.ProductEntry.COLUMN_PRODUCT_IMAGE);
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(StockContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
