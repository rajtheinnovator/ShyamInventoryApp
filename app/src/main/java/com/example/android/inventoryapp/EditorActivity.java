package com.example.android.inventoryapp;


import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
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
import com.example.android.inventoryapp.data.Contract.ProductEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_ITEM_LOADER = 0 ;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mCurrent_QuantityEditText;
    private EditText mQuantity_OrderedEditText;
    private ImageView mItemImageView;
    private Uri mCurrentItemUri;

    /** Boolean flag that keeps track of whether the item has been edited (true) or not (false) */
    private boolean mItemHasChanged = false;
    // onTouch listener that listens for any user touches on a View, implying that they are modifying the view
    // and we change the mItemHasChanged boolean to true
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        //examine the intent that was used to launch this Activity
        //in order to figure out that whether it is to create a new item or to edit an existing one.
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();
        // if the intent does not contain an item Content Uri we know that we are gonna create a new item.
        if(mCurrentItemUri == null) {
            //its a new item so name the level field(android:lavel) Add an Item
            setTitle("Add an item");
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete an item that hasn't been created yet.)
            invalidateOptionsMenu();
        }
        else {
            // its an existing pet what we have to edit.
            setTitle("Edit an Item");
            // Initialize a loader to read the item data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.item_name);
        mPriceEditText = (EditText) findViewById(R.id.price);
        mCurrent_QuantityEditText = (EditText) findViewById(R.id.quantity_available);
        mQuantity_OrderedEditText = (EditText) findViewById(R.id.quantity_ordered);
        mItemImageView = (ImageView) findViewById(R.id.item_image);
        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mCurrent_QuantityEditText.setOnTouchListener(mTouchListener);
        mQuantity_OrderedEditText.setOnTouchListener(mTouchListener);
        mItemImageView.setOnTouchListener(mTouchListener);
    }

    // get the user input from editor and save new item into database.
    private void saveItem() {
        //read from the input fields
        //Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String curQuantity = mCurrent_QuantityEditText.getText().toString().trim();
        if (mCurrentItemUri == null && TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty(curQuantity))
        {return;}
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);

        // If the price and current quantity is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int price =0;
        int cur_Quantity = 0;
        if(!TextUtils.isEmpty(priceString) && !TextUtils.isEmpty(curQuantity)){
            price = Integer.parseInt(priceString);
            cur_Quantity = Integer.parseInt(curQuantity);
        }
        values.put(ProductEntry.COLUMN_PRICE, price);
        values.put(ProductEntry.COLUMN_CURRENT_QUANTITY, cur_Quantity);

        // determine that whether it is a new Item or existing Item by checking if mCurrentPetItem is null or not
        if (mCurrentItemUri == null) {
            // This is a NEW item, so insert a new item into the provider,
            // returning the content URI for the new item.
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            // show a toast message depending upon whether insertion was successfull or not.
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_item_failed), Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING item, so update the item with content URI: mCurrentItemUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentItemUri will already identify the correct row in the database that we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
            //show a toast message depending upon whether update was successfull
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_item_failed), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_item_successful), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**This method is called after invalidateOptionsMenu(), so that the
     menu can be updated (some menu items can be hidden or made visible).*/
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //if this a new item, hide the "delete" menu item
        if(mCurrentItemUri == null){
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save item to data base
                saveItem();
                //Exit this current Activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up  confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the item hasn't changed, continue with navigating up to parent activity
                // which is the {@link MainActivity}.
                if(!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // this method is called when the back button is pressed
    @Override
    public void onBackPressed() {
        // If the item hasn't changed, continue with handling back button press
        if(!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // user clicked discard button, close the current activity
                        finish();
                    }
                };
        //show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // define the projection that specifies the columns from the table we care about
        // WHICH COLUMNS YOU WANT TO SELECT
        // Since the editor shows all item attributes, define a projection that contains
        // all columns from the items table

        String projection[] = {ProductEntry._ID, ProductEntry.COLUMN_PRODUCT_NAME, ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_CURRENT_QUANTITY, ProductEntry.COLUMN_PRODUCT_IMAGE};
        //this loader will execute the ContentProviders query method on a background thread
        return new CursorLoader(this, mCurrentItemUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if(cursor == null || cursor.getCount() < 1){
            return ;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if(cursor.moveToFirst()) {

            // Find the columns of item attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_CURRENT_QUANTITY);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mCurrent_QuantityEditText.setText(Integer.toString(quantity));

            mNameEditText.setText(name);
            mPriceEditText.setText(String.valueOf(price));
            mCurrent_QuantityEditText.setText(String.valueOf(quantity));

            }
        }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mCurrent_QuantityEditText.setText("");
    }

    //Show a dialog that warns the user there are unsaved changes that will be lost
    // if they continue leaving the editor.
    //@param discardButtonClickListener is the click listener for what to do when
    //the user confirms they want to discard their changes
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the item.
                if(dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // create and show the Alert Dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Prompt the user to confirm that they want to delete this pet.
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog and continue editing the pet.
                if(dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Perform the deletion of the item in the database.
    private void deletePet() {
        // Only perform the delete if this is an existing item.
        if(mCurrentItemUri != null)
        {
            // Call the ContentResolver to delete the item at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentitemUri
            // content URI already identifies the item that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if(rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_item_failed), Toast.LENGTH_LONG).show();
            }
            else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),Toast.LENGTH_LONG).show();
            }
        }
        finish();
    }

}
