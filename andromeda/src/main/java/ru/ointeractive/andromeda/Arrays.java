  package ru.ointeractive.andromeda;
  /*
   Created by Acuna on 17.07.2017
  */
  
  import android.database.Cursor;
  import android.graphics.Bitmap;
  import android.os.Bundle;
  import android.text.Spannable;
  import android.text.Spanned;
  import android.text.style.BackgroundColorSpan;
  import android.text.style.ForegroundColorSpan;
  import android.text.style.StyleSpan;
  import upl.core.Base64;
  
  import upl.json.JSONArray;
  import upl.json.JSONException;
  import upl.json.JSONObject;
  
  import java.io.ByteArrayOutputStream;
  import java.util.ArrayList;
  import java.util.List;
  
  import upl.core.Int;
  
  public class Arrays {
    
    public static byte[] toByteArray (Bitmap bitmap) {
      return toByteArray (bitmap, Bitmap.CompressFormat.PNG);
    }
    
    public static byte[] toByteArray (Bitmap bitmap, Bitmap.CompressFormat format) {
      return toByteArray (bitmap, format, 100);
    }
    
    public static byte[] toByteArray (Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
      
      ByteArrayOutputStream stream = new ByteArrayOutputStream ();
      bitmap.compress (format, quality, stream);
      
      return stream.toByteArray ();
      
    }
    
    public static JSONArray toJSONArray (Cursor cur) throws JSONException {
      
      JSONArray output = new JSONArray ();
      
      if (cur != null && cur.moveToFirst ()) {
        
        do {
          output.put (toJSONObject (cur));
        } while (cur.moveToNext ());
        
      }
      
      return output;
      
    }
    
    public static JSONObject toJSONObject (Cursor cur) throws JSONException {
      return toJSONObject (cur, new JSONObject ());
    }
    
    public static JSONObject toJSONObject (Cursor cur, JSONObject output) throws JSONException {
      
      for (int i = 0; i < cur.getColumnCount (); ++i) {
        
        String key = cur.getColumnName (i);
        
        switch (cur.getType (i)) {
          
          case Cursor.FIELD_TYPE_BLOB:
            output.put (key, new String (cur.getBlob (i)));
            break;
            
          case Cursor.FIELD_TYPE_INTEGER:
            output.put (key, cur.getLong (i));
            break;
            
          case Cursor.FIELD_TYPE_FLOAT:
            output.put (key, cur.getDouble (i));
            break;
            
          case Cursor.FIELD_TYPE_NULL:
            output.put (key, JSONObject.NULL);
            break;
            
          case Cursor.FIELD_TYPE_STRING:
            output.put (key, cur.getString (i));
            break;
          
        }
        
      }
      
      return output;
      
    }
    
    public static JSONObject toJSONObject (Bundle bundle) throws JSONException {
    	
	    JSONObject data = new JSONObject ();
      
      for (String key : bundle.keySet ())
        data.put (key, bundle.get (key));
      
      return data;
      
    }
    
    public static byte[] base64toBytecode (String str) {
      return Base64.decode (str, Base64.DEFAULT);
    }
    
    public static List<Spanned> explode (Spanned text, int indexStart, int indexEnd, List<Spanned> output) {
      
      if (indexEnd < 0) indexEnd = Int.size (text);
      Spanned word = (Spanned) text.subSequence (indexStart, indexEnd);
      
      output.add (word);
      
      return output;
      
    }
    
    public static List<Spanned> explode (String symb, Spanned text) {
      
      List<Spanned> output = new ArrayList<> ();
      String str = text.toString ();
      
      int sLength = 1;
      int indexStart = 0;
      int indexEnd = str.indexOf (symb);
      
      output = explode (text, indexStart, indexEnd, output);
      
      while (indexEnd >= 0) {
        
        indexStart = indexEnd + sLength;
        indexEnd = str.indexOf (symb, indexEnd + 1);
        
        output = explode (text, indexStart, indexEnd, output);
        
      }
      
      return output;
      
    }
    
    public static final String SPAN_TYPE_FOREGROUND = "foreground";
    public static final String SPAN_TYPE_BACKGROUND = "background";
    public static final String SPAN_TYPE_STYLE = "style";
    
    public static JSONObject toJSONObject (Spannable spannable) throws JSONException {
      
      JSONArray a = new JSONArray ();
      
      for (Object span : spannable.getSpans (0, Int.size (spannable), Object.class)) {
        
        JSONObject ret2 = new JSONObject ();
        
        if (span instanceof ForegroundColorSpan) {
          
          ret2.put ("type", SPAN_TYPE_FOREGROUND);
          ret2.put ("color", ((ForegroundColorSpan) span).getForegroundColor ());
          
        } else if (span instanceof BackgroundColorSpan) {
          
          ret2.put ("type", SPAN_TYPE_BACKGROUND);
          ret2.put ("color", ((BackgroundColorSpan) span).getBackgroundColor ());
          
        } else if (span instanceof StyleSpan) {
          
          ret2.put ("type", SPAN_TYPE_STYLE);
          ret2.put ("style", ((StyleSpan) span).getStyle ());
          
        }
        
        ret2.put ("start", spannable.getSpanStart (span));
        ret2.put ("end", spannable.getSpanEnd (span));
        ret2.put ("flags", spannable.getSpanFlags (span));
        
        a.put (ret2);
        
      }
      
      JSONObject ret = new JSONObject ();
      
      //ret.put ("text", spannable.toString ());
      
      ret.put ("spans", a);
      
      return ret;
      
    }
    
  }