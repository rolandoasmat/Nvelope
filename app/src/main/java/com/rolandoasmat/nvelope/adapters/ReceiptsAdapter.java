package com.rolandoasmat.nvelope.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rolandoasmat.nvelope.R;
import com.rolandoasmat.nvelope.models.Receipt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rolandoasmat on 9/1/17.
 */

public class ReceiptsAdapter extends RecyclerView.Adapter<ReceiptEntryViewHolder> {

    private List<Receipt> mReceipts;
    public ReceiptsAdapter(List<Receipt> receipts) {
        mReceipts = receipts;
    }
    public ReceiptsAdapter() {
        mReceipts = new ArrayList<>();
    }

    public void updateData(List<Receipt> receipts){
        this.mReceipts = receipts;
        notifyDataSetChanged();
    }

    @Override
    public ReceiptEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.receipt_list_item, parent, false);
        return new ReceiptEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReceiptEntryViewHolder holder, int position) {
        Receipt receipt = mReceipts.get(position);
        holder.bind(receipt);
    }

    @Override
    public int getItemCount() {
        return mReceipts.size();
    }
}

class ReceiptEntryViewHolder extends RecyclerView.ViewHolder {
    public TextView mLocation;
    public TextView mDate;
    public TextView mAmount;

    public ReceiptEntryViewHolder(View itemView) {
        super(itemView);
        mLocation = getTextView(itemView, R.id.location_text_view);
        mDate = getTextView(itemView, R.id.date_text_view);
        mAmount = getTextView(itemView, R.id.amount_text_view);
    }

    private TextView getTextView(View view, int id) {
        return (TextView) view.findViewById(id);
    }

    public void bind(Receipt receipt) {
        mLocation.setText(receipt.mLocation);
        mDate.setText(receipt.dateFormatted());
        mAmount.setText( Double.toString(receipt.mAmount));
    }
}
