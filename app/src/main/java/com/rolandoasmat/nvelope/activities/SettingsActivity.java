package com.rolandoasmat.nvelope.activities;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.rolandoasmat.nvelope.managers.GoogleAnalyticsManager;
import com.rolandoasmat.nvelope.R;
import com.rolandoasmat.nvelope.adapters.CategoriesAdapter;
import com.rolandoasmat.nvelope.models.CategoriesTable;
import com.rolandoasmat.nvelope.models.Category;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, CategoriesAdapter.CategoryClickListener {
    @BindView(R.id.categories_recycler_view)
    protected RecyclerView mCategoriesRecyclerView;
    protected CategoriesAdapter mAdapter;

    private final int FETCH_CATEGORIES = 4949;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setupRecyclerView();
        mFirebaseAnalytics = GoogleAnalyticsManager.getInstance(this);
    }

    private void setupRecyclerView() {
        mAdapter = new CategoriesAdapter(this);
        mCategoriesRecyclerView.setAdapter(mAdapter);
        mCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void refreshUI() {
        getSupportLoaderManager().initLoader(FETCH_CATEGORIES, null, this);
    }

    private void bindRecyclerView(Cursor cursor) {
        List<Category> receipts = CategoriesTable.getRows(cursor, false);
        mAdapter.updateData(receipts);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch(i) {
            case FETCH_CATEGORIES:
                return new CursorLoader(this, CategoriesTable.CONTENT_URI, null, null, null, null);
            default:
                throw new IllegalArgumentException("ID: " + i + " not handled.");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int id = loader.getId();
        switch(id) {
            case FETCH_CATEGORIES:
                bindRecyclerView(cursor);
                break;
            default:
                throw new IllegalArgumentException("ID: " + id + " not handled.");
        }
        getSupportLoaderManager().destroyLoader(id);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    @Override
    public void onClick(final String category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Delete " +category+ " category?");
        // Add the buttons
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                getContentResolver().delete(CategoriesTable.CONTENT_URI,"name == ?", new String[]{category});
                refreshUI();
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Removed a category");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "DID NOT remove a category");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}