package com.neurondigital.recipeapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsButton;
import com.mikepenz.iconics.view.IconicsImageView;
import com.neurondigital.neurondigital_listview.NDListview;
import com.neurondigital.neurondigital_listview.SingleLineItem;
import com.neurondigital.neurondigital_listview.SingleLineListAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;

/**
 * Created by melvin on 08/09/2016.
 */
public class AddRecipeActivity extends AppCompatActivity {
    Context context;
    private RichEditor mEditor;
    Spinner categorySpinner;
    List<Category> categ;
    EditText newIngredient, recipeTitle;
    IconicsButton addIngredient;
    NDListview ingredientList;
    SingleLineListAdapter ingredientListViewAdapter;
    IconicsImageView imageView;
    EditText[] nutritionTextVews = new EditText[16];
    LinearLayout nutrition_info;
    Switch nutrition_info_enable;

    EditText difficulty, prep_time, cook_time;

    Bitmap image;
    LoadImage loadImageHelper;
    Uri selectedImage;

    AppCompatActivity activity;

    //requestCodes
    final int PICK_IMAGE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_add_recipe);


        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        //submit button
        Button SubmitBtn = (Button) findViewById(R.id.submit);
        SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadRecipe();
            }
        });

        activity=this;
        context = this;

        setTitle(getString(R.string.add_recipe_title));

        //image laoder
        loadImageHelper = new LoadImage(this);
        imageView = (IconicsImageView) findViewById(R.id.image);
        imageView.setPadding(30,30,30,30);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image != null) {
                    //delete
                    image = null;
                    imageView.setIcon(new IconicsDrawable(context, FontAwesome.Icon.faw_picture_o));

                    imageView.setPadding(30,30,30,30);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                }
            }
        });

        //recipe title
        recipeTitle = (EditText) findViewById(R.id.title);

        //ingridient list
        newIngredient = (EditText) findViewById(R.id.new_ingredient);
        addIngredient = (IconicsButton) findViewById(R.id.add_ingredient);
        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientListViewAdapter.add(new SingleLineItem(newIngredient.getText().toString()));
                newIngredient.setText("");
                ingredientListViewAdapter.notifyDataSetChanged();
                ingredientList.setHeightBasedOnChildren();
            }
        });
        ingredientList = (NDListview) findViewById(R.id.listview_ingredients);
        //create List Adapter
        ingredientListViewAdapter = new SingleLineListAdapter(this);
        //ingredientListViewAdapter.add(new SingleLineItem("test"));
        //set the adapter
        ingredientList.setAdapter(ingredientListViewAdapter);
        ingredientList.setHeightBasedOnChildren();
        ingredientList.enableSwipeUndo(new OnDismissCallback() {
            @Override
            public void onDismiss(@NonNull ViewGroup listView, @NonNull int[] reverseSortedPositions) {
                for (int i = 0; i < reverseSortedPositions.length; i++) {
                    ingredientListViewAdapter.remove(reverseSortedPositions[i]);
                }
                ingredientListViewAdapter.notifyDataSetChanged();
                ingredientList.setHeightBasedOnChildren();
            }
        });


        //directions html editor
        mEditor = (RichEditor) findViewById(R.id.description);
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder(getString(R.string.recipe_directions));
        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
            }
        });

        //editor buttons

        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.redo();
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
            }
        });

        findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSubscript();
            }
        });

        findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSuperscript();
            }
        });

        findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBullets();
            }
        });

        findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setNumbers();
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alert AddItemAlert = new Alert();
                AddItemAlert.DisplayEditText(getString(R.string.html_editor_insert_image_title), getString(R.string.html_editor_insert_image_description), getString(R.string.html_editor_insert_image_hint), getString(R.string.Alert_accept), getString(R.string.Alert_cancel), activity);
                AddItemAlert.setPositiveButtonListener(new Alert.PositiveButtonListener() {
                    @Override
                    public void onPositiveButton(String input) {
                        mEditor.insertImage(input, "");
                    }
                });
                AddItemAlert.show(getSupportFragmentManager(), getString(R.string.add_item_dialog_title));

            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alert AddItemAlert = new Alert();
                AddItemAlert.DisplayEditText2(getString(R.string.html_editor_insert_link_title), getString(R.string.html_editor_insert_link_description), getString(R.string.html_editor_insert_link_title_hint), getString(R.string.html_editor_insert_link_url_hint), getString(R.string.Alert_accept), getString(R.string.Alert_cancel), activity);
                AddItemAlert.setPositiveButtonListener2(new Alert.PositiveButtonListener2() {
                    @Override
                    public void onPositiveButton(String inputTitle, String inputURL) {
                        mEditor.insertLink(inputURL, inputTitle);
                    }
                });
                AddItemAlert.show(getSupportFragmentManager(), getString(R.string.add_item_dialog_title));

            }
        });
        findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertTodo();
            }
        });

        categorySpinner = (Spinner) findViewById(R.id.category);

        Category.loadCategories(this, "", new Category.onCategoriesDownloadedListener() {
            @Override
            public void onCategoriesDownloaded(List<Category> categories) {
                if(activity==null)
                    return;

                categ = categories;
                String[] categoryNames = new String[categories.size()];
                for (int i = 0; i < categories.size(); i++) {
                    categoryNames[i] = categories.get(i).name;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, categoryNames);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
            }
        });


        //nutritional layout
        nutrition_info = (LinearLayout) findViewById(R.id.nutrition);

        //nutrition textviews
        nutrition_info_enable = (Switch) findViewById(R.id.nutrition_enable);
        nutritionTextVews[Recipe.NUTR_CALORIES] = (EditText) findViewById(R.id.calories);
        nutritionTextVews[Recipe.NUTR_CALORIES_FROM_FAT] = (EditText) findViewById(R.id.calories_from_fat);
        nutritionTextVews[Recipe.NUTR_TOTAL_FAT] = (EditText) findViewById(R.id.total_fat);
        nutritionTextVews[Recipe.NUTR_SATURATED_FAT] = (EditText) findViewById(R.id.saturated_fat);
        nutritionTextVews[Recipe.NUTR_TRANS_FAT] = (EditText) findViewById(R.id.trans_fat);
        nutritionTextVews[Recipe.NUTR_CHOLESTEROL] = (EditText) findViewById(R.id.cholesterol);
        nutritionTextVews[Recipe.NUTR_SODIUM] = (EditText) findViewById(R.id.sodium);
        nutritionTextVews[Recipe.NUTR_PROTEIN] = (EditText) findViewById(R.id.protein);
        nutritionTextVews[Recipe.NUTR_TOTAL_CARBOHYDRATE] = (EditText) findViewById(R.id.total_carbohydrate);
        nutritionTextVews[Recipe.NUTR_DIETARY_FIBER] = (EditText) findViewById(R.id.dietary_fiber);
        nutritionTextVews[Recipe.NUTR_SUGARS] = (EditText) findViewById(R.id.sugars);
        nutritionTextVews[Recipe.NUTR_VITAMIN_A] = (EditText) findViewById(R.id.vitamin_a_percent);
        nutritionTextVews[Recipe.NUTR_VITAMIN_C] = (EditText) findViewById(R.id.vitamin_c_percent);
        nutritionTextVews[Recipe.NUTR_CALCIUM] = (EditText) findViewById(R.id.calcium_percent);
        nutritionTextVews[Recipe.NUTR_IRON] = (EditText) findViewById(R.id.iron_percent);


        //process nutrition
        nutrition_info_enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    nutrition_info.setVisibility(View.VISIBLE);
                } else {
                    nutrition_info.setVisibility(View.GONE);
                }
            }
        });

        //general
        prep_time = (EditText) findViewById(R.id.preparation_time);
        cook_time = (EditText) findViewById(R.id.cooking_time);
        difficulty = (EditText) findViewById(R.id.difficulty);


    }




    /**
     * Upload a recipe to server via POST HTTP request
     */
    public void uploadRecipe() {
        //url
        String url = Configurations.SERVER_URL + "api/recipe";

        //check if image loaded
        if (image == null) {
            Toast.makeText(this, getString(R.string.add_image), Toast.LENGTH_LONG).show();
            return;
        }

        //upload
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                //upload success
                String resultResponse = new String(response.data);
                System.out.println("Server says: " + resultResponse);
                Toast.makeText(activity, getString(R.string.recipe_submitted), Toast.LENGTH_LONG).show();

                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //error. Probably no internet
                Functions.noInternetAlert(activity);

                //show error
                if (error.networkResponse.data != null) {
                    try {
                        String body = new String(error.networkResponse.data, "UTF-8");
                        System.out.println(body);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }) {


            @Override
            protected Map<String, String> getParams() {
                //get category id
                int categoryId = 0;
                if (categ.size() >= categorySpinner.getSelectedItemPosition())
                    categoryId = categ.get(categorySpinner.getSelectedItemPosition()).id;

                //fill up parameters
                Map<String, String> params = new HashMap<>();
                params.put("name", recipeTitle.getText().toString());
                params.put("directions", mEditor.getHtml());
                params.put("category", "" + categoryId);
                for (int i = 0; i < ingredientListViewAdapter.getItems().size(); i++) {
                    System.out.println("ingredients[" + i + "]  "+ingredientListViewAdapter.getItems().get(i).text1);
                    params.put("ingredients[" + i + "]", ingredientListViewAdapter.getItems().get(i).text1);
                }
               // params.put("ingredients[" + ingredientListViewAdapter.getItems().size() + "]", "extra");

                params.put("nutr_enable", "" + (nutrition_info_enable.isChecked() ? 1 : 0));
                params.put("nutr_calories", "" +    nutritionTextVews[Recipe.NUTR_CALORIES].getText().toString());
                params.put("nutr_calories_from_fat", "" +    nutritionTextVews[Recipe.NUTR_CALORIES_FROM_FAT].getText().toString());
                params.put("nutr_total_fat", "" +    nutritionTextVews[Recipe.NUTR_TOTAL_FAT].getText().toString());
                params.put("nutr_saturated_fat", "" +    nutritionTextVews[Recipe.NUTR_SATURATED_FAT].getText().toString());
                params.put("nutr_trans_fat", "" +    nutritionTextVews[Recipe.NUTR_TRANS_FAT].getText().toString());
                params.put("nutr_cholesterol", "" +    nutritionTextVews[Recipe.NUTR_CHOLESTEROL].getText().toString());
                params.put("nutr_sodium", "" +    nutritionTextVews[Recipe.NUTR_SODIUM].getText().toString());
                params.put("nutr_protein", "" +    nutritionTextVews[Recipe.NUTR_PROTEIN].getText().toString());
                params.put("nutr_total_carbohydrate", "" +    nutritionTextVews[Recipe.NUTR_TOTAL_CARBOHYDRATE].getText().toString());
                params.put("nutr_dietary_fiber", "" +    nutritionTextVews[Recipe.NUTR_DIETARY_FIBER].getText().toString());
                params.put("nutr_sugars", "" +    nutritionTextVews[Recipe.NUTR_SUGARS].getText().toString());
                params.put("nutr_vitamin_a", "" +    nutritionTextVews[Recipe.NUTR_VITAMIN_A].getText().toString());
                params.put("nutr_vitamin_c", "" +    nutritionTextVews[Recipe.NUTR_VITAMIN_C].getText().toString());
                params.put("nutr_calcium", "" +    nutritionTextVews[Recipe.NUTR_CALCIUM].getText().toString());
                params.put("nutr_iron", "" +    nutritionTextVews[Recipe.NUTR_IRON].getText().toString());

                params.put("prep_time", "" +   prep_time.getText().toString());
                params.put("cook_time", "" +   cook_time.getText().toString());
                params.put("difficulty", "" +    difficulty.getText().toString());

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                //compress image to JPEG
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                //add image to parameters
                Map<String, DataPart> params = new HashMap<>();
                params.put("image", new DataPart("image.jpg", byteArray, "image/jpg"));

                return params;
            }
        };

        //send
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(activity.getBaseContext()).addToRequestQueue(multipartRequest);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == RESULT_OK && null != data) {
                    selectedImage = data.getData();
                    Bitmap image = loadImageHelper.decodeSampledBitmapFromResource(selectedImage, 600, 300);
                    if (image != null) {
                        this.image = image;
                        imageView.setImageBitmap(image);
                        imageView.setPadding(0,0,0,0);
                    }
                }
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Bitmap image = loadImageHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (image != null) {
            this.image = image;
            imageView.setImageBitmap(image);
        }
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
