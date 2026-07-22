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
import android.webkit.WebChromeClient;

import android.widget.FrameLayout;
import android.widget.ImageView;

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
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setSupportMultipleWindows(true);

        // Controle de links dentro do WebView
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
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // CSS injetado para ocultar a interface padrão do Streamlit
                String js =
                        "var style = document.createElement('style'); " +
                        "style.type='text/css'; " +
                        "style.innerHTML='" +
                        "header, footer, #MainMenu," +
                        "[data-testid=\"stHeader\"]," +
                        "[data-testid=\"stToolbar\"]," +
                        "[data-testid=\"stStatusWidget\"]," +
                        "[data-testid=\"stDecoration\"]," +
                        "div[class*=\"stAppToolbar\"]," +
                        "div[class*=\"viewerBadge\"]," +
                        "div[class*=\"styles_viewerBadge\"]," +
                        ".viewerBadge_link," +
                        ".stApp > header {" +
                        "display:none!important;" +
                        "visibility:hidden!important;" +
                        "height:0!important;" +
                        "}';" +
                        "document.head.appendChild(style);" +

                        "setInterval(function(){" +
                        "var elements=document.querySelectorAll(" +
                        "'header, footer, [data-testid=\"stHeader\"]," +
                        "[data-testid=\"stToolbar\"]," +
                        "div[class*=\"stAppToolbar\"]," +
                        "div[class*=\"viewerBadge\"]');" +
                        "elements.forEach(function(el){" +
                        "el.style.display='none';" +
                        "});" +
                        "},200);";

                view.evaluateJavascript(js, null);
                hideSplash();
            }
        });

        // Suporte a abertura de janelas popup e links externos
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view,
                                          boolean isDialog,
                                          boolean isUserGesture,
                                          android.os.Message resultMsg) {

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

        // Carrega a URL do Streamlit
        webView.loadUrl("https://financassalao-blazvouwtjau5y667nrlrd.streamlit.app/?embed=true");

        // Timeout de segurança para remover a Splash Screen após 4 segundos
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                hideSplash();
            }
        }, 4000);
    }

    // Trata links de redes sociais, telefone e mapas
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

    // Trata a abertura de links externos criados por novas janelas
    private void abrirLinkExterno(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Oculta a tela de Splash
    private void hideSplash() {
        if (splashImage != null && !isSplashHidden) {
            isSplashHidden = true;
            splashImage.setVisibility(View.GONE);
        }
    }

    // Trata o botão de voltar do dispositivo
    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
