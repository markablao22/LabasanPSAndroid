package com.labasanps.waterelevation;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.content.Intent;
import android.widget.Toast;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        webView.addJavascriptInterface(new ScreenshotBridge(), "AndroidBridge");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (uri != null && ("http".equals(uri.getScheme()) || "https".equals(uri.getScheme()))) {
                    String host = uri.getHost();
                    if (host != null && !host.equals("localhost")) {
                        try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); }
                        catch (Exception ignored) {}
                        return true;
                    }
                }
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/index.html");
    }

    private class ScreenshotBridge {
        @JavascriptInterface
        public void saveScreenshot() {
            runOnUiThread(() -> saveWebViewScreenshot());
        }
    }

    private void saveWebViewScreenshot() {
        if (webView == null || webView.getWidth() <= 0 || webView.getHeight() <= 0) {
            Toast.makeText(this, "Hindi pa handa ang screen. Subukan ulit.", Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap = Bitmap.createBitmap(webView.getWidth(), webView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        webView.draw(canvas);

        OutputStream output = null;
        Uri imageUri = null;
        try {
            String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "LabasanPS_Screenshot_" + stamp + ".png");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/LabasanPS");
                values.put(MediaStore.Images.Media.IS_PENDING, 1);
            }
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (imageUri == null) throw new Exception("Hindi makagawa ng image file");
            output = getContentResolver().openOutputStream(imageUri);
            if (output == null) throw new Exception("Hindi mabuksan ang image file");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
            output = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues done = new ContentValues();
                done.put(MediaStore.Images.Media.IS_PENDING, 0);
                getContentResolver().update(imageUri, done, null, null);
            }
            Toast.makeText(this, "Na-save ang screenshot sa Pictures/LabasanPS", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            if (imageUri != null) getContentResolver().delete(imageUri, null, null);
            Toast.makeText(this, "Hindi na-save ang screenshot. Subukan ulit.", Toast.LENGTH_LONG).show();
        } finally {
            try { if (output != null) output.close(); } catch (Exception ignored) {}
            bitmap.recycle();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
