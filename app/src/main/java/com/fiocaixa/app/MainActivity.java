package com.fiocaixa.app;

import android.graphics.Color;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout de contêiner principal
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

        // Configura o comportamento ao carregar
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // Injeta CSS para esconder o topo, rodapé e ícones do Streamlit/GitHub
                String css = "header[data-testid=\"stHeader\"], " +
                             "footer, " +
                             "#MainMenu, " +
                             "[data-testid=\"stToolbar\"], " +
                             "[data-testid=\"stDecoration\"], " +
                             ".stApp > header { display: none !important; visibility: hidden !important; }";

                String js = "var style = document.createElement('style'); " +
                            "style.type = 'text/css'; " +
                            "style.innerHTML = '" + css + "'; " +
                            "document.head.appendChild(style);";

                view.evaluateJavascript(js, null);

                // Oculta a capa/splash quando a página termina de carregar
                if (splashImage != null) {
                    splashImage.setVisibility(View.GONE);
                }
            }
        });

        // 2. Tela de Capa / Splash Screen com a sua Logo
        splashImage = new ImageView(this);
        splashImage.setImageResource(R.drawable.logo); // Utiliza o arquivo logo.png em res/drawable
        splashImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        splashImage.setBackgroundColor(Color.WHITE); // Fundo branco enquanto carrega

        // Adiciona a visualização na tela
        rootLayout.addView(webView);
        rootLayout.addView(splashImage);

        setContentView(rootLayout);

        // Substitua abaixo pela URL do seu Streamlit
        webView.loadUrl("https://financassalao-blazvouwtjau5y667nrlrd.streamlit.app/");
    }

    // Garante que o botão voltar navegue dentro do sistema em vez de fechar o app
    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
