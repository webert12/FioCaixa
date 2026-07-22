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

        // Configura o comportamento ao carregar
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // CSS REFORÇADO: Esconde o cabeçalho, botão Fork, ícone do GitHub, 3 pontinhos e rodapé do Streamlit
                String css = "header, footer, #MainMenu, " +
                             "[data-testid=\"stHeader\"], " +
                             "[data-testid=\"stToolbar\"], " +
                             "[data-testid=\"stStatusWidget\"], " +
                             "[data-testid=\"stDecoration\"], " +
                             "div[class*=\"stAppToolbar\"], " +
                             "div[class*=\"viewerBadge\"], " +
                             "div[class*=\"styles_viewerBadge\"], " +
                             ".viewerBadge_link, " +
                             ".stApp > header { display: none !important; visibility: hidden !important; opacity: 0 !important; pointer-events: none !important; }";

                String js = "var style = document.createElement('style'); " +
                            "style.type = 'text/css'; " +
                            "style.innerHTML = '" + css + "'; " +
                            "document.head.appendChild(style);";

                view.evaluateJavascript(js, null);

                hideSplash();
            }
        });

        // 2. Tela de Capa / Splash Screen com a sua Logo
        splashImage = new ImageView(this);
        splashImage.setImageResource(R.drawable.logo);
        splashImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        splashImage.setBackgroundColor(Color.WHITE);

        rootLayout.addView(webView);
        rootLayout.addView(splashImage);

        setContentView(rootLayout);

        // Carrega a URL oficial do seu Streamlit
        webView.loadUrl("https://financassalao-blazvouwtjau5y667nrlrd.streamlit.app/");

        // Trava de segurança: remove a logo após 4 segundos
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                hideSplash();
            }
        }, 4000);
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
