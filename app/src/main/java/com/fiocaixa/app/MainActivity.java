package com.fiocaixa.app;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        WebView webView = new WebView(this);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        
        webView.setWebViewClient(new WebViewClient());
        
        // SUBSTITUA O LINK ABAIXO PELO LINK DO SEU SISTEMA (Streamlit / Web)
        webView.loadUrl("https://financassalao-blazvouwtjau5y667nrlrd.streamlit.app/");
        
        setContentView(webView);
    }
}
