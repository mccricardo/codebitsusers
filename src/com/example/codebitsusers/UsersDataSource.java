package com.example.codebitsusers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;

public class UsersDataSource {
    private static Context context;

    private static UsersDataSource instance;

    private SQLiteDatabase database;

    private UsersSQLiteHelper dbHelper;

    private GetData getData;

    private String[] result_columns = { UsersSQLiteHelper.COLUMN_ID,
	    UsersSQLiteHelper.COLUMN_TWITTER, UsersSQLiteHelper.COLUMN_BLOG,
	    UsersSQLiteHelper.COLUMN_NICK, UsersSQLiteHelper.COLUMN_NAME,
	    UsersSQLiteHelper.COLUMN_MD5};

    private String where = null;

    private String whereArgs[] = null;

    private String groupBy = null;

    private String having = null;

    private String order = null;

    private UsersDataSource() {
	dbHelper = new UsersSQLiteHelper(context);
	open();	
    }

    public static void setContext(Context _context) {
	context = _context;
	
	if (instance == null) {
	    instance = new UsersDataSource();
	}
	
	if (instance.database == null) {
	    instance.open();
	}
    }

    public static UsersDataSource getInstance() {		
	return instance;
    }

    public void open() throws SQLException {
	database = dbHelper.getWritableDatabase();
    }

    public void close() {
	dbHelper.close();
    }
    
    public void updateData() {
	getData = new GetData();	
	JSONArray newData = getData.syncData();
	updateDatabase(newData);
    }
    
    public Drawable getPicture(String md5) {
	getData = new GetData();	
	Drawable userPic = getData.getAvatar(md5);
	
	if (userPic == null) {
	    userPic = context.getResources().getDrawable(R.drawable.avatar_default);
	}
	
	return userPic;
    }
    
    private void updateDatabase(JSONArray usersData) {			
	ContentValues values = new ContentValues(); 
	
	database.beginTransaction();
	try {
	    int len = usersData.length();
	    for(int i = 0; i < len; ++i) {
		try {	
		    JSONObject obj = usersData.getJSONObject(i);
		
		    values.put(UsersSQLiteHelper.COLUMN_ID, obj.getInt("id"));
		    values.put(UsersSQLiteHelper.COLUMN_TWITTER, obj.getString("twitter"));
		    values.put(UsersSQLiteHelper.COLUMN_BLOG, obj.getString("blog"));
		    values.put(UsersSQLiteHelper.COLUMN_NICK, obj.getString("nick"));
		    values.put(UsersSQLiteHelper.COLUMN_NAME, obj.getString("name"));
		    values.put(UsersSQLiteHelper.COLUMN_MD5, obj.getString("md5mail"));
		
		    update(values);
		
		} catch (JSONException e) {
		 e.printStackTrace();
		}
	    }
	    database.setTransactionSuccessful();
	} finally {	    
	    database.endTransaction();
	}
    }
        
    public void update(ContentValues values) {
	database.replace(UsersSQLiteHelper.USERS_TABLE, null, values);
    }

    public void printUsers() {	
	Cursor cursor = database.query(UsersSQLiteHelper.USERS_TABLE,
		result_columns, where, whereArgs, groupBy, having, order);

	cursor.moveToFirst();
	while (cursor.moveToNext()) {
	    System.out.println(
		    "ID: " + cursor.getInt(0) + 
		    " TWITTER: " + cursor.getString(1) + 
		    " BLOG: " + cursor.getString(2) + 
		    " NICK: " + cursor.getString(3) + 
		    " NAME: " + cursor.getString (4));
	}

	cursor.close();
    }
    
    public Cursor getUsersCursor() {
	Cursor cursor = database.query(UsersSQLiteHelper.USERS_TABLE,
		result_columns, where, whereArgs, groupBy, having, order);

	return cursor;
    }
    
    public String[] getUserInfo(int userId) {
	where = UsersSQLiteHelper.COLUMN_ID + "=" + userId;
	Cursor cursor = database.query(UsersSQLiteHelper.USERS_TABLE,
		result_columns, where, whereArgs, groupBy, having, order);
	where = null;
	
	if (cursor != null)
	        cursor.moveToFirst();
	
	String res[] = {!cursor.getString(4).isEmpty() ? cursor.getString(4) : "No info", 
			!cursor.getString(1).isEmpty() ? cursor.getString(1) : "No info",
			!cursor.getString(2).isEmpty() ? cursor.getString(2) : "No info", 
			!cursor.getString(3).isEmpty() ? cursor.getString(3) : "No info"};
	return res;
    }
}
