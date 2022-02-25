	package ru.ointeractive.andromeda.graphic;
	/*
	 Created by Acuna on 23.04.2018.
	*/
	
	import android.graphics.Bitmap;
	import android.graphics.Paint;
	import android.graphics.PorterDuff;
	import android.graphics.PorterDuffColorFilter;
	
	public class Color {
		
		public final short Red;
		public final short Green;
		public final short Blue;
		
		public Color () {
			this (0, 0, 0);
		}
		
		public Color (int r, int g, int b) {
			
			Red = (short) (r & 0xFF);
			Green = (short) (g & 0xFF);
			Blue = (short) (b & 0xFF);
			
		}
		
		public Color (int intValue) {
			
			Red = (short) ((intValue >> 16) & 0xFF);
			Green = (short) ((intValue >> 8) & 0xFF);
			Blue = (short) (intValue & 0xFF);
			
		}
		
		int intValue () {
			return (Red << 16) + (Green << 8) + Blue;
		}
		
		@Override
		public int hashCode () {
			return intValue ();
		}
		
		@Override
		public String toString () {
			return "Color(" + Red + ", " + Green + ", " + Blue + ")";
		}
		
		public static void setColorLevel (Paint paint, Integer level) {
			
			if (level != null)
				paint.setColorFilter (new PorterDuffColorFilter (android.graphics.Color.rgb (level, level, level), PorterDuff.Mode.MULTIPLY));
			else
				paint.setColorFilter (null);
			
		}
		
		public static int rgba (Color color, int alpha) {
			return color != null
			       ? android.graphics.Color.argb (alpha, color.Red, color.Green, color.Blue)
			       : android.graphics.Color.argb (alpha, 0, 0, 0);
		}
		
		public static int rgb (Color color) {
			return color != null ? android.graphics.Color.rgb (color.Red, color.Green, color.Blue) : 0;
		}
		
		public static Color getAverageColor (Bitmap bitmap) {
			
			final int w = Math.min (bitmap.getWidth (), 7);
			final int h = Math.min (bitmap.getHeight (), 7);
			
			long r = 0, g = 0, b = 0;
			
			for (int i = 0; i < w; ++i) {
				
				for (int j = 0; j < h; ++j) {
					int color = bitmap.getPixel (i, j);
					r += color & 0xFF0000;
					g += color & 0xFF00;
					b += color & 0xFF;
				}
				
			}
			
			r /= w * h;
			g /= w * h;
			b /= w * h;
			r >>= 16;
			g >>= 8;
			
			return new Color ((int) (r & 0xFF), (int) (g & 0xFF), (int) (b & 0xFF));
			
		}
		
		public static int valueOf (String color) {
			return android.graphics.Color.parseColor (color);
		}
		
		public static String toString (int color) {
			return String.format ("#%06X", 0xFFFFFF & color);
		}
		
	}