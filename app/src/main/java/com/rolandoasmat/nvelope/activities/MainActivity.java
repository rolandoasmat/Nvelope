package com.rolandoasmat.nvelope.activities;

import android.content.Intent;
import android.support.v4.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.widget.RecyclerView;

import android.view.SubMenu;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.rolandoasmat.nvelope.models.Category;
import com.rolandoasmat.nvelope.models.PaymentMethod;
import com.rolandoasmat.nvelope.R;
import com.rolandoasmat.nvelope.models.Payment_methodsTable;
import com.rolandoasmat.nvelope.models.Receipt;
import com.rolandoasmat.nvelope.adapters.ReceiptsAdapter;
import com.rolandoasmat.nvelope.models.CategoriesTable;
import com.rolandoasmat.nvelope.models.ReceiptsTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @BindView(R.id.drawer_layout)
    protected DrawerLayout mDrawer;

    private final int REFRESH_UI = 9482;
    private final int REFRESH_DRAWER = 4935;
    private String mFilter = "";
    private FirebaseAnalytics mFirebaseAnalytics;

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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    private void setupFab() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "New Receipt FAB");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                Intent intent = new Intent(MainActivity.this, NewReceiptActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

    }

    private void bindDrawer(Cursor cursor) {
        List<Receipt> receipts = ReceiptsTable.getRows(cursor, false);
        Set<String> set = new HashSet<>();
        for(Receipt receipt: receipts) {
            set.add(receipt.mMethodOfPayment);
        }
        SubMenu subMenu = mNavigationView.getMenu().getItem(2).getSubMenu();
        subMenu.clear();
        for(String paymentMethod: set) {
            subMenu.add(paymentMethod);
        }
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

    private void refreshUI() {
        if(mFilter.equals("")) {
            mToolbar.setTitle(R.string.app_name);
        } else {
            mToolbar.setTitle(mFilter);
        }
        getSupportLoaderManager().initLoader(REFRESH_UI, null, this);
        getSupportLoaderManager().initLoader(REFRESH_DRAWER, null, this);
    }

    private void bindPie(Cursor cursor) {
        List<Receipt> receipts = ReceiptsTable.getRows(cursor, false);
        if(receipts.size() == 0){
            mPieChart.clear();
            return;
        }
        HashMap<String, Double> map = analyzeReceipts(receipts);

        List<PieEntry> entries = new ArrayList<>();
        for(String key: map.keySet()) {
            entries.add(new PieEntry(map.get(key).floatValue(), key));
        }
        PieDataSet set = new PieDataSet(entries, "");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setValueTextSize(13.0f);
        set.setValueTextColor(R.color.colorPrimary);
        PieData data = new PieData(set);
        data.setValueFormatter(new PercentFormatter());
        mPieChart.setData(data);
        mPieChart.invalidate(); // refresh
    }

    private HashMap<String, Double> analyzeReceipts(List<Receipt>receipts) {
        HashMap<String, Double> map = new HashMap<>();
        Double totalSum = 0.0;
        for(Receipt receipt: receipts) {
            String category = receipt.mCategory;
            Double amount = new Double(receipt.mAmount);
            totalSum += amount;
            if(map.containsKey(category)){
                Double currentSum = map.get(category);
                Double newSum = currentSum + amount;
                map.remove(category);
                map.put(category, newSum);
            } else {
                map.put(category, amount);
            }
        }
        // Calculate percentages
        HashMap<String, Double> finalMap = new HashMap<>();
        for(String key: map.keySet()) {
            Double sum = map.get(key);
            Double percent = (sum / totalSum) * 100.0;
            finalMap.put(key, percent);
        }
        return finalMap;
    }

    private void setupRecyclerView() {
        mAdapter = new ReceiptsAdapter();
        mReceiptsRecyclerView.setAdapter(mAdapter);
    }

    private void bindRecyclerView(Cursor cursor) {
        List<Receipt> receipts = ReceiptsTable.getRows(cursor, false);
        mAdapter.updateData(receipts);
    }

    public void chartPressed(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Pie Chart");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        if(mPieChart.getData() != null) {
            Intent intent = new Intent(this, PieDetailActivity.class);
            intent.putExtra("filter", mFilter);
            startActivity(intent);
        }
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

        if (id == R.id.nav_home) {
            mFilter = "";
            refreshUI();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else {
            mFilter= item.getTitle().toString();
            refreshUI();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch(i) {
            case REFRESH_UI:
                if(mFilter.equals("")) {
                    return new CursorLoader(this, ReceiptsTable.CONTENT_URI, null, null, null, null);
                } else {
                    String[] filter = new String[]{mFilter};
                    return new CursorLoader(this, ReceiptsTable.CONTENT_URI, null, " method_of_payment == ? ", filter , null);
                }
            case REFRESH_DRAWER:
                return new CursorLoader(this, ReceiptsTable.CONTENT_URI, null, null, null, null);
            default:
                throw new IllegalArgumentException("ID: " + i + " not handled.");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int id = loader.getId();
        switch(id) {
            case REFRESH_UI:
                bindPie(cursor);
                bindRecyclerView(cursor);
                break;
            case REFRESH_DRAWER:
                bindDrawer(cursor);
                break;
            default:
                throw new IllegalArgumentException("ID: " + id + " not handled.");
        }
        getSupportLoaderManager().destroyLoader(id);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}