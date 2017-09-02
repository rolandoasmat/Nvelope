package com.rolandoasmat.nvelope.activities;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.rolandoasmat.nvelope.R;
import com.rolandoasmat.nvelope.models.Receipt;
import com.rolandoasmat.nvelope.models.ReceiptsTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PieDetailActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    @BindView(R.id.pie_chart)
    protected PieChart mPieChart;

    @BindView(R.id.details_linear_layout)
    protected LinearLayout mLinearLayout;

    private String mFilter;
    private final int REFRESH_UI = 5938;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_detail);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mFilter = getIntent().getStringExtra("filter");
        setupPie();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    private void refreshUI() {
        getSupportLoaderManager().initLoader(REFRESH_UI, null, this);
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

    private void createCards(Cursor cursor) {
        List<Receipt> receipts = ReceiptsTable.getRows(cursor, false);
        if(receipts.size() != 0) {
            IPieDataSet dataSet = mPieChart.getData().getDataSet();
            for(int i = 0; i <  dataSet.getEntryCount(); i++) {
                PieEntry entry = dataSet.getEntryForIndex(i);
                String method = entry.getLabel();
                int color = dataSet.getColor(i);

                // Create UI
                LayoutParams params = new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                );
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setLayoutParams(params);
                linearLayout.setBackgroundColor(color);
                float scale = getResources().getDisplayMetrics().density;
                int dps = (int) (8.0f*scale + 0.5f);
                linearLayout.setPadding(dps, dps, dps, dps);

                // Title
                TextView title = new TextView(this);
                title.setLayoutParams(params);
                title.setText(method.toUpperCase());
                title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
                title.setTextColor(Color.WHITE);
                title.setTypeface(null, Typeface.BOLD);
                linearLayout.addView(title);

                // List
                for(Receipt receipt : receipts) {
                    if(receipt.mCategory.equals(method)) {
                        TextView tv = new TextView(this);
                        tv.setLayoutParams(params);
                        tv.setText(receipt.mLocation+"\n"+"$" + receipt.mAmount + " on " + receipt.dateFormatted());
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                        tv.setTextColor(Color.WHITE);
                        linearLayout.addView(tv);
                        View margin = new View(this);
                        margin.setLayoutParams(params);
                        margin.setMinimumHeight(dps);
                        linearLayout.addView(margin);
                    }
                }
                mLinearLayout.addView(linearLayout);
                View margin = new View(this);
                margin.setLayoutParams(params);
                margin.setMinimumHeight(dps);
                mLinearLayout.addView(margin);
            }
        }
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
                createCards(cursor);
                break;
            default:
                throw new IllegalArgumentException("ID: " + id + " not handled.");
        }
        getSupportLoaderManager().destroyLoader(id);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    public void chartPressed(View view) { }
}