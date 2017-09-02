package com.rolandoasmat.nvelope;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.CardView;

import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

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
    protected LinearLayout mConstraintLayout;

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
        if(receipts.size() != 0){
            HashMap<String, Double> map = analyzeReceipts(receipts);
            IPieDataSet dataSet = mPieChart.getData().getDataSet();
            for(int i = 0; i <  dataSet.getEntryCount(); i++) {
                PieEntry entry = dataSet.getEntryForIndex(i);
                String method = entry.getLabel();
                int color = dataSet.getColor(i);

                CardView card = new CardView(this);

                // Set the CardView layoutParams
                LayoutParams params = new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                );
                card.setLayoutParams(params);

                // Set CardView corner radius
                card.setRadius(9);

                // Set cardView content padding
                card.setContentPadding(15, 15, 15, 15);

                // Set a background color for CardView
//                card.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));

                // Set the CardView maximum elevation
                card.setMaxCardElevation(15);

                // Set CardView elevation
                card.setCardElevation(9);

                // Initialize a new TextView to put in CardView
                TextView tv = new TextView(this);
                tv.setLayoutParams(params);
                tv.setText(method);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                tv.setTextColor(Color.RED);

                TextView tv1 = new TextView(this);
                tv.setLayoutParams(params);
                tv.setText(method);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                tv.setTextColor(Color.RED);

                TextView tv2 = new TextView(this);
                tv.setLayoutParams(params);
                tv.setText(method);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                tv.setTextColor(Color.RED);

                TextView tv3 = new TextView(this);
                tv.setLayoutParams(params);
                tv.setText(method + "\n\n" + "test");
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                tv.setTextColor(color);

                // Put the TextView in CardView
                card.addView(tv);
                card.addView(tv1);
                card.addView(tv2);
                card.addView(tv3);


                // Finally, add the CardView in root layout
                mConstraintLayout.addView(card);
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
}
