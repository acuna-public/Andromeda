	package pro.acuna.andromeda;
	/*
	 Created by Acuna on 17.07.2017
	*/
	
	import android.content.ContentValues;
	import android.database.Cursor;
	import android.database.sqlite.SQLiteDatabase;
	
	import java.util.ArrayList;
	import java.util.List;
	
	import pro.acuna.jabadaba.Arrays;
	import pro.acuna.jabadaba.Int;
	import pro.acuna.jabadaba.Objects;
	import pro.acuna.jabadaba.Strings;
	
	public class DB {
		
		public static String type = "litesql";
		
		public static String create (String table, List<String> items) {
			return "CREATE TABLE " + table + " (\n\t" +
									"id INTEGER PRIMARY KEY AUTOINCREMENT,\n\t" +
									Arrays.implode (",\n\t", items) +
									"\n);";
		}
		
		public static String alter (String table, String item) {
			return "ALTER TABLE " + table + " ADD COLUMN " + item + ";";
		}
		
		public static Object get (String key, Cursor cursor) {
			
			String val = getString (key, cursor);
			Object value = val;
			
			try {
				value = Integer.valueOf (val);
			} catch (NumberFormatException e) {
				// empty
			}
			
			return value;
			
		}
		
		public static String getString (String key, Cursor cursor) {
			return cursor.getString (cursor.getColumnIndex (key));
		}
		
		public static Integer getInt (String key, Cursor cursor) {
			return cursor.getInt (cursor.getColumnIndex (key));
		}
		
		public static Long getLong (String key, Cursor cursor) {
			return cursor.getLong (cursor.getColumnIndex (key));
		}
		
		public static boolean getBool (String key, Cursor cursor) {
			return Objects.toBool (getString (key, cursor));
		}
		
		private static String[] prep (String what) {
			return (!what.equals ("") ? new String[] { what } : null);
		}
		
		public static Cursor select (SQLiteDatabase db, String table, String what, List<String> where) {
			return select (db, table, prep (what), where);
		}
		
		public static Cursor select (SQLiteDatabase db, String table, String[] what, List<String> where) {
			return select (db, table, what, Arrays.implode (" AND ", where));
		}
		
		public static Cursor select (SQLiteDatabase db, String table, String what, String where) {
			return select (db, table, prep (what), where);
		}
		
		public static Cursor select (SQLiteDatabase db, String table, List<String> what, String where) {
			return select (db, table, Arrays.toStringArray (what), where);
		}
		
		public static Cursor select (SQLiteDatabase db, String table, String[] what, String where) {
			return select (db, table, what, where, null, null, null);
		}
		
		public static Cursor select (SQLiteDatabase db, String table, String what, String where, String order, String orderBy) {
			return select (db, table, prep (what), where, order, orderBy, null);
		}
		
		public static Cursor select (SQLiteDatabase db, String table, String[] what, String where, String order, String orderBy) {
			return select (db, table, what, where, order, orderBy, null);
		}
		
		public static Cursor select (SQLiteDatabase db, String table, String what, String where, Object order, Object orderBy, Object perPage) {
			return select (db, table, prep (what), where, order.toString (), orderBy.toString (), perPage.toString ());
		}
		
		public static Cursor select (SQLiteDatabase db, String table, String what, String where, String order, String orderBy, String perPage) {
			return select (db, table, prep (what), where, order, orderBy, perPage);
		}
		
		public static Cursor select (SQLiteDatabase db, String table, String[] what, String where, String order, String orderBy, String perPage) {
			return db.query (table, what, where, null, null, null, (order != null && !order.equals ("") ? order + " " + orderBy : null), perPage);
		}
		
		public static List<?> explode (String col, Cursor c) {
			
			List<Object> items = new ArrayList<> ();
			
			if (c.getCount () > 0 && c.moveToFirst ()) {
				
				do {
					items.add (get (col, c));
				} while (c.moveToNext ());
				
			}
			
			return items;
			
		}
		
		public static StringBuilder implode (String col, Cursor c, StringBuilder ids) {
			
			List<?> items = explode (col, c);
			return implode (items, ids);
			
		}
		
		public static StringBuilder implode (List<?> array) {
			return implode (array, new StringBuilder ());
		}
		
		public static StringBuilder implode (List<?> array, StringBuilder ids) {
			
			for (int i = 0; i < Int.size (array); ++i) {
				
				Object id = array.get (i);
				if (!Int.isNumeric (id.toString ())) id = "'" + id + "'";
				
				if (i > 0) ids.append (", ");
				ids.append (id);
				
			}
			
			return ids;
			
		}
		
		public static ContentValues put (Cursor cursor, ContentValues dbValues) {
			return put (cursor, dbValues, new ContentValues ());
		}
		
		public static ContentValues put (Cursor cursor, ContentValues dbValues, ContentValues dbValues2) {
			
			for (String key : dbValues.keySet ())
				dbValues2 = put (cursor, key, dbValues, dbValues2);
			
			return dbValues2;
			
		}
		
		public static ContentValues put (Cursor cursor, String key, ContentValues dbValues, ContentValues dbValues2) {
			
			Object value = dbValues.get (key), value2 = get (key, cursor);
			
			if (value2 != null && !value2.equals (value))
				dbValues2 = put (key, value, dbValues2);
			
			return dbValues2;
			
		}
		
		public static ContentValues put (String key, Object value, ContentValues dbValues) {
			
			String val = value.toString ();
			
			try {
				dbValues.put (key, Integer.parseInt (val));
			} catch (NumberFormatException e) {
				
				try {
					dbValues.put (key, Long.parseLong (val));
				} catch (NumberFormatException e2) { // TODO Проверить порядок int > long
					dbValues.put (key, val);
				}
				
			}
			
			return dbValues;
			
		}
		
		public static long insert (SQLiteDatabase db, String table, ContentValues dbValues) {
			return db.insertOrThrow (table, null, dbValues);
		}
		
		public static long update (SQLiteDatabase db, String table, ContentValues dbValues, String where) {
			return db.update (table, dbValues, where, null);
		}
		
		public static long delete (SQLiteDatabase db, String table, String where) {
			return db.delete (table, where, null);
		}
		
		public static String prepColName (String name) {
			return Strings.toLower (name.replace ("-", ""));
		}
		
	}