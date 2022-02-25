  package ru.ointeractive.andromeda;
  /*
   Created by Acuna on 18.06.2018
  */
  
  import android.content.Context;
  import android.graphics.Bitmap;
  import android.graphics.drawable.BitmapDrawable;
  import android.text.Spannable;
  import android.text.SpannableString;
  import android.text.style.StyleSpan;
  import android.text.style.TypefaceSpan;
  import upl.core.Base64;
  
  import upl.json.JSONException;
  
  import java.io.ByteArrayOutputStream;
  import java.io.IOException;
  import java.io.InputStream;
  import java.io.UnsupportedEncodingException;

  import ru.ointeractive.andromeda.graphic.Graphic;
  import upl.core.HttpRequest;
  import upl.core.Int;
  import upl.core.exceptions.HttpRequestException;
  import upl.core.exceptions.OutOfMemoryException;
  
  public class Strings {
    
    public static String toString (Bitmap image) throws IOException, OutOfMemoryException {
      return toString (image, Bitmap.CompressFormat.PNG);
    }
    
    public static String toString (Bitmap image, Bitmap.CompressFormat compressFormat) throws IOException, OutOfMemoryException {
      return Strings.toString (Streams.toStream (image, compressFormat, 100));
    }
    
    public static String toString (ByteArrayOutputStream stream) {
      return toString (stream.toByteArray ());
    }
    
    public static String toString (byte[] bytes) {
      return Base64.encodeToString (bytes, Base64.DEFAULT);
    }
    
    public static String toString (Context context, int res) {
      return context.getString (res);
    }
  	
    public static String toStringRes (Context context, int res) throws IOException, OutOfMemoryException {
      return toString (context, res, Bitmap.CompressFormat.PNG);
    }
    
    public static String toString (Context context, int res, Bitmap.CompressFormat compressFormat) throws IOException, OutOfMemoryException {
      return toString ((BitmapDrawable) Graphic.toDrawable (context, res), compressFormat);
    }
    
    public static String toString (BitmapDrawable image) throws IOException, OutOfMemoryException {
      return toString (image, Bitmap.CompressFormat.PNG);
    }
    
    public static String toString (BitmapDrawable image, Bitmap.CompressFormat compressFormat) throws IOException, OutOfMemoryException {
      return toString (image, compressFormat, 100);
    }
    
    public static String toString (BitmapDrawable image, Bitmap.CompressFormat compressFormat, int quality) throws IOException, OutOfMemoryException {
      return Strings.toString (Streams.toStream (image.getBitmap (), compressFormat, quality));
    }
    
    public static String toString (HttpRequest httpConn) throws HttpRequestException, IOException, OutOfMemoryException {
      return toString (httpConn.getInputStream ());
    }
    
    public static String toString (InputStream stream) throws IOException, OutOfMemoryException {
      
      Bitmap bitmap = Graphic.toBitmap (stream);
      
      stream.close ();
      
      return toString (bitmap);
      
    }
    
    public static String base64toString (String str) throws UnsupportedEncodingException {
      
      byte[] data = Arrays.base64toBytecode (str);
      return new String (data, upl.type.String.DEF_CHARSET);
      
    }
    
    public static String toString (Spannable spannable) throws JSONException {
      return Arrays.toJSONObject (spannable).toString ();
    }
    
    public static SpannableString setSpan (SpannableString text, Object obj) {
      
      text.setSpan (obj, 0, Int.size (text), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
      return text;
      
    }
    
    public static SpannableString setFontTypeface (String text, int style) {
      return setFontTypeface (new SpannableString (text), style);
    }
    
    public static SpannableString setFontTypeface (SpannableString text, int style) {
      return setSpan (text, new StyleSpan (style));
    }
    
    public static SpannableString setFontFamily (String text, String name) {
      return setFontFamily (new SpannableString (text), name);
    }
    
    public static SpannableString setFontFamily (SpannableString text, String name) {
      return (!name.equals ("") ? setSpan (text, new TypefaceSpan (name)) : text);
    }
    
  }