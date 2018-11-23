package com.neurondigital.recipeapp;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by melvin on 08/09/2016.
 */
public class InfoActivity extends AppCompatActivity {
    Context context;

    WebView infoWebview;
    SwipeRefreshLayout swipeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_info);

        infoWebview = (WebView)findViewById(R.id.webview_info);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);

        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.info_page_title));
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

        context = this;

        //load info
        refresh();

        // Swipe to Refresh
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // refreshes the WebView
                refresh();
            }
        });

        //stop refresh
        infoWebview.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                swipeLayout.setRefreshing(false);
            }
        });
        //enable javascript
        infoWebview.getSettings().setJavaScriptEnabled(true);
        // infoWebview.clearCache(false);
        //infoWebview.getSettings().setMinimumFontSize((int) getResources().getDimension(R.dimen.webviewMinTextSize));
        //infoWebview.getSettings().setBuiltInZoomControls(true);
        //infoWebview.getSettings().setDisplayZoomControls(false);


    }



    public void refresh() {
        //load info
        Preference.load(context, "info", new Preference.onPreferenceDownloadedListener() {
            @Override
            public void onPreferenceDownloaded(String info) {
                infoWebview.loadData(Functions.HTMLTemplate(info), "text/html; charset=utf-8", "utf-8");
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        infoWebview.destroy();
    }

}
