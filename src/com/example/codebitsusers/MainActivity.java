package com.example.codebitsusers;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

    private UsersAdapter mAdapter;
    
    private ImageView spinner;    
    
    private UsersDataSource data;               
                     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);		
	
	data.setContext(this);
	data = data.getInstance();
	
	String[] fromColumns = {UsersSQLiteHelper.COLUMN_NAME, UsersSQLiteHelper.COLUMN_TWITTER};
	mAdapter = new UsersAdapter(MainActivity.this, 
			android.R.layout.simple_list_item_1, 
			data.getCursor(),
			fromColumns, 
			null);
	setListAdapter(mAdapter);
	
	spinner = (ImageView) findViewById(R.id.spinner);	
	if(isNetworkAvailable()) {	    
	    data.getUsersCursor(UsersDataSource.GET_NEW_DATA, spinner, MainActivity.this, mAdapter);	    
	} else {
	    Toast.makeText(this, 
		    "No network available to update data", 
		    Toast.LENGTH_SHORT)
		    .show();
	    data.getUsersCursor(UsersDataSource.GET_EXISTING_DATA, spinner, MainActivity.this, mAdapter);
	}					
    }                   

    @Override
    public void onConfigurationChanged(Configuration newConfig) {	
        super.onConfigurationChanged(newConfig);
        String[] fromColumns = {UsersSQLiteHelper.COLUMN_NAME, UsersSQLiteHelper.COLUMN_TWITTER};		
	mAdapter = new UsersAdapter(MainActivity.this, 
	           android.R.layout.simple_list_item_1, data.getCursor(),
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
