package com.fiocaixa.app;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebChromeClient;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ImageView splashImage;
    private boolean isSplashHidden = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout rootLayout = new FrameLayout(this);

        // 1. Configuração do WebView
        webView = new WebView(this);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setWebViewClient(new WebViewClient() {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return abrirLink(request.getUrl().toString());
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return abrirLink(url);
    }

    private boolean abrirLink(String url) {

        // WhatsApp
        if (url.startsWith("https://wa.me")
                || url.startsWith("https://api.whatsapp.com")
                || url.startsWith("whatsapp://")) {

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        // Telefone
        if (url.startsWith("tel:")) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return true;
        }

        // Email
        if (url.startsWith("mailto:")) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return true;
        }

        // Google Maps
        if (url.startsWith("geo:")) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return true;
        }

        return false;
    }
            webView.setWebChromeClient(new WebChromeClient() {
    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {

        WebView.HitTestResult result = view.getHitTestResult();

        String data = result.getExtra();

        if (data != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
            startActivity(intent);
        }

        return false;
    }
});

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        String js = "var style = document.createElement('style'); " +
                "style.type = 'text/css'; " +
                "style.innerHTML = '" +
                "header, footer, #MainMenu," +
                "[data-testid=\"stHeader\"]," +
                "[data-testid=\"stToolbar\"]," +
                "[data-testid=\"stStatusWidget\"]," +
                "[data-testid=\"stDecoration\"]," +
                "div[class*=\"stAppToolbar\"]," +
                "div[class*=\"viewerBadge\"]," +
                "div[class*=\"styles_viewerBadge\"]," +
                ".viewerBadge_link," +
                ".stApp > header {display:none!important;visibility:hidden!important;height:0!important;}';" +
                "document.head.appendChild(style);" +
                "setInterval(function(){" +
                "var elements=document.querySelectorAll('header,footer,[data-testid=\"stHeader\"],[data-testid=\"stToolbar\"],div[class*=\"stAppToolbar\"],div[class*=\"viewerBadge\"]');" +
                "elements.forEach(function(el){el.style.display='none';});" +
                "},200);";

        view.evaluateJavascript(js, null);

        hideSplash();
    }
});
    }

    private void hideSplash() {
        if (splashImage != null && !isSplashHidden) {
            isSplashHidden = true;
            splashImage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
