package com.rolandoasmat.nvelope;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewReceipt extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_receipt);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDate = Calendar.getInstance().getTime();
        String formattedDate = DateFormat.getDateInstance(DateFormat.LONG).format(mDate);
        mDateTextView.setText(formattedDate);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(adapter);
    }

    public void onSave(View view) {
        String location = mLocation.getText().toString();
        String category = mCategory.getSelectedItem().toString();
        String methodOfPayment = mMethodOfPayment.getText().toString();
        double amount = Double.parseDouble(mAmount.getText().toString());

        Receipt receipt = new Receipt(location, category, methodOfPayment, mDate, amount);
        getContentResolver().insert(ReceiptsTable.CONTENT_URI, ReceiptsTable.getContentValues(receipt, false));
        onBackPressed();
    }
}