package com.neurondigital.recipeapp;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.android.vending.billing.IabBroadcastReceiver;
import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;
import com.android.vending.billing.Purchase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the first Activity shown.
 * <p>
 * Handles the generation of the side navigation drawer, shows the main fragment and shows ads if enabled
 */

public class MainActivity extends AppCompatActivity implements IabBroadcastReceiver.IabBroadcastListener {

    Toolbar toolbar;
    Drawer drawer;
    Context context;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    //ad related
    AdView ad;
    LinearLayout BackgroundLayout;
    AdvertHelper advertHelper;



    //billing
    BillingHelper billingHelper;

    //navigation drawer item identification numbers
    final int NAV_FAV = 1, NAV_SHOP = 2, NAV_INFO = 4, NAV_PREMIUM = 5, NAVSETTINGS = 6, NAV_CATEGORIES = 100, NAV_ADD_RECIPE = 7, NAV_POLICY = 8, NAV_TERMS = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Configuration configuration = getResources().getConfiguration();
//        configuration.setLayoutDirection(new Locale("fa"));
//        configuration.setLocale(new Locale("fa"));
//        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        setContentView(R.layout.activity_main);
        this.context = this;

        //portrait only
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //admob app id
        if (getResources().getString(R.string.admob_app_id).length() > 1)
            MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));

        //enable/disable Firebase topic subscription
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean("pref_enable_push_notifications", true))
            FirebaseMessaging.getInstance().subscribeToTopic(Configurations.FIREBASE_PUSH_NOTIFICATION_TOPIC);
        else
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Configurations.FIREBASE_PUSH_NOTIFICATION_TOPIC);


        //set toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        //Generate the side navigation drawer
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                // .withRootView(R.id.drawer_container)
                .withDisplayBelowStatusBar(true)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(getDrawerItems(null))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //On click: open the required activity or fragment
                        Intent intent;
                        switch ((int) drawerItem.getIdentifier()) {
                            case NAV_FAV:
                                intent = new Intent(context, FavoriteActivity.class);
                                startActivity(intent);
                                break;
                            case NAV_SHOP:
                                intent = new Intent(context, ShoppingListActivity.class);
                                startActivity(intent);
                                break;
                            case NAV_INFO:
                                intent = new Intent(context, InfoActivity.class);
                                startActivity(intent);
                                break;
                            case NAV_PREMIUM:
                                intent = new Intent(context, PremiumActivity.class);
                                startActivity(intent);
                                break;
                            case NAVSETTINGS:
                                intent = new Intent(context, SettingsActivity.class);
                                startActivity(intent);
                                break;
                            case NAV_ADD_RECIPE:
                                intent = new Intent(context, AddRecipeActivity.class);
                                startActivity(intent);
                                break;
                            case NAV_POLICY:
                                intent = new Intent(context, PolicyActivity.class);
                                startActivity(intent);
                                break;
                            case NAV_TERMS:
                                intent = new Intent(context, TermsActivity.class);
                                startActivity(intent);
                                break;
                            default:
                                //opens the categories displayed in drawer
                                if (drawerItem.getIdentifier() > NAV_CATEGORIES) {
                                    intent = new Intent(context, CategoryRecipesActivity.class);
                                    intent.putExtra("Category_id", (int) (drawerItem.getIdentifier() - NAV_CATEGORIES));
                                    startActivity(intent);
                                }
                        }
                        drawer.closeDrawer();
                        return true;
                    }
                })
                .build();


        //initialise billing
        billingHelper = new BillingHelper(this,
                new BillingHelper.RefreshListener() {
                    @Override
                    public void onRefresh(boolean isPremium, Inventory inventory) {
                        if (isPremium) {
                            //destroy banner ad
                            if (ad != null)
                                ad.destroy();
                            if (BackgroundLayout != null)
                                BackgroundLayout.removeView(ad);

                            //remove upgrade 'Go premium' from drawer
                            drawer.removeItem(NAV_PREMIUM);

                            //remove purchase
                            invalidateOptionsMenu();
                        }
                    }
                },
                new IabHelper.OnIabPurchaseFinishedListener() {
                    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                        System.out.println("Purchase successful " + result);
                    }
                },
                new IabHelper.OnConsumeFinishedListener() {
                    public void onConsumeFinished(Purchase purchase, IabResult result) {
                    }
                }, Configurations.PUBLIC_KEY);


        // Important: Dynamically register for broadcast messages about updated purchases.
        // We register the receiver here instead of as a <receiver> in the Manifest
        // because we always call getPurchases() at startup, so therefore we can ignore
        // any broadcasts sent while the app isn't running.
        // Note: registering this listener in an Activity is a bad idea, but is done here
        // because this is a SAMPLE. Regardless, the receiver must be registered after
        // IabHelper is setup, but before first call to getPurchases().
        billingHelper.mBroadcastReceiver = new IabBroadcastReceiver(MainActivity.this);
        IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
        registerReceiver(billingHelper.mBroadcastReceiver, broadcastFilter);


        //Admob Banner and Interstitial Advert
        BackgroundLayout = (LinearLayout) findViewById(R.id.background_layout);
        if (!billingHelper.isPremium()) {
            advertHelper = new AdvertHelper(this, getResources().getString(R.string.interstitial_ad), null);
            advertHelper.initialiseInterstitialAd();
            final AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("275D94C2B5B93B3C4014933E75F92565")///nexus7//////testing
                    .addTestDevice("91608B19766D984A3F929C31EC6AB947")
                    .addTestDevice("6316D285813B01C56412DAF4D3D80B40") ///test htc sensesion xl
                    .addTestDevice("8C416F4CAF490509A1DA82E62168AE08")//asus transformer
                    .addTestDevice("7B4C6D080C02BA40EF746C4900BABAD7")//Galaxy S4
                    .addTestDevice("EA8AA9C3AA2BD16A954F592C6F935628")//motorola moto G
                    .addTestDevice("2278D56057803B605C31511DBFBEE7DF")//S7 edge
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();

            ad = (AdView) findViewById(R.id.adView);
            if (getResources().getString(R.string.banner_ad).length() > 1) {
                ad.loadAd(adRequest);
            } else {
                if (ad != null)
                    ad.destroy();
                if (BackgroundLayout != null)
                    BackgroundLayout.removeView(ad);
            }
        }

        Category.loadCategories(context, "", new Category.onCategoriesDownloadedListener() {
            @Override
            public void onCategoriesDownloaded(List<Category> categories) {
                refreshNavDrawer(categories);
            }
        });

    }


    /**
     * tabs creator
     *
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), getString(R.string.tab_recent));
        adapter.addFragment(new CategoryTextAndImageFragment(), getString(R.string.tab_categories));
        viewPager.setAdapter(adapter);
    }

    /**
     * Tabs adaptor
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    /**
     * Removes all items from drawer and creates them again to refresh.
     *
     * @param categories - List of Categories
     */
    public void refreshNavDrawer(List<Category> categories) {
        drawer.removeAllItems();
        drawer.addItems(getDrawerItems(categories));
    }

    /**
     * Generates a list of Drawer items
     *
     * @param categories
     * @return
     */
    public IDrawerItem[] getDrawerItems(List<Category> categories) {
        List<IDrawerItem> drawerItems = new ArrayList<>();

        //Add Home, Favorites and Shopping list
        drawerItems.add(new SecondaryDrawerItem().withIdentifier(NAV_FAV).withName(R.string.nav_favorites).withIcon(FontAwesome.Icon.faw_star));
        drawerItems.add(new SecondaryDrawerItem().withIdentifier(NAV_SHOP).withName(R.string.nav_shopping_list).withIcon(FontAwesome.Icon.faw_shopping_cart));

        if (Configurations.DISPLAY_CATEGORIES_IN_NAVIGATION_DRAWER) {
            //Add categories and more...
            drawerItems.add(new SectionDrawerItem().withName(R.string.nav_categories));
            if (categories != null) {
                for (int i = 0; i < categories.size(); i++) {
                    if (i < getResources().getInteger(R.integer.categories_to_show_in_drawer))
                        drawerItems.add(new SecondaryDrawerItem().withIdentifier(NAV_CATEGORIES + categories.get(i).id).withName(categories.get(i).name).withIcon(FontAwesome.Icon.faw_cutlery));
                }
            }
            drawerItems.add(new DividerDrawerItem());
        }

        //add final 4 items
        drawerItems.add(new SecondaryDrawerItem().withIdentifier(NAV_ADD_RECIPE).withName(R.string.nav_add_recipe).withIcon(FontAwesome.Icon.faw_plus));
        drawerItems.add(new SecondaryDrawerItem().withIdentifier(NAV_INFO).withName(R.string.nav_info).withIcon(FontAwesome.Icon.faw_question));
        if (Configurations.PUBLIC_KEY.length() > 0)
            drawerItems.add(new SecondaryDrawerItem().withIdentifier(NAV_PREMIUM).withName(R.string.nav_go_premium).withIcon(FontAwesome.Icon.faw_money));
        drawerItems.add(new SecondaryDrawerItem().withIdentifier(NAVSETTINGS).withName(R.string.nav_settings).withIcon(FontAwesome.Icon.faw_cog));
        drawerItems.add(new SecondaryDrawerItem().withIdentifier(NAV_TERMS).withName(R.string.terms).withIcon(FontAwesome.Icon.faw_file_o));
        drawerItems.add(new SecondaryDrawerItem().withIdentifier(NAV_POLICY).withName(R.string.privacy).withIcon(FontAwesome.Icon.faw_info));


        return drawerItems.toArray(new IDrawerItem[0]);
    }

//    /**
//     * Open interstitial Ad every couple of times. The number of clicks can be set from strings.xml
//     * Doesn't display ads in premium mode.
//     *
//     * @return
//     */
//    public boolean loadInterstitial() {
//        if (!billingHelper.isPremium()) {
//            ad_counter++;
//            if (ad_counter >= getResources().getInteger(R.integer.ad_shows_after_X_clicks)) {
//                advertHelper.openInterstitialAd(new AdvertHelper.InterstitialListener() {
//                    @Override
//                    public void onClosed() {
//                    }
//
//                    @Override
//                    public void onNotLoaded() {
//
//                    }
//                });
//
//                ad_counter = 0;
//                return true;
//            }
//        }
//        return false;
//    }


    /**
     * On back pressed, always go to home fragment before closing
     */
    @Override
    public void onBackPressed() {
            super.onBackPressed();

    }


    /**
     * Broadcast receiver for billing
     */
    @Override
    public void receivedBroadcast() {
        billingHelper.receivedBroadcast();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //back from billing
        if (billingHelper.onActivityResult(requestCode, resultCode, data)) {

        }

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        if (ad != null)
            ad.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ad != null)
            ad.resume();
        if (billingHelper != null)
            billingHelper.refreshInventory();
    }

    @Override
    public void onDestroy() {
        if (ad != null)
            ad.destroy();
        super.onDestroy();
        if (billingHelper.mBroadcastReceiver != null) {
            unregisterReceiver(billingHelper.mBroadcastReceiver);
        }
        if (billingHelper != null)
            billingHelper.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

}
