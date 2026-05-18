package android.webkit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class WebView extends View {
    private final WebSettings mSettings = new WebSettings();

    public WebView(Context context) {
        super(context);
    }

    public WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebSettings getSettings() {
        return mSettings;
    }

    public void loadUrl(String url) {
    }

    public void loadData(String data, String mimeType, String encoding) {
    }

    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
    }

    public void setWebViewClient(WebViewClient client) {
    }

    public void setWebChromeClient(WebChromeClient client) {
    }

    public void addJavascriptInterface(Object object, String name) {
    }

    public void destroy() {
    }
}
