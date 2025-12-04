package com.manyfollowers;

import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InitializationStatus;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private AdView adView;
    private InterstitialAd mInterstitialAd;
    private final String URL_TO_LOAD = "https://creativesayan.gamer.gd/manyfollowers.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create layout programmatically: WebView + AdView at bottom
        ViewGroup root = new android.widget.RelativeLayout(this);
        setContentView(root);

        webView = new WebView(this);
        webView.setId(12345);
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);

        android.widget.RelativeLayout.LayoutParams webParams =
                new android.widget.RelativeLayout.LayoutParams(
                        android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,
                        android.widget.RelativeLayout.LayoutParams.MATCH_PARENT);
        webParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_TOP);
        webParams.addRule(android.widget.RelativeLayout.ABOVE, 54321);

        root.addView(webView, webParams);

        // Banner AdView (id 54321)
        adView = new AdView(this);
        adView.setId(54321);
        adView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
        adView.setAdUnitId("ADMOB_BANNER_ID"); // placeholder

        android.widget.RelativeLayout.LayoutParams adParams =
                new android.widget.RelativeLayout.LayoutParams(
                        android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,
                        android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
        adParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM);

        root.addView(adView, adParams);

        // WebView client and load
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(URL_TO_LOAD);

        // Initialize Mobile Ads with App ID placeholder
        MobileAds.initialize(this, initializationStatus -> {});

        // Load banner ad
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // Load interstitial
        loadInterstitial();
    }

    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ADMOB_INTERSTITIAL_ID", adRequest,
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;
                }
                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    mInterstitialAd = null;
                }
            });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            // show interstitial if loaded, then finish
            if (mInterstitialAd != null) {
                mInterstitialAd.show(this);
                // optional: reload after closing for next time
                new Handler().postDelayed(this::loadInterstitial, 2000);
            } else {
                super.onBackPressed();
            }
        }
    }
}
