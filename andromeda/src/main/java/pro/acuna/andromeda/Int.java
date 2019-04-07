	package pro.acuna.andromeda;
	/*
	 Created by Acuna on 11.08.2018
	*/
	
	import android.content.ContentValues;
	import android.util.SparseArray;
	import android.util.SparseIntArray;
	
	public class Int {
		
		public static int size (SparseArray array) {
			return array.size ();
		}
		
		public static int size (SparseIntArray array) {
			return array.size ();
		}
		
		public static int size (ContentValues array) {
			return array.size ();
		}
		
	}