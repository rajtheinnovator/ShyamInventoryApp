package com.example.android.inventoryapp.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventoryapp.data.Contract.ProductEntry;


public class Provider extends ContentProvider {
    private static final int ITEMS = 50;
    private static final int ITEM_ID = 51;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_ITEMS, ITEMS);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_ITEMS + "/#" , ITEM_ID);
    }

    /** Tag for the log messages */
    public static final String LOG_TAG = Provider.class.getSimpleName();
    private DBHelper mDBHelper;

    //  Initialize the provider and the database helper object.

    @Override
    public boolean onCreate() {
        mDBHelper = new DBHelper(getContext());
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        return true;
    }

    // Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mDBHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                // For the ITEMS code, query the items table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the items table.
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEM_ID:
                // For the ITEM_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.items/items/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the items table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    // Insert new data into the provider with the given ContentValues.

    @Override
    public Uri insert(Uri uri, ContentValues contentvalues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertITEM(uri, contentvalues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert an item into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertITEM(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("product requires a name");
        }
        //Check that the price is not null
        Integer price = values.getAsInteger(ProductEntry.COLUMN_PRICE);
        if(price == null) {
            throw new IllegalArgumentException("Product requires a price");
        }
        //Check that current quantity is not null
        Integer currentQuantity = values.getAsInteger(ProductEntry.COLUMN_CURRENT_QUANTITY);
        if(currentQuantity == null ){
            throw new IllegalArgumentException("Product requires valid quantity");
        }
        // Check that image is not null
        Integer image = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_IMAGE);
        if(image == null){
            throw new IllegalArgumentException("Product requires valid image");
        }

        // inserting a new item into the data base table with the given content values
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        // insert the new item with the given values
        long id = database.insert(ProductEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if(id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //notify all listeners that data has changed for the itemContent uri
        // URI  === for the whole items table
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        Log.v("my_tag", "match is : "+match);
        switch(match) {
            case ITEMS :
                return updateItem(uri, values, selection, selectionArgs);
            case ITEM_ID :
                // For the ITEM_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for" + uri);
        }
    }

    /**
     * Update items in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more items).
     * Return the number of rows that were successfully updated.
     */
    private int updateItem
    (Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // TODO: Update the selected items in the items database table with the given ContentValues

        // TODO: Return the number of rows that were affected
        // If the {@link ProductEntry#COLUMN_ITEM_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }

        // If the {@link ProductEntry#COLUMN_ITEM_PRICE} key is present,
        // check that the price value is valid.
        if (values.containsKey(ProductEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(ProductEntry.COLUMN_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Item requires valid price");
            }
        }

        // If the {@link ProductEntry#COLUMN_CURRENT_QUANTITY} key is present,
        // check that the quantity value is valid.
        if (values.containsKey(ProductEntry.COLUMN_CURRENT_QUANTITY)) {
            // Check that the QUANTITY is greater than or equal to 0
            Integer quantity = values.getAsInteger(ProductEntry.COLUMN_CURRENT_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Quantity requires valid number");
            }
        }

        // No need to check the image, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        int rowsUpdated =  database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        //if one or more rows were updated , then notify all listeners that the data at the given URI has changed.
        if(rowsUpdated != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        //return the no of rows updated
        return rowsUpdated;
    }


    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;
        // get Writable database
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch(match) {
            case ITEMS :
                // delete all rows that match the  selection and selection arguments
                rowsDeleted =  database.delete(ProductEntry.TABLE_NAME, selection,  selectionArgs);
                break;
            case ITEM_ID :
                // delete a single row given by the ID in the URI
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default :
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if(rowsDeleted !=0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;


    }

    //Returns the MIME type of data for the content URI.

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case  ITEMS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }

}
