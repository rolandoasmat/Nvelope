package com.rolandoasmat.nvelope.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rolandoasmat.nvelope.R;
import com.rolandoasmat.nvelope.models.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rolandoasmat on 9/2/17.
 */

public class CategoriesAdapter  extends RecyclerView.Adapter<CategoriesAdapter.CategoryEntryViewHolder> {

    private List<Category> mCategories;
    private final CategoryClickListener mClickHandler;

    public interface CategoryClickListener {
        void onClick(String category);
    }

    public CategoriesAdapter(CategoryClickListener onClickListener) {
        mCategories = new ArrayList<>();
        mClickHandler = onClickListener;
    }

    public void updateData(List<Category> categories){
        this.mCategories = categories;
        notifyDataSetChanged();
    }

    @Override
    public CategoryEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_list_item, parent, false);
        return new CategoryEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryEntryViewHolder holder, int position) {
        Category category = mCategories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    class CategoryEntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mLabel;
        public ImageButton mButton;

        public CategoryEntryViewHolder(View itemView) {
            super(itemView);
            mLabel = getTextView(itemView, R.id.category_label);
            mButton = itemView.findViewById(R.id.delete_image_button);
            mButton.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String category = mCategories.get(adapterPosition).mName;
            mClickHandler.onClick(category);
        }

        private TextView getTextView(View view, int id) {
            return (TextView) view.findViewById(id);
        }

        public void bind(Category category) {
            mLabel.setText(category.mName);
        }
    }
}


