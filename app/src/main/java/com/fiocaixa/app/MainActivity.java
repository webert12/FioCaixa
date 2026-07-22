package com.fiocaixa.app;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.content.Intent;
import android.net.Uri;

import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;
import android.webkit.WebChromeClient;
import android.webkit.ValueCallback;
import android.webkit.CookieSyncManager;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ImageView splashImage;
    private boolean isSplashHidden = false;

    private static final String APP_URL =
            "https://fioecaixa.onrender.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout rootLayout = new FrameLayout(this);

        webView = new WebView(this);

        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);

        // Permissões para arquivos/downloads
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);

        // Permite abertura de janelas do Streamlit
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        // Atualiza sempre o conteúdo do servidor
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(false);

        webSettings.setMixedContentMode(
                WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        );

        webSettings.setSupportMultipleWindows(true);


        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request
            ) {

                return abrirLink(request.getUrl().toString());
            }


            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    String url
            ) {

                return abrirLink(url);
            }


            @Override
            public void onReceivedError(
                    WebView view,
                    WebResourceRequest request,
                    WebResourceError error
            ) {

                if (request.isForMainFrame()) {

                    Toast.makeText(
                            MainActivity.this,
                            "Erro ao carregar o sistema. Verifique sua internet.",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }


            @Override
            public void onPageFinished(
                    WebView view,
                    String url
            ) {

                super.onPageFinished(view, url);


                String js =

                        "var style=document.createElement('style');" +

                        "style.innerHTML='" +

                        "header,footer,#MainMenu," +

                        "[data-testid=\"stHeader\"]," +

                        "[data-testid=\"stToolbar\"]," +

                        "[data-testid=\"stStatusWidget\"]," +

                        "[data-testid=\"stDecoration\"]," +

                        "div[class*=\"viewerBadge\"]," +

                        "div[class*=\"stAppToolbar\"]" +

                        "{display:none!important;visibility:hidden!important;}" +

                        "';" +

                        "document.head.appendChild(style);";


                view.evaluateJavascript(js, null);


                hideSplash();
            }

        });



        webView.setWebChromeClient(new WebChromeClient());


        splashImage = new ImageView(this);

        splashImage.setImageResource(R.drawable.logo);

        splashImage.setScaleType(
                ImageView.ScaleType.CENTER_INSIDE
        );

        splashImage.setBackgroundColor(Color.WHITE);


        rootLayout.addView(webView);

        rootLayout.addView(splashImage);


        setContentView(rootLayout);


        webView.loadUrl(APP_URL);



        new Handler(Looper.getMainLooper())
                .postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                hideSplash();
                            }
                        },
                        4000
                );

    }



    private boolean abrirLink(String url) {


        if (
                url.startsWith("https://wa.me")
                ||
                url.startsWith("https://api.whatsapp.com")
                ||
                url.startsWith("whatsapp://")
                ||
                url.startsWith("tel:")
                ||
                url.startsWith("mailto:")
                ||
                url.startsWith("geo:")
        ) {


            try {

                Intent intent =
                        new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(url)
                        );

                startActivity(intent);

                return true;


            } catch(Exception e){

                return false;
            }
        }


        return false;
    }



    private void hideSplash(){

        if(
                splashImage != null
                &&
                !isSplashHidden
        ){

            isSplashHidden = true;

            splashImage.setVisibility(
                    View.GONE
            );
        }
    }



    @Override
    public void onBackPressed(){

        if(
                webView != null
                &&
                webView.canGoBack()
        ){

            webView.goBack();

        } else {

            super.onBackPressed();
        }
    }
}
