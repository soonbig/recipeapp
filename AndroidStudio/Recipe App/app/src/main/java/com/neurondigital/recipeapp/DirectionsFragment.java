package com.neurondigital.recipeapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * Created by melvin on 28/09/2016.
 * A Fragment to Display ingredients. Uses a WebView to show Directions in HTML format.
 * Supports videos, images, javascript
 */
public class DirectionsFragment extends Fragment {

    WebView webViewDirections;
    TextView difficulty_textview, preperationTime_textview, cookingTime_textview;
    int recipeId;

    /**
     * Required empty public constructor
     */
    public DirectionsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        //get rootview
        View rootView = inflater.inflate(R.layout.fragment_directions, container, false);

        if(getActivity()==null)
            return rootView;

        //get recipe id
        recipeId = Recipe.getRecipeIdFromIntent(getActivity().getIntent(), savedInstanceState);

        //preparation, cooking time and difficulty
        preperationTime_textview = (TextView) rootView.findViewById(R.id.preparation_time);
        cookingTime_textview = (TextView) rootView.findViewById(R.id.cooking_time);
        difficulty_textview = (TextView) rootView.findViewById(R.id.difficulty);

        //get webView Resource
        webViewDirections = (WebView) rootView.findViewById(R.id.webview_directions);

        //enable javascript
        webViewDirections.getSettings().setJavaScriptEnabled(true);

        //load recipe
        Recipe.loadRecipe(getActivity(), recipeId, new Recipe.onRecipeDownloadedListener() {
            @Override
            public void onRecipeDownloaded(Recipe recipe) {
                if(recipe==null)
                    return;
                if (isAdded()) {
                    webViewDirections.loadData(Functions.HTMLTemplate(recipe.directions), "text/html; charset=utf-8", "utf-8");
                    preperationTime_textview.setText(recipe.prep_time + getString(R.string.minutes));
                    cookingTime_textview.setText(recipe.cook_time + getString(R.string.minutes));
                    difficulty_textview.setText("" + recipe.difficulty);
                }
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        if (recipeId >= 0)
            savedInstanceState.putInt("RECIPE_ID", recipeId);
        // etc.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webViewDirections.destroy();
    }


}
