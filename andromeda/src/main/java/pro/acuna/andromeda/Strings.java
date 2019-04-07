	package pro.acuna.andromeda;
	/*
	 Created by Acuna on 18.06.2018
	*/
	
	import android.content.Context;
	import android.graphics.Bitmap;
	import android.graphics.drawable.BitmapDrawable;
	import android.os.Build;
	import android.text.Html;
	import android.text.Spannable;
	import android.text.SpannableString;
	import android.text.Spanned;
	import android.text.style.StyleSpan;
	import android.text.style.TypefaceSpan;
	import android.util.Base64;
	
	import org.json.JSONException;
	import org.json.JSONObject;
	
	import java.io.ByteArrayOutputStream;
	import java.io.IOException;
	import java.io.InputStream;
	import java.io.UnsupportedEncodingException;
	
	import pro.acuna.jabadaba.HttpRequest;
	import pro.acuna.jabadaba.Int;
	import pro.acuna.jabadaba.exceptions.HttpRequestException;
	import pro.acuna.jabadaba.exceptions.OutOfMemoryException;
	
	public class Strings {
		
		public static String toString (Bitmap image) {
			return toString (image, Bitmap.CompressFormat.PNG);
		}
		
		public static String toString (Bitmap image, Bitmap.CompressFormat compressFormat) {
			return Strings.toString (Streams.toStream (image, compressFormat, 100));
		}
		
		public static String toString (ByteArrayOutputStream stream) {
			return Base64.encodeToString (stream.toByteArray (), Base64.DEFAULT).trim ();
		}
		
		public static String toString (Context context, int res) {
			return toString (context, res, Bitmap.CompressFormat.PNG);
		}
		
		public static String toString (Context context, int res, Bitmap.CompressFormat compressFormat) {
			return toString ((BitmapDrawable) Graphic.toDrawable (context, res), compressFormat);
		}
		
		public static String toString (BitmapDrawable image) {
			return toString (image, Bitmap.CompressFormat.PNG);
		}
		
		public static String toString (BitmapDrawable image, Bitmap.CompressFormat compressFormat) {
			return toString (image, compressFormat, 100);
		}
		
		public static String toString (BitmapDrawable image, Bitmap.CompressFormat compressFormat, int quality) {
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
			return new String (data, pro.acuna.jabadaba.Strings.DEF_CHARSET);
			
		}
		
		public static String toString (Spannable spannable) throws JSONException {
			return Arrays.toJSONObject (spannable).toString ();
		}
		
	}