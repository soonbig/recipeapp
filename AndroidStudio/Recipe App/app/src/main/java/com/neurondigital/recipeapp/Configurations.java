package com.neurondigital.recipeapp;

/**
 * Created by melvin on 25/09/2016.
 */
public class Configurations {

    //GENERAL---------------------------------------------------------------------------------------

    //Server URL
    public static String SERVER_URL = "http://soonchangmusic.com/recipeapp/";


    //IN-APP PURCHASE-------------------------------------------------------------------------------
    //TO USE THE IN-APP PURCHASE FEATURE AN EXTENDED LICENSE NEEDS TO BE PURCHASED.
    //ONLY PUT IN A PUBLIC KEY IF YOU PURCHASED AN EXTENDED LICENSE FROM
    // CODECANYON: http://codecanyon.net/user/neurondigital/portfolio?ref=neurondigital

    //OPTIONAL - Leave 'PUBLIC_KEY' empty to disable in-app purchase.
    //final static String PUBLIC_KEY = "";
    final static String PUBLIC_KEY = "";

    // For testing use:  "android.test.purchased";  to make the purchase always accepted without an actual payment
    //Needs to be the same as the product id used in the Google Play Dashboard
    final static String SKU_PREMIUM = "your_premium_upgrade_sku_here";
   // final static String SKU_PREMIUM = "android.test.purchased";

    //DEEP LINK SHARE-------------------------------------------------------------------------------
    //in android manifest don't forget:
    //<data android:host="recipeapp.neurondigital.com" android:scheme="http"/>
    //<data android:host="recipeapp.neurondigital.com" android:scheme="https"/>
    final static String DEEP_LINK_TO_SHARE = "http://recipeapp.neurondigital.com";


    //LIST TYPE------------------------------------------------------------------------------------
    public final static int LIST_FULLWIDTH = 0, LIST_2COLUMNS = 1;
    public final static int LIST_MENU_TYPE = LIST_FULLWIDTH;


    //CATEGORIES------------------------------------------------------------------------------------

    public final static boolean DISPLAY_CATEGORIES_IN_NAVIGATION_DRAWER = true;

    //Ingredients-----------------------------------------------------------------------------------
    //fraction (true) or decimal (false)
    public final static boolean INGREDIENTS_FRACTION = true;

    //Nutrition-------------------------------------------------------------------------------------
    //daily values to calculate percent
    public final static float FAT_DAILY_VALUE =65;//grams
    public final static float SATURATED_FAT_DAILY_VALUE =20;//grams
    public final static float CHOLESTEROL_DAILY_VALUE =300;//milligrams
    public final static float SODIUM_DAILY_VALUE =2400;//milligrams
    public final static float TOTAL_CARBOHYDRATE_DAILY_VALUE =300;//grams
    public final static float DIETARY_FIBER_DAILY_VALUE =25;//grams

    public final static float RECIPE_IMAGE_RATIO =1f;//1:1

    //FIREBASE PUSH NOTIFICATION--------------------------------------------------------------------
    public final static String FIREBASE_PUSH_NOTIFICATION_TOPIC = "news";


}
