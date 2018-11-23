package com.neurondigital.recipeapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

/**
 * Created by melvin on 08/09/2016.
 */
public class CategoryRecipesActivity extends AppCompatActivity {
    Context context;
    AppCompatActivity activity;
    private EmptyRecyclerView mRecyclerView;
    private EmptyRecyclerView.Adapter mAdapter;
    private EmptyRecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout swipeLayout;
    int categoryId = 0;

    public final static int LIST_INITIAL_LOAD = 10;
    public final static int LIST_INITIAL_LOAD_MORE_ONSCROLL = 5;

    List<Recipe> recipes;
    EndlessRecyclerViewScrollListener scrollListener;
    String searchText = "";

    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_category);

        context = this;
        activity=this;

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go back
                onBackPressed();

            }
        });

        mRecyclerView = findViewById(R.id.recipesList);
        swipeLayout = findViewById(R.id.swipeToRefresh);
        RelativeLayout empty = findViewById(R.id.empty);
        mRecyclerView.setEmptyView(empty);



        //set RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manage
        int spanCount = 1;
        if (Configurations.LIST_MENU_TYPE == Configurations.LIST_FULLWIDTH) {
            mLayoutManager = new LinearLayoutManager(context);

        } else if (Configurations.LIST_MENU_TYPE == Configurations.LIST_2COLUMNS) {
            mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            spanCount = ((StaggeredGridLayoutManager) mLayoutManager).getSpanCount();
        }
        mRecyclerView.setLayoutManager(mLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager, spanCount) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                System.out.println("load more" + totalItemsCount);
                loadMore(totalItemsCount);
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);


        //get category Id from intent
        if (getIntent().hasExtra("Category_id")) {
            categoryId = getIntent().getIntExtra("Category_id", 0);
        }

        //load category from server
        Category.getCategoryName(context, categoryId, new Category.onNameFoundListener() {
            @Override
            public void onNameFound(String name) {
                getSupportActionBar().setTitle(name);
            }
        });

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
     * Refresh recipe list from server
     */
    public void refresh() {
        Recipe.loadRecipes(this, 0, LIST_INITIAL_LOAD, searchText, "" + categoryId, new Recipe.onRecipesDownloadedListener() {
            @Override
            public void onRecipesDownloaded(List<Recipe> recipes) {
                swipeLayout.setRefreshing(false);
                setRecipes(recipes);
            }
        });
    }

    /**
     * Load more recipes from server
     *
     * @param first - start loading from this recipe
     */
    public void loadMore(int first) {
        Recipe.loadRecipes(this, first, LIST_INITIAL_LOAD_MORE_ONSCROLL, searchText, "" + categoryId, new Recipe.onRecipesDownloadedListener() {
            @Override
            public void onRecipesDownloaded(List<Recipe> recipes) {
                swipeLayout.setRefreshing(false);
                ((RecipeAdapter) mAdapter).addItems(recipes);
                mRecyclerView.swapAdapter(mAdapter, false);

            }
        });
    }


    /**
     * Show recipes to screen
     *
     * @param recipes_loaded - list of recipes to show
     */
    public void setRecipes(final List<Recipe> recipes_loaded) {
        this.recipes = recipes_loaded;
        mAdapter = new RecipeAdapter(recipes, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //open recipe in new activity on click
                Intent intent = new Intent(context, SingleRecipeActivity.class);
                intent.putExtra("RECIPE_ID", recipes.get(i).id);
                startActivity(intent);
            }
        }, context);
        //mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.swapAdapter(mAdapter, false);
        scrollListener.resetState();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        //clear options menu
        menu.clear();

        //re-initialise menu
        inflater.inflate(R.menu.options_menu, menu);

        //set search icon using FontAwesome
        menu.findItem(R.id.search).setIcon(
                new IconicsDrawable(this)
                        .icon(FontAwesome.Icon.faw_search)
                        .color(ContextCompat.getColor(context, R.color.md_black_1000))
                        .sizeDp(18));

        //set search feature
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = new SearchView( getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                //Search for recipes on server when text changed
                Recipe.loadRecipes(activity, 0, 100, newText, "" + categoryId, new Recipe.onRecipesDownloadedListener() {
                    @Override
                    public void onRecipesDownloaded(List<Recipe> recipes) {
                        setRecipes(recipes);
                    }
                });
                return false;
            }
        });
        return true;
    }


}
