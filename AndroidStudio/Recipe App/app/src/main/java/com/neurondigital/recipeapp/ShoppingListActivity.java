package com.neurondigital.recipeapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.neurondigital.neurondigital_listview.NDListview;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

import java.util.List;

import static com.neurondigital.recipeapp.IngredientItem.getIngredients;

/**
 * Created by melvin on 08/09/2016.
 */
public class ShoppingListActivity extends AppCompatActivity {
    Context context;
    AppCompatActivity activity;
    NDListview ingredientListView;
    ShoppingListAdapter shoppingListAdapter;
    RelativeLayout empty;
    FloatingActionButton buttonFloat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_shoppinglist);

        context = this;
        activity=this;
        ingredientListView = (NDListview) findViewById(R.id.listview_ingredients);
        empty = (RelativeLayout) findViewById(R.id.empty);
        buttonFloat =  findViewById(R.id.buttonFloat);


        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.shoppinglist_page_title));
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

        //set Float '+' button icon
//        buttonFloat.setDrawableIcon(
//                new IconicsDrawable(activity)
//                        .icon(FontAwesome.Icon.faw_plus)
//                        .color(ContextCompat.getColor(context, R.color.md_white_1000))
//                        .sizeDp(18));

        //set Float '+' button on click listener
        buttonFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddItemToShoppingListDialog();
            }
        });

        //load ingredient list
        createIngredientList(activity);
    }






    /**
     * open a dialog which asks the user to add an item to shopping cart
     */
    public void openAddItemToShoppingListDialog() {
        Alert AddItemAlert = new Alert();
        AddItemAlert.DisplayEditText(getString(R.string.add_item_dialog_title), getString(R.string.add_item_dialog_description), getString(R.string.add_item_dialog_hint), getString(R.string.Alert_accept), getString(R.string.Alert_cancel), activity);
        AddItemAlert.setPositiveButtonListener(new Alert.PositiveButtonListener() {
            @Override
            public void onPositiveButton(String input) {
                //add to shopping cart
                //Save.addToArray(input, "shopping_list", getContext());
                IngredientItem.addIngredient(context, input, getString(R.string.add_item_other));
                createIngredientList(context);
            }
        });
        AddItemAlert.show(getSupportFragmentManager(), getString(R.string.add_item_dialog_title));
    }

    /**
     * Creates list of ingredients
     *
     * @param context
     */
    public void createIngredientList(final Context context) {
        List<IngredientItem> items = getIngredients(context);
        if (items != null) {
            //create List Adapter
            shoppingListAdapter = new ShoppingListAdapter(context);

            //add ingredients
            for (int i = 0; i < items.size(); i++) {

                //TODO: Modify if you wish to add functionality on Checkbox changed
//                final int i_const = i;
//                items.get(i).setCheckbox(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                    }
//                }, false);

                //add item to list adapter
                shoppingListAdapter.add(items.get(i));
            }


            //set the adapter
            ingredientListView.setAdapter(shoppingListAdapter);
            ingredientListView.setEmptyView(empty);

            //enable drag and drop
            // ingredientListView.enableDragAndDrop(R.id.list_row_draganddrop_touchview);

            //enable swipe to delete item with undo
            ingredientListView.enableSwipeUndo(new OnDismissCallback() {
                @Override
                public void onDismiss(@NonNull ViewGroup listView, @NonNull int[] reverseSortedPositions) {
                    for (int position : reverseSortedPositions) {
                        List<IngredientItem> items = getIngredients(context);
                        if (items.get(position).isSection) {
                            IngredientItem.deleteAllRecipeIngredients(context, items.get(position).text1);
                        } else {
                            IngredientItem.deleteIngredient(context, items.get(position).text1, items.get(position).parentRecipe);
                        }
                        //ingredientListView.adapter.remove(position);
                        //Save.removeFromArray(position, "shopping_list", getContext());
                    }
                    createIngredientList(context);
                }
            });
        }


//        TODO: Modify to add functionality when item clicked
//        ingredientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
//                Toast.makeText(SingleLineListActivity.this, "Click pos: " + position + " " + singleLineListAdapter.getItem(position).text1, Toast.LENGTH_SHORT).show();
//
//            }
//        });
    }

//

    /**
     * Share cart on social media
     */
    public void share(Activity activity) {
        String str_ingredients = "";
        List<IngredientItem> items = getIngredients(context);
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isSection)
                str_ingredients += items.get(i).text1 + "\r\n";
            else
                str_ingredients += " + " + items.get(i).text1 + "\r\n";
        }
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, str_ingredients);
        sendIntent.setType("text/plain");
        activity.startActivity(Intent.createChooser(sendIntent, "Share via"));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shoppinglist_menu, menu);

        //set share icon from FontAwsome
        menu.findItem(R.id.share).setIcon(
                new IconicsDrawable(context)
                        .icon(FontAwesome.Icon.faw_share_alt)
                        .color(ContextCompat.getColor(context, R.color.md_white_1000))
                        .sizeDp(18));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //handle clear all button in option menu
            case R.id.clear:
                //Save.removeArray("shopping_list", getContext());
                IngredientItem.deleteAllCart(context);
                ingredientListView.adapter.clear();

                return true;
            //handle share button
            case R.id.share:
                share(activity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        List<IngredientItem> items = getIngredients(context);
        for (int i = 0; i < items.size(); i++) {
            ingredientListView.dismiss(i);
        }
    }
}
