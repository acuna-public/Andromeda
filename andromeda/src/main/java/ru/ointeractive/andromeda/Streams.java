	package ru.ointeractive.andromeda;
	
	import android.graphics.Bitmap;
	
	import java.io.ByteArrayInputStream;
	import java.io.ByteArrayOutputStream;
	import java.io.InputStream;
	
	public class Streams {
		
		public static InputStream toStream (Bitmap bitmap) {
			return toStream (bitmap, Bitmap.CompressFormat.PNG);
		}
		
		public static InputStream toStream (Bitmap bitmap, Bitmap.CompressFormat compressFormat) {
			return toStream (bitmap, compressFormat, 100);
		}
		
		public static InputStream toStream (Bitmap bitmap, Bitmap.CompressFormat compressFormat, int quality) {
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream ();
			bitmap.compress (compressFormat, quality, bos);
			
			return new ByteArrayInputStream (bos.toByteArray ());
			
		}
		
	}