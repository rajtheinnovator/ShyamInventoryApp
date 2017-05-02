package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryapp.data.Contract.ProductEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ITEM_LOADER = 0;
    ItemCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView itemListView = (ListView) findViewById(R.id.list_view_product);
        View emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);
        mCursorAdapter = new ItemCursorAdapter(this, null);
        itemListView.setAdapter(mCursorAdapter);
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("my_tag", "id is: " + id);
                //Create a new intent to go to editor activity
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                //form  d content URI that presents the specific ITEM that was clicked on,
                //by appending d id (passed as input to this method) onto the ITEMENTRY#CONTENT_URI
                //EXAMPLE IF ITEM_ID 2 WAS clicked on URI would be "content://com.example.android.items/items/2"
                Uri currentItemUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                //set the URI on the data field of the content.
                intent.setData(currentItemUri);
                //launch the editor activity to display the data for the current item
                startActivity(intent);
            }
        });
        //kick off the loader
        getSupportLoaderManager().initLoader(ITEM_LOADER, null, this);
    }

    private void insertItem() {
        // Create a ContentValues object where column names are the keys,
        // and nokia 1100 item attributes are the values.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Nokia 1100");
        values.put(ProductEntry.COLUMN_PRICE, 858);
        values.put(ProductEntry.COLUMN_CURRENT_QUANTITY, 4);
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, 7);
        // Insert a new row for Nokia 1100 into the provider using the ContentResolver.
        // Use the {@link ItemEntry#CONTENT_URI} to indicate that we want to insert
        // into the items database table.
        // Receive the new content URI that will allow us to access Nokia 1100 data in the future.
        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }

    // Helper method to delete all the items from the data base
    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + "rows deleted from Items data base");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_main.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertItem();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // define the projection that specifies the columns from the table we care about
        // WHICH COLUMNS DO YOU WANNA  SELECT
        String projection[] = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_CURRENT_QUANTITY};

        //This loader will execute the ContentProviders query method on a background thread.
        return new CursorLoader(this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v("my_tag", "data is: "+data);
        //Update with this new cursor containing updated items data
        mCursorAdapter.changeCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // callback called when data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
