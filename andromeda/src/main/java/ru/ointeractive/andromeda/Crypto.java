  package ru.ointeractive.andromeda;
  /*
   Created by Acuna on 25.09.2017
  */
  
  import android.os.Build;
  
  public class Crypto {
    
    public static String encrypt (String value) throws upl.crypto.exceptions.EncryptException {
      return new upl.crypto.adapters.String (value, Build.FINGERPRINT).encrypt ();
    }
    
    public static String decrypt (String value) throws upl.crypto.exceptions.DecryptException {
      return new upl.crypto.adapters.String (value, Build.FINGERPRINT).decrypt ();
    }
    
  }