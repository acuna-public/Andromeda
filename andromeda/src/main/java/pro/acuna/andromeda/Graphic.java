  package pro.acuna.andromeda;
  /*
   Created by Acuna on 24.04.2018
  */
  
  import android.content.Context;
  import android.graphics.Bitmap;
  import android.graphics.BitmapFactory;
  import android.graphics.Canvas;
  import android.graphics.Paint;
  import android.graphics.PorterDuff;
  import android.graphics.PorterDuffXfermode;
  import android.graphics.Rect;
  import android.graphics.RectF;
  import android.graphics.Shader;
  import android.graphics.drawable.BitmapDrawable;
  import android.graphics.drawable.Drawable;
  import android.graphics.drawable.PictureDrawable;
  import android.graphics.drawable.shapes.Shape;
  import android.util.DisplayMetrics;
  import android.view.View;
  
  import java.io.File;
  import java.io.FileInputStream;
  import java.io.IOException;
  import java.io.InputStream;
  import java.net.URL;
  
  import pro.acuna.jabadaba.HttpRequest;
  import pro.acuna.jabadaba.Int;
  import pro.acuna.jabadaba.Net;
  import pro.acuna.jabadaba.Streams;
  import pro.acuna.jabadaba.exceptions.HttpRequestException;
  import pro.acuna.jabadaba.exceptions.OutOfMemoryException;
  
  public class Graphic {
    
    public static Bitmap toBitmap (String string) {
      return toBitmap (Arrays.base64toBytecode (string));
    }
    
    public static Bitmap toBitmap (byte[] string) {
      return BitmapFactory.decodeByteArray (string, 0, Int.size (string));
    }
    
    public static Bitmap toBitmap (Context context, int icon) {
      return BitmapFactory.decodeResource (context.getResources (), icon);
    }
    
    public static Drawable repeatedBitmap (Context context, Drawable drawable, int num) {
      
      if (num > 1) {
        
        DisplayMetrics dm = Device.getScreenSize (context);
        Bitmap bitmap = Bitmap.createScaledBitmap (toBitmap (drawable), (dm.widthPixels / num), dm.heightPixels, false);
        
        drawable = new BitmapDrawable (bitmap);
        ((BitmapDrawable) drawable).setTileModeXY (Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        
      }
      
      return drawable;
      
    }
    
    public static BitmapDrawable toBitmapDrawable (Drawable drawable) {
      return (BitmapDrawable) drawable;
    }
    
    public static Bitmap toBitmap (Context context, URL url) throws IOException, HttpRequestException, OutOfMemoryException {
      return toBitmap (context, url, 0, 0);
    }
    
    public static Bitmap toBitmap (Context context, URL url, int targetWidth, int targetHeight) throws IOException, HttpRequestException, OutOfMemoryException {
      
      HttpRequest httpConn = pro.acuna.andromeda.Net.connect (context, url.toString ());
      return toBitmap (httpConn.getInputStream (), targetWidth, targetHeight);
      
    }
    
    public static Drawable toDrawable (URL url, String userAgent) throws IOException, HttpRequestException, OutOfMemoryException {
      return toDrawable (toBitmap (url, userAgent));
    }
    
    public static Bitmap toBitmap (URL url, String userAgent, int targetWidth, int targetHeight) throws IOException, HttpRequestException, OutOfMemoryException {
      
      HttpRequest httpConn = Net.request (url, userAgent);
      return toBitmap (httpConn.getInputStream (), targetWidth, targetHeight);
      
    }
    
    public static Bitmap toBitmap (InputStream stream) throws IOException, OutOfMemoryException {
      return toBitmap (stream, 0, 0);
    }
    
    public static BitmapFactory.Options getResizeOptions (int targetWidth, int targetHeight) {
      
      BitmapFactory.Options options = new BitmapFactory.Options ();
      
      if (targetWidth > 0 && targetHeight > 0) {
        
        if (targetWidth > 0 && targetHeight > 0)
          options.inJustDecodeBounds = true;
        
        options.inSampleSize = calculateInSampleSize (options, targetWidth, targetHeight);
        options.inJustDecodeBounds = false;
        
      }
      
      return options;
      
    }
    
    public static Bitmap toBitmap (InputStream stream, int targetWidth, int targetHeight) throws IOException, OutOfMemoryException {
      return toBitmap (stream, getResizeOptions (targetWidth, targetHeight));
    }
    
    public static Bitmap toBitmap (File file) throws IOException, OutOfMemoryException {
      return toBitmap (new FileInputStream (file));
    }
    
    public static Bitmap toBitmap (InputStream stream, BitmapFactory.Options options) throws IOException, OutOfMemoryException {
      
      try {
        return BitmapFactory.decodeStream (Streams.toInputStream (stream), null, options);
      } catch (OutOfMemoryError e) {
        throw new OutOfMemoryException (e);
      }
      
    }
    
    public static Bitmap toBitmap (byte[] data, BitmapFactory.Options options) {
      return BitmapFactory.decodeByteArray (data, 0, Int.size (data), options);
    }
    
    private static int calculateInSampleSize (BitmapFactory.Options options, int reqWidth, int reqHeight) {
      
      int width = options.outWidth, height = options.outHeight, inSampleSize = 1;
      
      if (height > reqHeight || width > reqWidth) {
        
        int halfHeight = height / 2;
        int halfWidth = width / 2;
        
        while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth)
          inSampleSize *= 2;
        
      }
      
      //OS.debug (inSampleSize, width, reqWidth, height, reqHeight);
      
      return inSampleSize;
      
    }
    
    public static Drawable toDrawable (File file) {
      return Drawable.createFromPath (file.getAbsolutePath ());
    }
    
    public static Drawable toDrawable (String string) {
      return toDrawable (toBitmap (string));
    }
    
    public static Drawable toDrawable (byte[] string) {
      return toDrawable (toBitmap (string));
    }
    
    public static Bitmap toBitmap (URL url, String userAgent) throws IOException, HttpRequestException, OutOfMemoryException {
      
      HttpRequest httpConn = HttpRequest.get (url, userAgent);
      return toBitmap (httpConn.getInputStream ());
      
    }
    
    public static Drawable toDrawable (InputStream stream) {
      return Drawable.createFromStream (stream, null);
    }
    
    public static Drawable toDrawable (Context context, String image) {
      return new BitmapDrawable (context.getResources (), toBitmap (image));
    }
    
    public static Drawable toDrawable (Context context, int image) {
      return context.getResources ().getDrawable (image);
    }
    
    public static Bitmap createBitmap (int width, int height) {
      return createBitmap (width, height, Bitmap.Config.RGB_565);
    }
    
    public static Bitmap createBitmap (int width, int height, Bitmap.Config c) {
      
      try {
        return Bitmap.createBitmap (width, height, c);
      } catch (OutOfMemoryError e) {
        
        java.lang.System.gc ();
        java.lang.System.gc ();
        
        return createBitmap (width, height, c);
        
      }
      
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
    
    public static Bitmap toBitmap (Drawable image) {
      return ((BitmapDrawable) image).getBitmap ();
    }
    
    public static Bitmap toBitmap (PictureDrawable drawable) {
      
      Bitmap bitmap = createBitmap (drawable.getIntrinsicWidth (), drawable.getIntrinsicHeight (), Bitmap.Config.ARGB_8888);
      
      Canvas canvas = new Canvas (bitmap);
      canvas.drawPicture (drawable.getPicture ());
      
      return bitmap;
      
    }
    
    public static Bitmap toBitmap (Shape shape, int width, int height) {
      
      Bitmap bitmap = createBitmap (width, height, Bitmap.Config.ARGB_8888);
      
      shape.resize (width, height);
      shape.draw (new Canvas (bitmap), new Paint ());
      
      return bitmap;
      
    }
    
    public static Drawable toDrawable (Bitmap image) {
      return new BitmapDrawable (image);
    }
    
    public static Drawable resize (Drawable image, int width) {
      return resize (image, width, 0);
    }
    
    public static Drawable resize (Drawable image, int width, int height) {
      return toDrawable (resize (toBitmap (image), width, height));
    }
    
    public static Bitmap resize (Bitmap image, int width) {
      return resize (image, width, 0);
    }
    
    public static int getResizedWidth (Bitmap image, int height) {
      return getResizedWidth (height, image.getWidth (), image.getHeight ());
    }
    
    public static int getResizedWidth (int height, int imageWidth, int imageHeight) {
      return Int.prop (height, imageHeight, imageWidth);
    }
    
    public static int getResizedHeight (Bitmap image, int width) {
      return getResizedHeight (width, image.getWidth (), image.getHeight ());
    }
    
    public static int getResizedHeight (int width, int imageWidth, int imageHeight) {
      return Int.prop (width, imageWidth, imageHeight);
    }
    
    public static Bitmap resize (Bitmap image, int width, int height) {
      return new ResizeData (image).doResize (width, height);
    }
    
    public static Bitmap rounded (Drawable image) {
      
      Bitmap bitmap = toBitmap (image);
      return rounded (bitmap, bitmap.getWidth ());
      
    }
    
    public static Bitmap rounded (Drawable image, float roundPx) {
      return rounded (toBitmap (image), roundPx);
    }
    
    public static Bitmap rounded (Bitmap bitmap) {
      return rounded (bitmap, bitmap.getWidth ());
    }
    
    public static Bitmap rounded (Bitmap bitmap, float roundPx) {
      
      Bitmap output = createBitmap (bitmap.getWidth (), bitmap.getHeight (), Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas (output);
      
      final int color = 0xff424242;
      final Paint paint = new Paint ();
      final Rect rect = new Rect (0, 0, bitmap.getWidth (), bitmap.getHeight ());
      final RectF rectF = new RectF (rect);
      
      paint.setAntiAlias (true);
      canvas.drawARGB (0, 0, 0, 0);
      paint.setColor (color);
      canvas.drawRoundRect (rectF, roundPx, roundPx, paint);
      
      paint.setXfermode (new PorterDuffXfermode (PorterDuff.Mode.SRC_IN));
      canvas.drawBitmap (bitmap, rect, rect, paint);
      
      return output;
      
    }
    
    public static class ResizeData {
      
      public Bitmap image;
      public int width = 0, height = 0;
      
      private ResizeData () {}
      
      private ResizeData (Bitmap image) {
        this.image = image;
      }
      
      public ResizeData doOrientationResize (Context context) {
        
        if (Device.isPortrait (context)) {
          
          if (isPortrait (image))
            image = doResize (0, Device.getScreenHeight (context));
          else
            image = doResize (Device.getScreenWidth (context), 0);
          
        } else image = doResize (0, Device.getScreenHeight (context));
        
        return this;
        
      }
      
      public Bitmap doResize (int width, int height) {
        
        if (width <= 0)
          width = getResizedWidth (image, height);
        else if (height <= 0)
          height = getResizedHeight (image, width);
        
        this.width = width;
        this.height = height;
        
        return Bitmap.createScaledBitmap (image, width, height, true);
        
      }
      
      @Override
      public String toString () {
        return "width: " + width + ", height: " + height;
      }
      
    }
    
    public static ResizeData resize (int width, int height, int normal) {
      
      ResizeData data = new ResizeData ();
      
      if (width > height) {
        
        data.width = normal;
        data.height = Graphic.getResizedHeight (data.width, width, height);
        
      } else {
        
        data.height = normal;
        data.width = Graphic.getResizedWidth (data.height, width, height);
        
      }
      
      return data;
      
    }
    
    public static ResizeData getResizeData (Drawable image) {
      return getResizeData (toBitmap (image));
    }
    
    public static ResizeData getResizeData (Bitmap image) {
      return new ResizeData (image);
    }
    
    public static Bitmap orientationResize (Context context, Bitmap image) {
      return getResizeData (image).doOrientationResize (context).image;
    }
    
    public static Drawable orientationResize (Context context, Drawable image) {
      
      if (Device.isPortrait (context)) {
        
        if (isPortrait (image))
          image = resize (image, 0, Device.getScreenHeight (context));
        else
          image = resize (image, Device.getScreenWidth (context));
        
      } else image = resize (image, 0, Device.getScreenHeight (context));
      
      return image;
      
    }
    
    public static Bitmap transparent (Bitmap src, int color) {
      
      int width = src.getWidth ();
      int height = src.getHeight ();
      
      Bitmap b = src.copy (Bitmap.Config.ARGB_8888, true);
      b.setHasAlpha (true);
      
      int[] pixels = new int[width * height];
      src.getPixels (pixels, 0, width, 0, 0, width, height);
      
      for (int i = 0; i < width * height; i++)
        if (pixels[i] == color)
          pixels[i] = 0;
      
      b.setPixels (pixels, 0, width, 0, 0, width, height);
      
      return b;
      
    }
    
    public static Bitmap resize (String filePath, int requiredWidth) {
      
      BitmapFactory.Options optionsIn = new BitmapFactory.Options ();
      optionsIn.inJustDecodeBounds = true; // Предотвращаем утечки памяти
      
      BitmapFactory.decodeFile (filePath, optionsIn);
      
      BitmapFactory.Options optionsOut = new BitmapFactory.Options ();
      float bitmapWidth = optionsIn.outWidth;
      optionsOut.inSampleSize = Math.round (bitmapWidth / requiredWidth);
      
      return BitmapFactory.decodeFile (filePath, optionsOut);
      
    }
    
    public static boolean isLandscape (Bitmap image) {
      return isLandscape (image.getWidth (), image.getHeight ());
    }
    
    public static boolean isLandscape (int width, int height) {
      return (width > height);
    }
    
    public static boolean isPortrait (Drawable image) {
      return isPortrait (toBitmap (image));
    }
    
    public static boolean isPortrait (Bitmap image) {
      return isPortrait (image.getWidth (), image.getHeight ());
    }
    
    public static boolean isPortrait (int width, int height) {
      return (height > width);
    }
    
    public static int getPaddingTop (Context context, Bitmap image) {
      return getPaddingTop (context, image.getHeight ());
    }
    
    public static int getPaddingTop (Context context, int imageHeight) {
      
      int screenHeight = Device.getScreenHeight (context);
      return ((screenHeight / 2) - (imageHeight / 2));
      
    }
    
    public static Bitmap toBitmap (View view, Bitmap.Config config) {
      
      int width = view.getWidth ();
      int height = view.getHeight ();
      
      if (width > 0 && height > 0) {
        
        Bitmap bitmap = createBitmap (width, height, config);
        Canvas canvas = new Canvas (bitmap);
        
        view.draw (canvas);
        
        if (System.debug) canvas.drawColor (android.graphics.Color.RED, PorterDuff.Mode.DARKEN);
        
        return bitmap;
        
      } else return null;
      
    }
    
    public static BitmapFactory.Options getInfo (File file) {
      
      BitmapFactory.Options options = new BitmapFactory.Options ();
      return getInfo (file, options);
      
    }
    
    public static BitmapFactory.Options getInfo (File file, BitmapFactory.Options options) {
      
      options.inJustDecodeBounds = true;
      
      BitmapFactory.decodeFile (file.getAbsolutePath (), options);
      
      return options;
      
    }
    
  }