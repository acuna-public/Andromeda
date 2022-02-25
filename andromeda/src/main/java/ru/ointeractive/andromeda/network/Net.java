  package ru.ointeractive.andromeda.network;
  /*
   Created by Acuna on 04.09.2017
  */
  
  import android.content.Context;
  import android.graphics.Bitmap;
  import android.net.Uri;
  import android.os.AsyncTask;
  import android.os.Build;
  import android.webkit.CookieManager;
  import android.webkit.CookieSyncManager;
  import android.webkit.WebView;
  
  import java.util.ArrayList;
  import java.util.List;
  import java.util.Map;

  import ru.ointeractive.andromeda.OS;
  import upl.core.File;
  import upl.core.HttpRequest;
  import upl.core.exceptions.HttpRequestException;
  import upl.core.Int;
  import upl.core.exceptions.OutOfMemoryException;
  
  public class Net {
    
    public static class Download extends AsyncTask<String, String, Void> {
      
      private upl.core.Net.ProgressListener listener;
      private long size;
      private int timeout, mCode;
      private String mResult;
      private List<Exception> errors = new ArrayList<> ();
      
      public Download (upl.core.Net.ProgressListener listener, long size, int timeout) {
        
        this.listener = listener;
        this.size = size;
        this.timeout = timeout;
        
      }
      
      @Override
      protected Void doInBackground (String... params) {
        
        try {
          
          upl.core.Net.download (params[0], new File (params[1]), params[2], new upl.core.Net.ProgressListener () {
            
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
              
              mCode = code;
              mResult = result;
              
            }
            
          }, size, timeout);
          
        } catch (HttpRequestException | OutOfMemoryException e) {
          errors.add (e);
        }
        
        return null;
        
      }
      
      @Override
      protected void onPostExecute (Void result) {
        
        if (Int.size (errors) > 0) {
          
          for (Exception e : errors) {
            
            if (e instanceof HttpRequestException)
              listener.onError (((HttpRequestException) e).getHTTPCode (), ((HttpRequestException) e).getMessage ());
            else
              listener.onError (0, e.getMessage ());
            
          }
          
        } else listener.onFinish (mCode, mResult);
        
      }
      
    }
    
    private static String getUserAgent (Context context) {
      return OS.getUserAgent (context, "Andromeda");
    }
    
    public static HttpRequest connect (Context context, String url) throws HttpRequestException {
      return connect (context, url, HttpRequest.defTimeout);
    }
    
    private static HttpRequest connect (Context context, String url, int timeout) throws HttpRequestException {
      return new HttpRequest (HttpRequest.METHOD_GET, url).setUserAgent (getUserAgent (context)).setTimeout (timeout);
    }
    
    public interface WebViewListener {
      
      void onPageStarted (WebView view, Uri uri, Bitmap favicon);
      boolean onPageLoading (WebView view, Uri uri);
      void onPageFinished (WebView view, Uri uri);
      
    }
    
    public static String getCookies (String url) {
      return CookieManager.getInstance ().getCookie (url);
    }
    
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
    
    public static CookieManager addCookies (Context context, String url, Map<String, Object> cookies) {
      
      CookieManager cookieManager = CookieManager.getInstance ();
      
      cookieManager.setAcceptCookie (true);
      
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
        
        addCookies (cookieManager, url, cookies);
        cookieManager.flush ();
        
      } else {
        
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance (context);
        
        cookieSyncMngr.startSync ();
  
        addCookies (cookieManager, url, cookies);
        
        cookieSyncMngr.stopSync ();
        cookieSyncMngr.sync ();
        
      }
      
      return cookieManager;
      
    }
    
  }