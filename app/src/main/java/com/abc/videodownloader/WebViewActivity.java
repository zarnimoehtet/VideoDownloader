package com.abc.videodownloader;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.abc.videodownloader.GenerateLink;
import com.abc.videodownloader.R;

import java.io.File;

public class WebViewActivity  extends AppCompatActivity {

    private static final String target_url = "https://m.facebook.com/";
    private static WebView webView;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Bundle b = getIntent().getExtras();
        String id = b.getString("id");

        EditText editText = findViewById(R.id.urlbar);
       editText.setText(id+"");

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "FBDownloader");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:(function() { "
                        + "var el = document.querySelectorAll('div[data-sigil]');"
                        + "for(var i=0;i<el.length; i++)"
                        + "{"
                        + "var sigil = el[i].dataset.sigil;"
                        + "if(sigil.indexOf('inlineVideo') > -1){"
                        + "delete el[i].dataset.sigil;"
                        + "var jsonData = JSON.parse(el[i].dataset.store);"
                        + "el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\");');"
                        + "}" + "}" + "})()");
            }

        @Override
        public void onLoadResource(WebView view, String url) {
            webView.loadUrl("javascript:(function prepareVideo() { "
                    + "var el = document.querySelectorAll('div[data-sigil]');"
                    + "for(var i=0;i<el.length; i++)"
                    + "{"
                    + "var sigil = el[i].dataset.sigil;"
                    + "if(sigil.indexOf('inlineVideo') > -1){"
                    + "delete el[i].dataset.sigil;"
                    + "console.log(i);"
                    + "var jsonData = JSON.parse(el[i].dataset.store);"
                    + "el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');"
                    + "}" + "}" + "})()");
            webView.loadUrl("javascript:( window.onload=prepareVideo;"
                    + ")()");
        }
    });
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        CookieSyncManager.getInstance().startSync();

        webView.loadUrl(id);
    }

    @JavascriptInterface
    public void processVideo(final String vidData, final String vidID) {
        try {
//            String mBaseFolderPath = android.os.Environment
//                    .getExternalStorageDirectory()
//                    + File.separator
//                    + "FacebookVideos" + File.separator;
//            if (!new File(mBaseFolderPath).exists()) {
//                new File(mBaseFolderPath).mkdir();
//            }
//            String mFilePath = "file://" + mBaseFolderPath + "/" + vidID + ".mp4";

            Uri downloadUri = Uri.parse(vidData);

            Log.e("DownloadURI", downloadUri.toString());
            Intent i = new Intent(WebViewActivity.this, GenerateLink.class);
            i.putExtra("title","default");
            i.putExtra("sd",downloadUri.toString());
            i.putExtra("hd","false");
            startActivity(i);
           // finish();

//            DownloadManager.Request req = new DownloadManager.Request(downloadUri);
//            req.setDestinationUri(Uri.parse(mFilePath));
//            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//            DownloadManager dm = (DownloadManager) getSystemService(getApplicationContext().DOWNLOAD_SERVICE);
//            dm.enqueue(req);
//            Toast.makeText(this,"Download Started", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this,"Download Failed: "+e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed()
    {
        if (webView.canGoBack()) {
            webView.goBack();

        } else
            super.onBackPressed();
    }

    }

