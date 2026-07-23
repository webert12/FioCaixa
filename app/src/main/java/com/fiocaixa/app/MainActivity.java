package com.fiocaixa.app;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.net.Uri;

import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;
import android.webkit.WebChromeClient;
import android.webkit.WebView.WebViewTransport;

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

        // --- CÓDIGO NOVO: MODO FULLSCREEN (TELA CHEIA) ---
        // 1. Esconde a Action Bar (Barra de título com nome do app)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // 2. Esconde a Status Bar (Bateria, Wi-Fi, Relógio)
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        // -------------------------------------------------


        FrameLayout rootLayout =
                new FrameLayout(this);



        webView =
                new WebView(this);



        WebSettings settings =
                webView.getSettings();



        settings.setJavaScriptEnabled(true);

        settings.setDomStorageEnabled(true);

        settings.setDatabaseEnabled(true);

        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        settings.setSupportMultipleWindows(true);

        settings.setAllowFileAccess(true);

        settings.setAllowContentAccess(true);



        settings.setCacheMode(
                WebSettings.LOAD_NO_CACHE
        );


        settings.setBuiltInZoomControls(false);

        settings.setDisplayZoomControls(false);

        settings.setSupportZoom(false);



        settings.setMixedContentMode(
                WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        );



        CookieManager cookieManager =
                CookieManager.getInstance();


        cookieManager.setAcceptCookie(true);


        cookieManager.setAcceptThirdPartyCookies(
                webView,
                true
        );



        webView.setWebViewClient(
                new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request
            ) {

                String url =
                        request.getUrl().toString();


                return abrirLink(url);

            }



            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    String url
            ) {

                return abrirLink(url);

            }



            @Override
            public void onLoadResource(
                    WebView view,
                    String url
            ) {

                // Captura links WhatsApp carregados pelo Streamlit

                if(url.contains("whatsapp")
                        || url.contains("wa.me")) {


                    abrirWhatsApp(url);

                }


            }



            @Override
            public void onReceivedError(
                    WebView view,
                    WebResourceRequest request,
                    WebResourceError error
            ) {


                if(request.isForMainFrame()) {


                    Toast.makeText(
                            MainActivity.this,
                            "Erro ao carregar sistema",
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



                // Remove elementos do Streamlit
                String css =

                        "var style=document.createElement('style');"

                        +

                        "style.innerHTML='"

                        +

                        "header,footer,#MainMenu,"

                        +

                        "[data-testid=\"stHeader\"],"

                        +

                        "[data-testid=\"stToolbar\"],"

                        +

                        "[data-testid=\"stDecoration\"],"

                        +

                        "div[class*=\"viewerBadge\"]"

                        +

                        "{display:none!important;}';"

                        +

                        "document.head.appendChild(style);";



                view.evaluateJavascript(
                        css,
                        null
                );




                // MONITORAMENTO PERMANENTE DOS LINKS WHATSAPP
                String whatsappScript =


                        "(function(){"

                        +

                        "function procurarWhats(){"

                        +

                        "document.querySelectorAll('a').forEach(function(link){"

                        +

                        "var href=link.href;"

                        +

                        "if(href && (href.includes('whatsapp') || href.includes('wa.me'))){"

                        +

                        "link.onclick=function(e){"

                        +

                        "e.preventDefault();"

                        +

                        "Android.openWhatsApp(href);"

                        +

                        "return false;"

                        +

                        "};"

                        +

                        "}"

                        +

                        "});"

                        +

                        "}"

                        +

                        "setInterval(procurarWhats,1000);"

                        +

                        "})();";



                view.evaluateJavascript(
                        whatsappScript,
                        null
                );



                hideSplash();

            }



        });





        // Permite links que abrem nova janela
        webView.setWebChromeClient(
                new WebChromeClient(){


            @Override
            public boolean onCreateWindow(
                    WebView view,
                    boolean isDialog,
                    boolean isUserGesture,
                    android.os.Message resultMsg
            ){


                WebView newWebView =
                        new WebView(MainActivity.this);



                newWebView.setWebViewClient(
                        new WebViewClient(){


                    @Override
                    public boolean shouldOverrideUrlLoading(
                            WebView v,
                            String url
                    ){

                        abrirLink(url);

                        return true;

                    }



                });



                WebViewTransport transport =
                        (WebViewTransport)
                                resultMsg.obj;



                transport.setWebView(
                        newWebView
                );


                resultMsg.sendToTarget();


                return true;

            }


        });





        // Permite comunicação JavaScript -> Android

        webView.addJavascriptInterface(
                new WebAppInterface(this),
                "Android"
        );





        splashImage =
                new ImageView(this);



        splashImage.setImageResource(
                R.drawable.logo
        );


        splashImage.setScaleType(
                ImageView.ScaleType.CENTER_INSIDE
        );


        splashImage.setBackgroundColor(
                Color.WHITE
        );



        rootLayout.addView(webView);


        rootLayout.addView(
                splashImage
        );



        setContentView(
                rootLayout
        );



        webView.loadUrl(
                APP_URL
        );



        new Handler(
                Looper.getMainLooper()
        ).postDelayed(

                () -> hideSplash(),

                4000

        );


    }


    private boolean abrirLink(String url) {


        try {


            // WhatsApp
            if (url.contains("whatsapp.com")
                    || url.contains("wa.me")
                    || url.startsWith("whatsapp://")) {


                abrirWhatsApp(url);

                return true;

            }



            // Links externos
            if (url.startsWith("tel:")
                    || url.startsWith("mailto:")
                    || url.startsWith("geo:")) {


                Intent intent =
                        new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(url)
                        );


                startActivity(intent);


                return true;

            }



            // Links intent://
            if (url.startsWith("intent://")) {


                try {


                    Intent intent =
                            Intent.parseUri(
                                    url,
                                    Intent.URI_INTENT_SCHEME
                            );


                    startActivity(intent);


                } catch(Exception e) {


                    e.printStackTrace();

                }


                return true;

            }



        } catch(Exception e) {


            e.printStackTrace();

        }



        return false;

    }





    private void abrirWhatsApp(String url) {


        try {


            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW
                    );


            intent.setData(
                    Uri.parse(url)
            );


            intent.setPackage(
                    "com.whatsapp"
            );


            startActivity(intent);



        } catch(ActivityNotFoundException e) {


            try {


                Intent navegador =
                        new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(url)
                        );


                startActivity(navegador);



            } catch(Exception erro) {


                Toast.makeText(
                        MainActivity.this,
                        "Não foi possível abrir o WhatsApp",
                        Toast.LENGTH_LONG
                ).show();


            }


        }


    }





    public class WebAppInterface {


        MainActivity activity;



        WebAppInterface(
                MainActivity activity
        ) {

            this.activity = activity;

        }




        @JavascriptInterface
        public void openWhatsApp(
                String url
        ) {


            activity.runOnUiThread(
                    () -> {


                        abrirWhatsApp(url);


                    }
            );


        }


    }





    private void hideSplash() {


        if(splashImage != null
                && !isSplashHidden) {


            isSplashHidden = true;


            splashImage.setVisibility(
                    View.GONE
            );

        }


    }





    @Override
    public void onBackPressed() {


        if(webView != null
                && webView.canGoBack()) {


            webView.goBack();


        } else {


            super.onBackPressed();


        }


    }


}
