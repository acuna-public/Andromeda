  package pro.acuna.andromeda;
  /*
   Created by Acuna on 24.10.2018
  */
  
  import android.graphics.Bitmap;
  
  import java.io.ByteArrayOutputStream;
  
  public class Streams {
    
    public static ByteArrayOutputStream toStream (Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
      
      ByteArrayOutputStream stream = new ByteArrayOutputStream ();
      image.compress (compressFormat, quality, stream);
      
      return stream;
      
    }
    
  }