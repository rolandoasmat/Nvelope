package com.rolandoasmat.nvelope.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rolandoasmat.nvelope.R;
import com.rolandoasmat.nvelope.models.PaymentMethod;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by rolandoasmat on 9/2/17.
 */

public class PaymentMethodsAdapter  extends RecyclerView.Adapter<PaymentMethodsAdapter.PaymentMethodEntryViewHolder> {

    private List<PaymentMethod> mPaymentMethods;
    private final PaymentMethodClickListener mClickHandler;

    public interface PaymentMethodClickListener {
        void onClick(String category);
    }

    public PaymentMethodsAdapter(PaymentMethodClickListener onClickListener) {
        mPaymentMethods = new ArrayList<>();
        mClickHandler = onClickListener;
    }

    public void updateData(List<PaymentMethod> methods){
        this.mPaymentMethods = methods;
        notifyDataSetChanged();
    }

    @Override
    public PaymentMethodEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payment_method_list_item, parent, false);
        return new PaymentMethodEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PaymentMethodEntryViewHolder holder, int position) {
        PaymentMethod method = mPaymentMethods.get(position);
        holder.bind(method);
    }

    @Override
    public int getItemCount() {
        return mPaymentMethods.size();
    }

    class PaymentMethodEntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mLabel;

        public PaymentMethodEntryViewHolder(View itemView) {
            super(itemView);
            mLabel = getTextView(itemView, R.id.payment_method_text_view);
            mLabel.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String category = mPaymentMethods.get(adapterPosition).mName;
            mClickHandler.onClick(category);
        }

        private TextView getTextView(View view, int id) {
            return (TextView) view.findViewById(id);
        }

        public void bind(PaymentMethod category) {
            mLabel.setText(category.mName);
        }
    }
}


