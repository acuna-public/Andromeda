  package ru.ointeractive.andromeda;
  /*
   Created by Acuna on 11.05.2017
  */
  
  import android.app.ProgressDialog;
  import android.content.Context;
  import android.os.AsyncTask;

  import upl.util.List;

  public class AsyncTaskLoading extends AsyncTask<Object, Object, List<Object>> {
    
    private String mess;
    private Context context;
    private AsyncTaskInterface listener;
    private AsyncTaskInterfaceJSONArray listenerJSONArray;
    private AsyncTaskInterfaceJSONObject listenerJSONObject;
    private boolean canceable;
    
    private ProgressDialog progress;
    
    public interface AsyncTaskInterface {
      
      List<Object> doInBackground (Object... params);
      void onPostExecute (List<Object> result);
      
    }
    
    public interface AsyncTaskInterfaceJSONArray {
      
      List<Object> doInBackground ();
      void onPostExecute (List<Object> result);
      
    }
    
    public interface AsyncTaskInterfaceJSONObject {
      
      List<Object> doInBackground ();
      void onPostExecute (List<Object> result);
      
    }
    
    public AsyncTaskLoading (Context context, int mess, AsyncTaskInterface listener) {
      this (context, context.getString (mess), listener, true);
    }
    
    public AsyncTaskLoading (Context context, int mess, AsyncTaskInterface listener, boolean canceable) {
      this (context, context.getString (mess), listener, canceable);
    }
    
    public AsyncTaskLoading (Context context, int mess, AsyncTaskInterfaceJSONArray listener) {
      this (context, context.getString (mess), listener, true);
    }
    
    public AsyncTaskLoading (Context context, int mess, AsyncTaskInterfaceJSONObject listener) {
      this (context, context.getString (mess), listener, true);
    }
    
    public AsyncTaskLoading (Context context, String mess, AsyncTaskInterfaceJSONArray listener) {
      this (context, mess, listener, true);
    }
    
    public AsyncTaskLoading (Context context, String mess, AsyncTaskInterface listener) {
      this (context, mess, listener, true);
    }
    
    public AsyncTaskLoading (Context context, String mess, AsyncTaskInterface listener, boolean canceable) {
      
      this.context = context;
      this.listener = listener;
      this.mess = mess;
      this.canceable = canceable;
      
    }
    
    public AsyncTaskLoading (Context context, String mess, AsyncTaskInterfaceJSONArray listener, boolean canceable) {
      
      this.context = context;
      this.listenerJSONArray = listener;
      this.mess = mess;
      this.canceable = canceable;
      
    }
    
    public AsyncTaskLoading (Context context, String mess, AsyncTaskInterfaceJSONObject listener, boolean canceable) {
      
      this.context = context;
      this.listenerJSONObject = listener;
      this.mess = mess;
      this.canceable = canceable;
      
    }
    
    private boolean process = true;
    
    public AsyncTaskLoading process (boolean proc) {
      
      this.process = proc;
      return this;
      
    }
    
    @Override
    protected void onPreExecute () {
      
      if (process) {
        
        progress = ProgressDialog.show (context, null, mess);
  
        progress.setCancelable (canceable);
        progress.show ();
  
      }
      
    }
    
    @Override
    protected List<Object> doInBackground (Object... params) {
      
      if (listenerJSONArray != null)
        return listenerJSONArray.doInBackground ();
      else if (listenerJSONObject != null)
        return listenerJSONObject.doInBackground ();
      else
        return listener.doInBackground (params);
      
    }
    
    @Override
    protected void onPostExecute (List<Object> result) {
      
      if (progress != null) progress.dismiss ();
      
      if (listenerJSONArray != null)
        listenerJSONArray.onPostExecute (result);
      else if (listenerJSONObject != null)
        listenerJSONObject.onPostExecute (result);
      else
        listener.onPostExecute (result);
      
    }
    
  }