package com.example.android.trackstore.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class StockContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.trackstore";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCT = "products";

    private StockContract() {
    }

    public static final class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + CONTENT_AUTHORITY + "\n" + PATH_PRODUCT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + CONTENT_AUTHORITY + "\n" + PATH_PRODUCT;

        public static final String TABLE_NAME = "products";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_IMAGE = "image";


    }

}
