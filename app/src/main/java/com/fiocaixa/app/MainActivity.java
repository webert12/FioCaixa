package com.fiocaixa.app;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.content.Intent;
import android.net.Uri;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;
import android.webkit.WebChromeClient;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ImageView splashImage;
    private boolean isSplashHidden = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout rootLayout = new FrameLayout(this);

        // Configuração do WebView
        webView = new WebView(this);
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        webSettings.setSupportMultipleWindows(true);

        // Controle de navegação e erros
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return abrirLink(request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return abrirLink(url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request.isForMainFrame()) {
                    Toast.makeText(MainActivity.this, "Erro ao carregar. Verifique sua conexão.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                String js =
                        "var style = document.createElement('style'); " +
                        "style.type='text/css'; " +
                        "style.innerHTML='" +
                        "header, footer, #MainMenu, " +
                        "[data-testid=\"stHeader\"], " +
                        "[data-testid=\"stToolbar\"], " +
                        "[data-testid=\"stStatusWidget\"], " +
                        "[data-testid=\"stDecoration\"], " +
                        "div[class*=\"stAppToolbar\"], " +
                        "div[class*=\"viewerBadge\"], " +
                        ".viewerBadge_link { display:none !important; visibility:hidden !important; height:0 !important; }';" +
                        "document.head.appendChild(style);";

                view.evaluateJavascript(js, null);
                hideSplash();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
                WebView tempWebView = new WebView(MainActivity.this);
                tempWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest request) {
                        abrirLinkExterno(request.getUrl().toString());
                        return true;
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView v, String url) {
                        abrirLinkExterno(url);
                        return true;
                    }
                });

                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(tempWebView);
                resultMsg.sendToTarget();
                return true;
            }
        });

        // Configuração da Splash Screen
        splashImage = new ImageView(this);
        splashImage.setImageResource(R.drawable.logo);
        splashImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        splashImage.setBackgroundColor(Color.WHITE);

        rootLayout.addView(webView);
        rootLayout.addView(splashImage);

        setContentView(rootLayout);

        // Carrega o app Streamlit
        webView.loadUrl("https://financassalao-blazvouwtjau5y667nrlrd.streamlit.app/?embed=true");

        // Handler compatível com Java 7/8
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                hideSplash();
            }
        }, 4000);
    }

    private boolean abrirLink(String url) {
        if (url.startsWith("https://wa.me")
                || url.startsWith("https://api.whatsapp.com")
                || url.startsWith("whatsapp://")
                || url.startsWith("tel:")
                || url.startsWith("mailto:")
                || url.startsWith("geo:")) {

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private void abrirLinkExterno(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
