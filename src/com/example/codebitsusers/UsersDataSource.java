package com.example.codebitsusers;

import java.lang.ref.WeakReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.ImageView;

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
    
    private SyncData usersOperation;
    
    private SetUserPic picsOperation;
    
    public static final int GET_NEW_DATA = 1;
    
    public static final int GET_EXISTING_DATA = 2;
    
    private class SyncData extends AsyncTask<Void, Void, Void> {
	private View view;				
	
	private SimpleCursorAdapter usersAdapter;
	
	JSONArray newData;
	
	private ListActivity activity;
	
	public SyncData(View _view, Activity _activity, Adapter adapter) {
	    super();
	    view = _view;
	    usersAdapter = (SimpleCursorAdapter) adapter;
	    activity = (ListActivity) _activity;
	}
	
	@Override
	protected void onPreExecute() {
	    view.setVisibility(View.VISIBLE);
	    view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate));
	}
			
	@Override
	protected Void doInBackground(Void... arg0) {
	    newData = getData.syncData();
	    updateDatabase(newData);
	    return null;
	}
	
	@Override
	protected void onPostExecute(Void params) {
	    String[] fromColumns = {UsersSQLiteHelper.COLUMN_NAME, UsersSQLiteHelper.COLUMN_TWITTER};
	    usersAdapter = new UsersAdapter(activity, 
				android.R.layout.simple_list_item_1, 
				getCursor(),
				fromColumns, 
				null);		    
	    
	    activity.setListAdapter(usersAdapter);
	    
	    view.clearAnimation();
	    view.setVisibility(View.INVISIBLE);	    	    
	}
    }
    
    
    private class SetUserPic extends AsyncTask<String, Void, Drawable> {	
	private final WeakReference<ImageView> imageViewReference;	
	
	private String url;
	
	public SetUserPic(ImageView _view, String _url) {
	    super();
	    imageViewReference = new WeakReference<ImageView>(_view);
	    url = _url;
	}				

	@Override
	protected Drawable doInBackground(String... params) {
	    return getPicture(url);	    
	}      
	
	@Override
	protected void onPostExecute(Drawable pic) {
	    if (imageViewReference != null) {
		ImageView imageView = imageViewReference.get();
	        if (imageView != null) {	            
	            imageView.setImageDrawable(pic);	            
	        }
	    }	    
	}			
    }

    
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
                    
    public void updateData(View view, Activity activity, Adapter usersAdapter) {
	getData = new GetData();
	
	usersOperation = new SyncData(view, activity, usersAdapter);
	usersOperation.execute();	
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
        
    public void getUsersCursor(int type, View spinner, Activity activity, UsersAdapter usersAdapter) {	
	if (type == GET_NEW_DATA) {	    	    
	    updateData(spinner, activity, usersAdapter);
	} else {
	    String[] fromColumns = {UsersSQLiteHelper.COLUMN_NAME, UsersSQLiteHelper.COLUMN_TWITTER};
	    usersAdapter = new UsersAdapter(activity, 
				android.R.layout.simple_list_item_1, 
				getCursor(),
				fromColumns, 
				null);
	    
	    ((ListActivity)activity).setListAdapter(usersAdapter);
	    
	    spinner.clearAnimation();
	    spinner.setVisibility(View.INVISIBLE);    
	}	
    }
    
    public Cursor getCursor() {	
	Cursor cursor = database.query(UsersSQLiteHelper.USERS_TABLE,
		result_columns, where, whereArgs, groupBy, having, order);
	
	return cursor;
    }
                    
    public void setPicture(String md5, ImageView image) {
	picsOperation = new SetUserPic(image, md5);
	picsOperation.execute();	
    }
    
    private Drawable getPicture(String md5) {
	getData = new GetData();	
	Drawable userPic = getData.getAvatar(md5);
	
	if (userPic == null) {
	    userPic = context.getResources().getDrawable(R.drawable.avatar_default);
	}
	
	return userPic;
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
}
