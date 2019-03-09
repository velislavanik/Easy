package bg.easy.demo.easy2;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {
    private WebView webView = null;
    String path = Uri.parse("https://demo.easy.bg").toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.webView = findViewById(R.id.webview);
        MyWebViewClient webViewClient = new MyWebViewClient(this);
        // Configure related browser settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true); // allow pinch to zooom
        webView.getSettings().setDisplayZoomControls(false); // disable the default zoom controls on the page
        webView.getSettings().setLoadsImagesAutomatically(true);// Sets whether the WebView should load image resources.
        webView.getSettings().setLoadWithOverviewMode(true);// Zoom out if the content width is greater than the width of the viewport
        webView.getSettings().setUseWideViewPort(true);// Enable responsive layout
       // Configure the client to use when opening URLs
        webView.setWebViewClient(webViewClient);
        // Load the initial URL
        webView.loadUrl(path);
    }

    /*The WebView maintains a browsing history just like a normal browser.
      If there is no history then it will result in the default behavior of back button i.e. exiting the app. */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
