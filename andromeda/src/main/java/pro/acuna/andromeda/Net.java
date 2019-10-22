  package pro.acuna.andromeda;
  /*
   Created by Acuna on 04.09.2017
  */
  
  import android.annotation.SuppressLint;
  import android.annotation.TargetApi;
  import android.content.Context;
  import android.graphics.Bitmap;
  import android.net.Uri;
  import android.os.AsyncTask;
  import android.os.Build;
  import android.webkit.CookieManager;
  import android.webkit.CookieSyncManager;
  import android.webkit.WebResourceRequest;
  import android.webkit.WebSettings;
  import android.webkit.WebView;
  import android.webkit.WebViewClient;
  
  import java.io.File;
  import java.util.Map;
  
  import pro.acuna.jabadaba.HttpRequest;
  import pro.acuna.jabadaba.exceptions.HttpRequestException;
  import pro.acuna.jabadaba.exceptions.OutOfMemoryException;
  
  public class Net {
    
    public static class Download extends AsyncTask<String, String, Void> {
      
      private pro.acuna.jabadaba.Net.ProgressListener listener;
      private long size;
      private int timeout;
      
      public Download (pro.acuna.jabadaba.Net.ProgressListener listener, long size, int timeout) {
        
        this.listener = listener;
        this.size = size;
        this.timeout = timeout;
        
      }
      
      @Override
      protected Void doInBackground (String... params) {
        
        try {
          
          pro.acuna.jabadaba.Net.download (params[0], new File (params[1]), params[2], new pro.acuna.jabadaba.Net.ProgressListener () {
            
            @Override
            public void onStart (long size) {
              listener.onStart (size);
            }
            
            @Override
            public void onProgress (long length, long size) {
              listener.onProgress (length, size);
            }
            
            @Override
            public void onError (int code, String result) {
              listener.onError (code, result);
            }
            
            @Override
            public void onFinish (int code, String result) {
              listener.onFinish (code, result);
            }
            
          }, size, timeout);
          
        } catch (HttpRequestException | OutOfMemoryException e) {
          listener.onError (0, e.getMessage ()); // TODO
        }
        
        return null;
        
      }
      
    }
    
    private static String getUserAgent (Context context) {
      return OS.getUserAgent (context, "Andromeda");
    }
    
    static HttpRequest connect (Context context, String url) throws HttpRequestException {
      return connect (context, url, pro.acuna.jabadaba.Net.defTimeout);
    }
    
    private static HttpRequest connect (Context context, String url, int timeout) throws HttpRequestException {
      return pro.acuna.jabadaba.Net.request (url, getUserAgent (context), timeout);
    }
    
    public interface WebViewListener {
      
      void onPageStarted (WebView view, Uri uri, Bitmap favicon);
      boolean onPageLoading (WebView view, Uri uri);
      void onPageFinished (WebView view, Uri uri);
      
    }
    
    public static WebView loadURL (Context context, String url) {
      return loadURL (context, url, true);
    }
    
    public static WebView loadURL (Context context, String url, boolean allowRedirects) {
      return loadURL (context, url, allowRedirects, null);
    }
    
    public static WebView loadURL (Context context, String url, boolean allowRedirects, WebViewListener listener) {
      
      WebView webView = new WebView (context);
      return loadURL (webView, url, allowRedirects, listener);
      
    }
    
    public static WebView loadURL (WebView webView, String url, boolean allowRedirects) {
      return loadURL (webView, url, allowRedirects, null);
    }
    
    @SuppressLint ("SetJavaScriptEnabled")
    public static WebView loadURL (WebView webView, String url, final boolean allowRedirects, final WebViewListener listener) {
      
      WebSettings settings = webView.getSettings ();
      
      settings.setJavaScriptEnabled (true);
      settings.setJavaScriptCanOpenWindowsAutomatically (true);
      settings.setSaveFormData (true);
      settings.setSupportZoom (true);
      
      webView.loadUrl (url);
      
      if (Build.VERSION.SDK_INT >= 21) {
        
        webView.setWebViewClient (new WebViewClient () {
          
          private boolean isRedirected = false;
          
          @Override
          public void onPageStarted (WebView view, String url, Bitmap favicon) {
            if (listener != null && !isRedirected) listener.onPageStarted (view, Uri.parse (url), favicon);
          }
          
          @Override
          @TargetApi (21)
          public boolean shouldOverrideUrlLoading (WebView view, WebResourceRequest request) {
            
            Uri url = request.getUrl ();
            boolean loaded = ((listener != null && listener.onPageLoading (view, url)) || listener == null);
            
            if (loaded) view.loadUrl (url.toString ());
            
            return loaded;
            
          }
          
          @Override
          public void onPageFinished (WebView view, String url) {
            
            if (listener != null && !isRedirected) {
              
              if (!allowRedirects) isRedirected = true;
              listener.onPageFinished (view, Uri.parse (url));
              
            }
            
          }
          
        });
        
      } else {
        
        webView.setWebViewClient (new WebViewClient () {
          
          private boolean isRedirected = false;
          
          @Override
          public void onPageStarted (WebView view, String url, Bitmap favicon) {
            if (listener != null && !isRedirected) listener.onPageStarted (view, Uri.parse (url), favicon);
          }
          
          @Override
          @SuppressWarnings ("deprecation")
          public boolean shouldOverrideUrlLoading (WebView view, String url) {
            
            Uri uri = Uri.parse (url);
            boolean loaded = ((listener != null && listener.onPageLoading (view, uri)) || listener == null);
            if (loaded) view.loadUrl (uri.toString ());
            
            return loaded;
            
          }
          
          @Override
          public void onPageFinished (WebView view, String url) {
            
            if (listener != null && !isRedirected) {
              
              if (!allowRedirects) isRedirected = true;
              listener.onPageFinished (view, Uri.parse (url));
              
            }
            
          }
          
        });
        
      }
      
      return webView;
      
    }
    
    public static String getCookies (String url) {
      return CookieManager.getInstance ().getCookie (url);
    }
    
    @SuppressWarnings ("deprecation")
    public static CookieManager clearCookies (Context context) {
      
      CookieManager cookieManager = CookieManager.getInstance ();
      
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
        
        cookieManager.removeAllCookies (null);
        cookieManager.flush ();
        
      } else {
        
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance (context);
        
        cookieSyncMngr.startSync ();
        
        cookieManager.removeAllCookie ();
        cookieManager.removeSessionCookie ();
        
        cookieSyncMngr.stopSync ();
        cookieSyncMngr.sync ();
        
      }
      
      return cookieManager;
      
    }
    
    public static CookieManager addCookies (CookieManager cookieManager, String url, Map<String, Object> cookies) {
      
      for (String key : cookies.keySet ())
        cookieManager.setCookie (url, key + "=" + cookies.get (key));
      
      return cookieManager;
      
    }
    
    @SuppressWarnings ("deprecation")
    public static CookieManager addCookies (Context context, String url, Map<String, Object> cookies) {
      
      CookieManager cookieManager = CookieManager.getInstance ();
      
      cookieManager.setAcceptCookie (true);
      
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
        
        cookieManager = addCookies (cookieManager, url, cookies);
        cookieManager.flush ();
        
      } else {
        
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance (context);
        
        cookieSyncMngr.startSync ();
  
        cookieManager = addCookies (cookieManager, url, cookies);
        
        cookieSyncMngr.stopSync ();
        cookieSyncMngr.sync ();
        
      }
      
      return cookieManager;
      
    }
    
  }