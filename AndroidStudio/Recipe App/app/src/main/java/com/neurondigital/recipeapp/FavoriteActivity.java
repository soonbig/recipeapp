package com.neurondigital.recipeapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * Created by melvin on 08/09/2016.
 *
 * Shows a list of the favorite Recipes. The favorite recipes are stored by id locally in preferences.
 * The content is however obtained from server.
 */
public class FavoriteActivity extends AppCompatActivity {
    Context context;
    AppCompatActivity activity;

    private EmptyRecyclerView mRecyclerView;
    private EmptyRecyclerView.Adapter mAdapter;
    private EmptyRecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout swipeLayout;
    RelativeLayout empty;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_favorites);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        mRecyclerView = (EmptyRecyclerView) findViewById(R.id.recipesList);
        empty =  findViewById(R.id.empty);
        mRecyclerView.setEmptyView(empty);

        context = this;
        activity=this;

        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.favorite_page_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

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
     * Refresh Favorite Recipes.
     */
    public void refresh() {
        Recipe.getFavoriteRecipes(activity, new Recipe.onRecipesDownloadedListener() {
            @Override
            public void onRecipesDownloaded(List<Recipe> recipes) {
                swipeLayout.setRefreshing(false);
                setRecipes(recipes);
            }
        });
    }


    /**
     * Show recipes on screen after refresh
     * @param recipes
     */
    public void setRecipes(final List<Recipe> recipes) {
        mAdapter = new RecipeAdapter(recipes, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, SingleRecipeActivity.class);
                intent.putExtra("RECIPE_ID", recipes.get(i).id);
                startActivity(intent);
            }
        }, context);
        mRecyclerView.setAdapter(mAdapter);
    }



}
