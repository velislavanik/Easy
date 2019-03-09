package bg.easy.demo.easy2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;
    // The user's current network preference setting.
    public static String sPref = null;
    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    String path = Uri.parse("https://demo.easy.bg").toString();
    String currentURL = "";
    private Activity activity = null;
    private WebView webView = null;
    private SwipeRefreshLayout swipe;
    // The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver receiver = new NetworkReceiver();

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Registers BroadcastReceiver to track network connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

        setContentView(R.layout.activity_main);
        this.webView = findViewById(R.id.webview);
        swipe = findViewById(R.id.swipe);
        swipe.setOnRefreshListener(this);
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
        //  MyWebViewClient webViewClient = new MyWebViewClient(this) {
        webView.setWebViewClient(new myWebClient2());


        //   webView.setWebViewClient(webViewClient2);
    }

    @Override
    public void onRefresh() {
        if (refreshDisplay) {
            swipe.setRefreshing(true);
            ReLoadWebView(currentURL);
        } else {
            swipe.setRefreshing(false);
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Check your internet connection and try again.");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Try Again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // finish();
                    //   startActivity(getIntent());
                }
            });

            alertDialog.show();
        }
    }

    private void ReLoadWebView(String url) {
        webView.loadUrl(url);
        swipe.setRefreshing(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregisters BroadcastReceiver when app is destroyed.
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    // Refreshes the display if the network connection and the
    // pref settings allow it.
    @Override
    public void onStart() {
        super.onStart();
        currentURL = path;

        // Gets the user's network preference settings
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieves a string value for the preferences. The second parameter
        // is the default value to use if a preference value is not found.
        sPref = sharedPrefs.getString("listPref", "Wi-Fi");

        updateConnectedFlags();

        if (refreshDisplay) {
            loadPage();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Check your internet connection and try again.");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Try Again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // finish();
                    //   startActivity(getIntent());
                }
            });

            alertDialog.show();
        }
    }

    // Checks the network connection and sets the wifiConnected and mobileConnected
    // variables accordingly.
    public void updateConnectedFlags() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connMgr != null;
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }

    // Uses AsyncTask subclass to download the XML feed from stackoverflow.com.
    public void loadPage() {
      //
        if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected))
                || ((sPref.equals(WIFI)) && (wifiConnected))) {
            // AsyncTask subclass
            //  new DownloadXmlTask().execute(URL);
            webView.loadUrl(path);
        }
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

    @Override
    // This method is used to detect back button
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }

    /*To keep page navigation within the WebView and hence within the app, we need to create a subclass of WebViewClient,
    and override its shouldOverrideUrlLoading(WebView webView, String url) method.
    When the shouldOverrideUrlLoading() method returns false, the URLs passed as parameter to the method is loaded
    inside the WebView instead of the browser. To load the url into the browser an intent needs to fired*/

    public class myWebClient2 extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            swipe.setRefreshing(false);
            currentURL = url;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
            currentURL = urlNewString;
            if (currentURL.contains("demo.easy.bg")) return false;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentURL));
            activity.startActivity(intent);
            return true;

        }

        public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
            try {
                webView.stopLoading();
            } catch (Exception e) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Check your internet connection and try again.");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Try Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                alertDialog.show();
                super.onReceivedError(webView, errorCode, description, failingUrl);

            }

         /*  if (webView.canGoBack()) {
               webView.goBack();
             }
             webView.loadUrl("about:blank");*/
        }
    }

    public class NetworkReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager conn = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert conn != null;
            NetworkInfo networkInfo = conn.getActiveNetworkInfo();


            // Checks the user prefs and the network connection. Based on the result, decides whether
            // to refresh the display or keep the current display.
            // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection and sets refreshDisplay to true
            // this  cause the display to be refreshed when the user returns to the app
            // If the setting is ANY network and there is a network connection
            // (which by process of elimination would be mobile), sets refreshDisplay to true.
            // Otherwise, the app can't download content--either because there is no network
            // connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
            // is no Wi-Fi connection. Sets refreshDisplay to false.
            refreshDisplay = (WIFI.equals(sPref) && networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) || (networkInfo != null);
           //  refreshDisplay=networkInfo.isConnected();
        }
    }
}
