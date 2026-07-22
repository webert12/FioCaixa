package com.fiocaixa.app;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.net.Uri;

import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;
import android.webkit.WebChromeClient;
import android.webkit.ValueCallback;

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


        WebSettings settings =
                webView.getSettings();


        settings.setJavaScriptEnabled(true);

        settings.setDomStorageEnabled(true);

        settings.setDatabaseEnabled(true);

        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        settings.setSupportMultipleWindows(true);

        settings.setAllowFileAccess(true);

        settings.setAllowContentAccess(true);


        // Atualiza sempre o sistema online
        settings.setCacheMode(
                WebSettings.LOAD_NO_CACHE
        );


        settings.setBuiltInZoomControls(false);

        settings.setDisplayZoomControls(false);

        settings.setSupportZoom(false);


        settings.setMixedContentMode(
                WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        );



        CookieManager cookies =
                CookieManager.getInstance();


        cookies.setAcceptCookie(true);

        cookies.setAcceptThirdPartyCookies(
                webView,
                true
        );



        webView.setWebViewClient(
                new WebViewClient(){



            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request
            ){

                return abrirLink(
                        request.getUrl().toString()
                );

            }



            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    String url
            ){

                return abrirLink(url);

            }




            @Override
            public void onReceivedError(
                    WebView view,
                    WebResourceRequest request,
                    WebResourceError error
            ){

                if(request.isForMainFrame()){

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
            ){

                super.onPageFinished(view,url);



                // Remove elementos do Streamlit
                String javascript =

                "var css=document.createElement('style');"

                +

                "css.innerHTML='"

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

                "document.head.appendChild(css);";



                view.evaluateJavascript(
                        javascript,
                        null
                );



                // Captura links WhatsApp criados pelo Streamlit

                view.evaluateJavascript(

                "document.querySelectorAll('a').forEach(function(a){"

                +

                "a.onclick=function(){"

                +

                "if(a.href.includes('whatsapp')){"

                +

                "Android.openWhatsApp(a.href);"

                +

                "return false;"

                +

                "}"

                +

                "}"

                +

                "});",


                null);



                hideSplash();

            }


        });




        webView.addJavascriptInterface(
                new WebAppInterface(this),
                "Android"
        );



        splashImage = new ImageView(this);


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

        rootLayout.addView(splashImage);



        setContentView(rootLayout);



        webView.loadUrl(APP_URL);



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



            // Intent Android
            if (url.startsWith("intent://")) {


                try {


                    Intent intent =
                            Intent.parseUri(
                                    url,
                                    Intent.URI_INTENT_SCHEME
                            );


                    startActivity(intent);


                } catch(Exception e){


                    e.printStackTrace();

                }



                return true;

            }



        } catch(Exception e){


            e.printStackTrace();

        }



        return false;

    }




    private void abrirWhatsApp(String url){


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



        } catch(ActivityNotFoundException e){



            try {


                Intent navegador =
                        new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(url)
                        );


                startActivity(navegador);



            } catch(Exception erro){


                Toast.makeText(
                        this,
                        "WhatsApp não encontrado",
                        Toast.LENGTH_LONG
                ).show();


            }


        }


    }




    public class WebAppInterface {


        MainActivity activity;



        WebAppInterface(
                MainActivity activity
        ){

            this.activity = activity;

        }



        @android.webkit.JavascriptInterface
        public void openWhatsApp(String url){


            activity.runOnUiThread(
                    () -> {

                        abrirWhatsApp(url);

                    }
            );


        }


    }





    private void hideSplash(){


        if(splashImage != null
                && !isSplashHidden){



            isSplashHidden = true;



            splashImage.setVisibility(
                    View.GONE
            );

        }


    }





    @Override
    public void onBackPressed(){


        if(webView != null
                && webView.canGoBack()){


            webView.goBack();


        }else{


            super.onBackPressed();


        }


    }


}
