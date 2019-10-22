  package pro.acuna.andromeda;
  /*
   Created by Acuna on 11.05.2017
  */
  
  import android.app.ProgressDialog;
  import android.content.Context;
  import android.os.AsyncTask;
  
  import java.util.List;
  
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
    
    @Override
    protected void onPreExecute () {
      
      progress = ProgressDialog.show (context, null, mess);
      
      progress.setCancelable (canceable);
      progress.show ();
      
      super.onPreExecute ();
      
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
      
      progress.dismiss ();
      
      if (listenerJSONArray != null)
        listenerJSONArray.onPostExecute (result);
      else if (listenerJSONObject != null)
        listenerJSONObject.onPostExecute (result);
      else
        listener.onPostExecute (result);
      
    }
    
  }