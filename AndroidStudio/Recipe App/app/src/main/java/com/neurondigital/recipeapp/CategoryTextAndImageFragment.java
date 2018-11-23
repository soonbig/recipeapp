package com.neurondigital.recipeapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * Created by melvin on 08/09/2016.
 * A fragment which displays a list of all the categories in text and image format.
 */
public class CategoryTextAndImageFragment extends Fragment {
    Context context;
    private EmptyRecyclerView mRecyclerView;
    private EmptyRecyclerView.Adapter mAdapter;
    private EmptyRecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout swipeLayout;
    List<Category> categories;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = (EmptyRecyclerView) rootView.findViewById(R.id.recipesList);
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeToRefresh);
        RelativeLayout empty = (RelativeLayout) rootView.findViewById(R.id.empty);
        mRecyclerView.setEmptyView(empty);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();

        //set title
        // getActivity().setTitle(getString(R.string.categories_page_title));

        //set RecycleView
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Swipe to Refresh
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        refresh();
    }


    /**
     * Reloads categories from server and shows them on screen
     */
    public void refresh() {
        Category.loadCategories(context, "", new Category.onCategoriesDownloadedListener() {
            @Override
            public void onCategoriesDownloaded(List<Category> categories) {
                swipeLayout.setRefreshing(false);
                setCategories(categories);
            }
        });
    }


    /**
     * Shows loaded categories on screen
     *
     * @param items
     */
    public void setCategories(List<Category> items) {
        this.categories = items;
        mAdapter = new CategoryAdapter(categories, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, CategoryRecipesActivity.class);
                intent.putExtra("Category_id", categories.get(i).id);
                startActivity(intent);
            }
        }, context);
        mRecyclerView.swapAdapter(mAdapter, false);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

    }
}
