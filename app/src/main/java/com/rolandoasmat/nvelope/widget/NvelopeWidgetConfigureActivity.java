package com.rolandoasmat.nvelope.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.rolandoasmat.nvelope.R;
import com.rolandoasmat.nvelope.adapters.PaymentMethodsAdapter;
import com.rolandoasmat.nvelope.models.PaymentMethod;
import com.rolandoasmat.nvelope.models.Payment_methodsTable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NvelopeWidgetConfigureActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, PaymentMethodsAdapter.PaymentMethodClickListener {

    @BindView(R.id.payment_methods_recycler_view)
    protected RecyclerView mPaymentMethodRecyclerView;
    protected PaymentMethodsAdapter mAdapter;
    private static final String PREFS_NAME = "com.rolandoasmat.nvelope.widget.NvelopeWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private final int FETCH_PAYMENT_METHODS = 2593;

    public NvelopeWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return "";
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);
        setContentView(R.layout.nvelope_widget_configure);
        ButterKnife.bind(this);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        setupRecyclerView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    private void refreshUI() {
        getSupportLoaderManager().initLoader(FETCH_PAYMENT_METHODS, null, this);
    }

    private void setupRecyclerView() {
        mAdapter = new PaymentMethodsAdapter(this);
        mPaymentMethodRecyclerView.setAdapter(mAdapter);
        mPaymentMethodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void bindRecyclerView(Cursor cursor) {
        List<PaymentMethod> receipts = Payment_methodsTable.getRows(cursor, false);
        mAdapter.updateData(receipts);
    }

    @Override
    public void onClick(String category) {
        final Context context = NvelopeWidgetConfigureActivity.this;

        // When an option is clicked, store the string locally
        saveTitlePref(context, mAppWidgetId, category);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        NvelopeWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch(i) {
            case FETCH_PAYMENT_METHODS:
                return new CursorLoader(this, Payment_methodsTable.CONTENT_URI, null, null, null, null);
            default:
                throw new IllegalArgumentException("ID: " + i + " not handled.");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int id = loader.getId();
        switch(id) {
            case FETCH_PAYMENT_METHODS:
                bindRecyclerView(cursor);
                break;
            default:
                throw new IllegalArgumentException("ID: " + id + " not handled.");
        }
        getSupportLoaderManager().destroyLoader(id);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}

