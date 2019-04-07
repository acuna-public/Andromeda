	package pro.acuna.andromeda;
	/*
	 Created by Acuna on 25.09.2017
	*/
	
	import android.os.Build;
	import android.util.Base64;
	
	import java.io.UnsupportedEncodingException;
	
	import javax.crypto.BadPaddingException;
	import javax.crypto.Cipher;
	import javax.crypto.IllegalBlockSizeException;
	
	import pro.acuna.jabadaba.Arrays;
	
	public class Crypto {
		
		public static class EncryptException extends Exception {
			
			private EncryptException (Exception e) {
				super (e);
			}
			
			@Override
			public Exception getCause () {
				return (Exception) super.getCause ();
			}
			
		}
		
		public static class DecryptException extends Exception {
			
			private DecryptException (Exception e) {
				super (e);
			}
			
			@Override
			public Exception getCause () {
				return (Exception) super.getCause ();
			}
			
		}
		
		public static String encrypt (String value) throws EncryptException {
			
			try {
				return Base64.encodeToString (pro.acuna.jabadaba.Crypto.getCipher (Cipher.ENCRYPT_MODE, Build.FINGERPRINT).doFinal (Arrays.toByteArray (value)), Base64.NO_WRAP);
			} catch (pro.acuna.jabadaba.Crypto.CryptoException | IllegalBlockSizeException | IllegalArgumentException | BadPaddingException | UnsupportedEncodingException e) {
				throw new EncryptException (e);
			}
			
		}
		
		public static String decrypt (String value) throws DecryptException {
			
			try {
				
				byte[] bytes = (value != null ? Base64.decode (value, Base64.DEFAULT) : new byte[0]);
				return new String (pro.acuna.jabadaba.Crypto.getCipher (Cipher.DECRYPT_MODE, Build.FINGERPRINT).doFinal (bytes));
				
			} catch (pro.acuna.jabadaba.Crypto.CryptoException | IllegalBlockSizeException | IllegalArgumentException | BadPaddingException e) {
				throw new DecryptException (e);
			}
			
		}
		
	}