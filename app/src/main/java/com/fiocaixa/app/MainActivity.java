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

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // Injeta um loop contínuo que monitora a tela e elimina qualquer elemento do Streamlit/GitHub que tente aparecer
                String js = "var style = document.createElement('style'); " +
                            "style.type = 'text/css'; " +
                            "style.innerHTML = '" +
                            "  header, footer, #MainMenu, " +
                            "  [data-testid=\"stHeader\"], " +
                            "  [data-testid=\"stToolbar\"], " +
                            "  [data-testid=\"stStatusWidget\"], " +
                            "  [data-testid=\"stDecoration\"], " +
                            "  div[class*=\"stAppToolbar\"], " +
                            "  div[class*=\"viewerBadge\"], " +
                            "  div[class*=\"styles_viewerBadge\"], " +
                            "  .viewerBadge_link, " +
                            "  .stApp > header { display: none !important; visibility: hidden !important; height: 0 !important; }'; " +
                            "document.head.appendChild(style); " +
                            "setInterval(function() { " +
                            "  var elements = document.querySelectorAll('header, footer, [data-testid=\"stHeader\"], [data-testid=\"stToolbar\"], div[class*=\"stAppToolbar\"], div[class*=\"viewerBadge\"]'); " +
                            "  elements.forEach(function(el) { el.style.display = 'none'; }); " +
                            "}, 200);";

                view.evaluateJavascript(js, null);

                hideSplash();
            }
        });

        // 2. Splash Screen
        splashImage = new ImageView(this);
        splashImage.setImageResource(R.drawable.logo);
        splashImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        splashImage.setBackgroundColor(Color.WHITE);

        rootLayout.addView(webView);
        rootLayout.addView(splashImage);

        setContentView(rootLayout);

        // ⚠️ O SEGREDO: ?embed=true no final da URL remove nativamente o topo e rodapé do Streamlit
        webView.loadUrl("https://financassalao-blazvouwtjau5y667nrlrd.streamlit.app/?embed=true");

        // Trava de segurança para remover a capa após 4 segundos
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
