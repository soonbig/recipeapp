package com.neurondigital.recipeapp;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Category adapter to create a list with Text and Images
 */

public class CategoryAdapter extends RecyclerView.Adapter<ViewHolder> {
    List<Category> categories;
    Context context;
    private AdapterView.OnItemClickListener onItemClickListener;

    CategoryAdapter(List<Category> categories, AdapterView.OnItemClickListener onItemClickListener, Context context) {
        this.categories = categories;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }


    /**
     * Holds the Category elements so that they don't have to be re-created each time
     */
    public class CategoryViewHolder extends ViewHolder implements View.OnClickListener {
        LinearLayout back;
        TextView name;
        AspectRatioImageView image;

        CategoryViewHolder(View itemView) {
            super(itemView);
            back = itemView.findViewById(R.id.back);
            image = (AspectRatioImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);
            back.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //passing the clicked position to the parent class
            onItemClickListener.onItemClick(null, view, getAdapterPosition(), view.getId());
        }
    }


    @Override
    public int getItemCount() {
        return categories.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_card, viewGroup, false);
        ViewHolder cvh = new CategoryViewHolder(v);
        return cvh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder recipeViewHolder, final int i) {
        //set category name
        ((CategoryViewHolder) recipeViewHolder).name.setText(categories.get(i).name);

        //load images using Picasso
        Picasso.with(context)
                .load(categories.get(i).imageUrl).placeholder(R.drawable.loading)
                //.fit().centerCrop()
                .into(((CategoryViewHolder) recipeViewHolder).image);
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}