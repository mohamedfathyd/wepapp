package com.taleme.talemelive.app;
/*
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private WebView myWebView;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        myWebView = (WebView)findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl("http://www.aielanat.com/");
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.setWebChromeClient(new WebChromeClient()
        {
            //The undocumented magic method override
            //Eclipse will swear at you if you try to put @Override here
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                MainActivity.this.startActivityForResult(Intent.createChooser(i,"File Chooser"), FILECHOOSER_RESULTCODE);

            }

            // For Android 3.0+
            public void openFileChooser( ValueCallback uploadMsg, String acceptType ) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
         //  /*     i.setType("**");*/
        /*        MainActivity.this.startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
            }

            //For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                MainActivity.this.startActivityForResult( Intent.createChooser( i, "File Chooser" ), MainActivity.FILECHOOSER_RESULTCODE );

            }

        });
    }

    @Override
    public void onBackPressed() {
        if(myWebView.canGoBack()){
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
     //   getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
     //   if (id == R.id.action_settings) {
        //    return true;
       // }

        return super.onOptionsItemSelected(item);
    }
}
        */

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Browser;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import im.delight.android.webview.AdvancedWebView;

public class MainActivity extends AppCompatActivity {
    AdvancedWebView webView;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR = 1;
    private GoogleApiClient client;
  int   MY_PERMISSIONS_REQUEST_AUDIO =6;
    String cookies;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;
            //Check if response is positive
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FCR) {
                    if (null == mUMA) {
                        return;
                    }
                    if (intent == null) {
                        //Capture Photo if no image available
                        if (mCM != null) {
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        } else {
            if (requestCode == FCR) {
                if (null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint({"SetJavaScriptEnabled", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int permission;

        permission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (permission != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                // Show an expanation to the user *asynchronously* -- don't block
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_AUDIO);
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)
                    this, Manifest.permission.CAMERA)) {


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        15);
            }

        }
        if (ContextCompat.checkSelfPermission(this,
               Manifest.permission.MEDIA_CONTENT_CONTROL) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)
                    this, Manifest.permission.MEDIA_CONTENT_CONTROL)) {


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.MEDIA_CONTENT_CONTROL},
                        16);
            }

        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)
                    this, Manifest.permission.MODIFY_AUDIO_SETTINGS)) {


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS},
                        18);
            }

        }
        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }

        webView = findViewById(R.id.webView);
        assert webView != null;
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);

        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(0);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < 19) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.setWebViewClient(new Callback());
        webView.setWebViewClient(new WebViewClient(){



            @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("message",url);

                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL,
                            Uri.parse(url));
                    startActivity(intent);
                }
                else if(url.contains("/join/index.php?id=")){
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    Bundle bundle = new Bundle();

                    i.putExtra(Browser.EXTRA_HEADERS, bundle);
                    startActivity(i);
                }
                else if(url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                }
                return true;
        }});
        webView.loadUrl("https://talemelive.com");
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url){
                cookies = CookieManager.getInstance().getCookie(url);
                Log.d(TAG, "All the cookies in a string:" + cookies);
            }

        });
        webView.getSettings().
                setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.117 Safari/537.36");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

       webView.setWebChromeClient(new WebChromeClient() {


           @TargetApi(Build.VERSION_CODES.LOLLIPOP)
           @Override
           public void onPermissionRequest(final PermissionRequest request) {
               request.grant(request.getResources());
           }

            //For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                MainActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), FCR);
            }

            // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                MainActivity.this.startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FCR);
            }

            //For Android 4.1+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                MainActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), MainActivity.FCR);
            }

            //For Android 5.0+
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {
                if (mUMA != null) {
                    mUMA.onReceiveValue(null);
                }
                mUMA = filePathCallback;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCM);
                    } catch (IOException ex) {
                        Log.e(TAG, "Image file creation failed", ex);
                    }
                    if (photoFile != null) {
                        mCM = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }
                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");
                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, FCR);
                return true;
            }
        });
    }

    public class Callback extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse(url));
                startActivity(intent);
            }
            else if(url.contains("/join/index.php?id=")){
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
            else if(url.startsWith("http:") || url.startsWith("https:")) {
                view.loadUrl(url);
            }

            return true;
        }
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
           // Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(MainActivity.this,ofline.class));
            finish();

        }

    }

    // Create an image file
    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      Log.d("message",url);

        if (url.startsWith("tel:")) {
            Intent intent = new Intent(Intent.ACTION_DIAL,
                    Uri.parse(url));
            startActivity(intent);
        }
        else if(url.contains("/join/index.php?id=")){
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        else if(url.startsWith("http:") || url.startsWith("https:")) {
            view.loadUrl(url);
        }
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }



}