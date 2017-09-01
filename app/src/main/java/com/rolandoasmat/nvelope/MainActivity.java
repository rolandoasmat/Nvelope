package com.rolandoasmat.nvelope;

import android.content.Intent;
import android.support.v4.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.content.CursorLoader;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderCallbacks<Cursor> {
    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    @BindView(R.id.fab)
    protected FloatingActionButton mFab;

    @BindView(R.id.nav_view)
    protected NavigationView mNavigationView;

    @BindView(R.id.pie_chart)
    protected PieChart mPieChart;

    @BindView(R.id.receipts_recycler_view)
    protected RecyclerView mReceiptsRecyclerView;
    protected ReceiptsAdapter mAdapter;

    private final int FETCH_RECEIPTS = 0;
    private final int FETCH_CATEGORIES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setupFab();
        setupDrawer();
        mNavigationView.setNavigationItemSelectedListener(this);
        dbInit();
        setupPie();
        setupRecyclerView();
    }

    private void setupFab() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewReceipt.class);
                startActivity(intent);
            }
        });
    }

    private void setupDrawer(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void dbInit() {
        // Initialize DB if needed.
        // Restore preferences
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        String isDatabaseInitializedKey = "isDatabaseInitialized";
        boolean isDatabaseInitialized = settings.getBoolean(isDatabaseInitializedKey, false);
        if(!isDatabaseInitialized) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(isDatabaseInitializedKey, true);
            editor.commit();
            // Initialize categories
            getContentResolver().insert(CategoriesTable.CONTENT_URI, CategoriesTable.getContentValues(new Category("Eating Out"), false));
            getContentResolver().insert(CategoriesTable.CONTENT_URI, CategoriesTable.getContentValues(new Category("Groceries"), false));
            getContentResolver().insert(CategoriesTable.CONTENT_URI, CategoriesTable.getContentValues(new Category("Entertainment"), false));
            getContentResolver().insert(CategoriesTable.CONTENT_URI, CategoriesTable.getContentValues(new Category("Other"), false));
            // Initialize default payment methods
            getContentResolver().insert(Payment_methodsTable.CONTENT_URI, Payment_methodsTable.getContentValues(new PaymentMethod("WF Credit Card"), false));
            getContentResolver().insert(Payment_methodsTable.CONTENT_URI, Payment_methodsTable.getContentValues(new PaymentMethod("BoA Credit Card"), false));
        }
    }

    private void setupPie() {
        mPieChart.setHoleRadius(0.0f);
        mPieChart.setTransparentCircleRadius(0.0f);
        mPieChart.setDrawEntryLabels(false);
        mPieChart.setEntryLabelTextSize(30.0f);
        mPieChart.setTouchEnabled(false);
        Description description = mPieChart.getDescription();
        description.setText("");
        Legend legend = mPieChart.getLegend();
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
    }

    private void refreshPie() {
        getSupportLoaderManager().initLoader(FETCH_CATEGORIES, null, this);
    }

    private void bindPie(Cursor cursor) {
        List<Category> categories = CategoriesTable.getRows(cursor, false);
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(18.5f, "Eating Out"));
        entries.add(new PieEntry(26.7f, "Groceries"));
        entries.add(new PieEntry(24.0f, "Entertainment"));
        entries.add(new PieEntry(30.8f, "Toys"));
        PieDataSet set = new PieDataSet(entries, "");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setValueTextSize(13.0f);
        set.setValueTextColor(R.color.colorPrimary);
        PieData data = new PieData(set);
        data.setValueFormatter(new PercentFormatter());
        mPieChart.setData(data);
        mPieChart.invalidate(); // refresh
    }

    private void setupRecyclerView() {
        Cursor cursor = getContentResolver().query(ReceiptsTable.CONTENT_URI,null,null,null,null);
        List<Receipt> receipts = ReceiptsTable.getRows(cursor,false);
        mAdapter = new ReceiptsAdapter(receipts);
        mReceiptsRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshPie();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch(i) {
            case FETCH_CATEGORIES:
                return new CursorLoader(this, ReceiptsTable.CONTENT_URI, null, null, null, null);
            default:
                throw new IllegalArgumentException("no id handled!");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int id = loader.getId();
        switch(id) {
            case FETCH_CATEGORIES:
                bindPie(cursor);
                break;
            default:
                throw new IllegalArgumentException("ID: "+ id + " not handled.");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
