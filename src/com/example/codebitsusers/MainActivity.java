package com.example.codebitsusers;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;

public class MainActivity extends ListActivity {

    private UsersAdapter mAdapter;
    
    private ImageView spinner;    
    
    private UsersDataSource data;        
    
    static final String SELECTION = "((" + 
	    UsersSQLiteHelper.COLUMN_NAME	 + " NOTNULL) AND (" +
            UsersSQLiteHelper.COLUMN_TWITTER + " != '' ))";       
     
    private SyncData operation;
    
    private class SyncData extends AsyncTask<Void, Void, Void> {
	private View view;		
	
	public SyncData(View _view) {
	    super();
	    view = _view;	    
	}
	
	@Override
	protected void onPreExecute() {
	    view.setVisibility(View.VISIBLE);
	    view.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate));
	}
	
	@Override
	protected Void doInBackground(Void... params) {	    
	    data.updateData();
	    return null;
	}
	
	@Override
	protected void onPostExecute(Void params) {	    
	    String[] fromColumns = {UsersSQLiteHelper.COLUMN_NAME, UsersSQLiteHelper.COLUMN_TWITTER};		
	    mAdapter = new UsersAdapter(MainActivity.this, 
	               android.R.layout.simple_list_item_1, 
	               data.getUsersCursor(),
	               fromColumns, 
	               null);
	    setListAdapter(mAdapter);	   
	    
	    view.clearAnimation();	    
	    view.setVisibility(View.INVISIBLE);   	    
	}	
    }       
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
		
	UsersDataSource.setContext(this);
	data = UsersDataSource.getInstance();	
	
	spinner = (ImageView) findViewById(R.id.spinner);	
	if(isNetworkAvailable()) {
	    
	    if (operation != null) {
		operation.cancel(true);
	    }
	    operation = new SyncData(spinner);
	    operation.execute();
	} else {
	    String[] fromColumns = {UsersSQLiteHelper.COLUMN_NAME, UsersSQLiteHelper.COLUMN_TWITTER};		
	    mAdapter = new UsersAdapter(MainActivity.this, 
	               android.R.layout.simple_list_item_1, data.getUsersCursor(),
	               fromColumns, null);
	    setListAdapter(mAdapter);
	    
	    spinner.setVisibility(View.INVISIBLE);
	}	
    }   
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {	
        super.onConfigurationChanged(newConfig);
        String[] fromColumns = {UsersSQLiteHelper.COLUMN_NAME, UsersSQLiteHelper.COLUMN_TWITTER};		
	mAdapter = new UsersAdapter(MainActivity.this, 
	           android.R.layout.simple_list_item_1, data.getUsersCursor(),
	               fromColumns, null);
	setListAdapter(mAdapter);        
    }
    
    @Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
	Object o = l.getItemAtPosition(position);	
	int userId = ((Cursor) o).getInt(0);
	
	Intent intent = new Intent(MainActivity.this, UserDetailActivity.class);	
	intent.putExtra("userId", userId);
	startActivity(intent);        
    }
    
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
