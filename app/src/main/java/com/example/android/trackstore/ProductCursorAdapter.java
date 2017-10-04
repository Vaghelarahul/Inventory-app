package com.example.android.trackstore;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.trackstore.data.StockContract;

public class ProductCursorAdapter extends CursorAdapter {

    public int mQuantityInAdapter;

    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        TextView nameView = (TextView) view.findViewById(R.id.product_name);
        TextView priceView = (TextView) view.findViewById(R.id.price_value);
        TextView quantityView = (TextView) view.findViewById(R.id.quantity_value);

        int nameIndex = cursor.getColumnIndex(StockContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int priceIndex = cursor.getColumnIndex(StockContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityIndex = cursor.getColumnIndex(StockContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);

        String name = cursor.getString(nameIndex);
        String price = String.valueOf(cursor.getInt(priceIndex));
        mQuantityInAdapter = cursor.getInt(quantityIndex);

        String quantity;
        if (mQuantityInAdapter == 0 || mQuantityInAdapter < 0) {
            quantity = "NA";
        } else {
            quantity = String.valueOf(mQuantityInAdapter);
        }

        nameView.setText(name);
        priceView.setText(price);
        quantityView.setText(quantity);

    }
}
