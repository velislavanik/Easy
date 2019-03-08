package bg.easy.demo.easy2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {

    private Activity activity = null;

    public MyWebViewClient(Activity activity) {
        this.activity = activity;
    }

    /*To keep page navigation within the WebView and hence within the app, we need to create a subclass of WebViewClient,
    and override its shouldOverrideUrlLoading(WebView webView, String url) method.
    When the shouldOverrideUrlLoading() method returns false, the URLs passed as parameter to the method is loaded
    inside the WebView instead of the browser. To load the url into the browser an intent needs to fired*/

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.contains("demo.easy.bg")) return false;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
        return true;
    }
}
