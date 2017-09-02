package com.rolandoasmat.nvelope.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.rolandoasmat.nvelope.models.CategoriesTable;
import com.rolandoasmat.nvelope.R;
import com.rolandoasmat.nvelope.models.Receipt;
import com.rolandoasmat.nvelope.models.ReceiptsTable;
import com.rolandoasmat.nvelope.models.Category;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewReceiptActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.location_edit_text)
    protected EditText mLocation;
    @BindView(R.id.category_spinner)
    protected Spinner mCategory;
    @BindView(R.id.payment_method_edit_text)
    protected EditText mMethodOfPayment;
    protected Date mDate;
    @BindView(R.id.date_text_view)
    protected TextView mDateTextView;
    @BindView(R.id.amount_edit_text)
    protected EditText mAmount;
    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    private final int REFRESH_CATEGORIES_SPINNER = 3948;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_receipt);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mDate = Calendar.getInstance().getTime();
        String formattedDate = Receipt.formatDate(mDate);
        mDateTextView.setText(formattedDate);
        setupCategoriesSpinner();
    }

    private void setupCategoriesSpinner() {
        getSupportLoaderManager().initLoader(REFRESH_CATEGORIES_SPINNER, null, this);
    }

    private void bindCategoriesSpinner(Cursor cursor) {
        List<Category> categoriesList = CategoriesTable.getRows(cursor, false);
        ArrayList<String> categoriesArray = new ArrayList<>();
        for(Category category: categoriesList) {
            if(!categoriesArray.contains(category.mName)) {
                categoriesArray.add(category.mName);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(adapter);
    }

    public void onSave(View view) {
        String location = mLocation.getText().toString();
        if(location.equals("")) {
            showSnackBar(R.string.empty_location_field);
            return;
        }

        String category = mCategory.getSelectedItem().toString();
        if(category.equals("")) {
            showSnackBar(R.string.empty_category_field);
            return;
        }

        String methodOfPayment = mMethodOfPayment.getText().toString();
        if(methodOfPayment.equals("")) {
            showSnackBar(R.string.empty_method_of_payment_field);
            return;
        }


        String amountText = mAmount.getText().toString();
        if(amountText.equals("")) {
            showSnackBar(R.string.empty_amount_field);
            return;
        }
        double amount = Double.parseDouble(amountText);

        Receipt receipt = new Receipt(location, category, methodOfPayment, mDate, amount);
        getContentResolver().insert(ReceiptsTable.CONTENT_URI, ReceiptsTable.getContentValues(receipt, false));
        onBackPressed();
    }

    private void showSnackBar(int id){
        Snackbar.make(findViewById(R.id.coordinator_layout), id, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch(i) {
            case REFRESH_CATEGORIES_SPINNER:
                return new CursorLoader(this, CategoriesTable.CONTENT_URI, null, null, null, null);
            default:
                throw new IllegalArgumentException("no id handled!");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int id = loader.getId();
        switch(id) {
            case REFRESH_CATEGORIES_SPINNER:
                bindCategoriesSpinner(cursor);
                break;
            default:
                throw new IllegalArgumentException("ID: "+ id + " not handled.");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}