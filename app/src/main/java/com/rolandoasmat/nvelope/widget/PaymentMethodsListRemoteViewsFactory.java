package com.rolandoasmat.nvelope.widget;

import android.content.Context;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.rolandoasmat.nvelope.R;
import com.rolandoasmat.nvelope.models.Receipt;
import com.rolandoasmat.nvelope.models.ReceiptsTable;

import java.util.List;

/**
 * Created by rolandoasmat on 9/2/17.
 */

public class PaymentMethodsListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private List<Receipt> mReceipts;
    private String mPaymentMethod;

    public PaymentMethodsListRemoteViewsFactory(Context context, String paymentMethod) {
        this.mContext = context;
        this.mPaymentMethod = paymentMethod;
    }

    @Override
    public void onCreate() { }

    @Override
    public void onDataSetChanged() {
        if(this.mContext != null) {
            Cursor cursor = mContext.getContentResolver().query(ReceiptsTable.CONTENT_URI,null,null,null,null);
            mReceipts = ReceiptsTable.getRows(cursor,false);
        }
    }

    @Override
    public void onDestroy() { }

    @Override
    public int getCount() {
        return this.mReceipts != null?this.mReceipts.size():0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if(mReceipts == null) {
            return null;
        } else {
            Receipt receipt = mReceipts.get(position);
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.receipt_list_item);
            views.setTextViewText(R.id.location_text_view, receipt.mLocation);
            views.setTextViewText(R.id.date_text_view, receipt.dateFormatted());
            views.setTextViewText(R.id.amount_text_view, Double.toString(receipt.mAmount));
            return views;
        }
    }

    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return (long)position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public final Context getMContext() {
        return this.mContext;
    }

    public final void setMContext(Context context) {
        this.mContext = context;
    }

    public final String getMRecipeTitle() {
        return this.mPaymentMethod;
    }

    public final void setMRecipeTitle(String title) {
        this.mPaymentMethod = title;
    }
}