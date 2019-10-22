  package pro.acuna.andromeda;
  /*
   Created by Acuna on 11.02.2019
  */
  
  import android.graphics.Bitmap;
  import android.util.LruCache;
  
  public class BitmapArray {
    
    private final static double PERC = 0.3;
    private LruCache<Long, Bitmap> mMemoryCache;
    
    public BitmapArray () {
      
      final int maxMemory = (int) (Runtime.getRuntime ().maxMemory () / 1024);
      final int cacheSize = (int) (maxMemory * PERC);
      
      mMemoryCache = new LruCache<Long, Bitmap> (cacheSize) {
        
        @Override
        protected int sizeOf (Long key, Bitmap bitmap) {
          return bitmap.getByteCount () / 1024;
        }
        
      };
      
    }
    
    public Bitmap get (long lng) {
      return mMemoryCache.get (lng);
    }
    
    public void put (Long lng, Bitmap bitmap) {
      
      if (get (lng) == null)
        mMemoryCache.put (lng, bitmap);
      
    }
    
  }