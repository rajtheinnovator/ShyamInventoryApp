package com.example.android.inventoryapp;

//{@link ITEMCursorAdapter} is an adapter for a list or grid view
//that uses a {@link Cursor} of item data as its data source. This adapter knows
//how to create list items for each row of item data in the {@link Cursor}.

import android.content.ContentUris;
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

import com.example.android.inventoryapp.data.Contract;

public class ItemCursorAdapter extends CursorAdapter {
    private TextView mNumber;
    private int number;
    private Context mContext;
    private int rowId;

    //  Constructs a new {@link ITEM Cursor Adapter}.
    //@param context The context
    //@param c       The cursor from which to get the data.
    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0/* flags */);
        mContext=context;
    }

    // Makes a new blank list item view. No data is set (or bound) to the views yet.
    //@param context app context
    //@param cursor  The cursor from which to get the data. The cursor is already
    //moved to the correct position.
    //@param parent  The parent to which the new view is attached to
    //@return the newly created list item view.

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    // This method binds the item data (in the current row pointed to by cursor) to the given
    //list item layout. For example, the name for the current item can be set on the name TextView
    //in the list item layout.
    //@param view    Existing view, returned earlier by newView() method
    //@param context app context
    //@param cursor  The cursor from which to get the data. The cursor is already moved to the correct row.

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView mName = (TextView) view.findViewById(R.id.name);
        TextView mPrice = (TextView) view.findViewById(R.id.price);
        mNumber = (TextView) view.findViewById(R.id.number);


        // find the columns of items attribute we are interested in
        int nameColumnIndex = cursor.getColumnIndex(Contract.ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(Contract.ProductEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(Contract.ProductEntry.COLUMN_CURRENT_QUANTITY);
        int rowColumnIndex = cursor.getColumnIndex(Contract.ProductEntry._ID);
        //Read the ITEM attributes from cursor for the current ITEM
        String naam = cursor.getString(nameColumnIndex);
        int daam = cursor.getInt(priceColumnIndex);
        number = cursor.getInt(quantityColumnIndex);
        rowId = cursor.getInt(rowColumnIndex);
        mName.setText(naam);
        mPrice.setText(String.valueOf(daam));
        mNumber.setText(String.valueOf(number));
        Button sold = (Button) view.findViewById(R.id.button_bech_dala);
        sold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(number == 0)
                {
                    Toast toast = Toast.makeText(mContext, "You can't have less than 0 no's of item", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                number = number-1;
                ContentValues values = new ContentValues();
                values.put(Contract.ProductEntry.COLUMN_CURRENT_QUANTITY, number);
                Uri uribaba = ContentUris.withAppendedId(Contract.ProductEntry.CONTENT_URI, rowId);
                int rowsUpdated = mContext.getContentResolver().update(uribaba, values, null, null);
                mNumber.setText(String.valueOf(number));
            }
        });

    }
}