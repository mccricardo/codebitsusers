package com.example.codebitsusers;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class UsersAdapter extends SimpleCursorAdapter {
    private Cursor c;
    
    private Context context;
    
    private UsersDataSource data;                       
                
    public UsersAdapter(Context context, int layout, Cursor c, String[] from,
	    int[] to) {
	super(context, layout, c, from, to);
	
	this.c = c;
        this.context=context;
        
        UsersDataSource.setContext(context);
	data = UsersDataSource.getInstance();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	if(convertView == null)
            convertView = View.inflate(context, R.layout.list_row, null);
        View row = convertView;        
                        
        c.moveToPosition(position);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView twitter = (TextView) convertView.findViewById(R.id.twitter);
        ImageView image = (ImageView) convertView.findViewById(R.id.list_image);        
        
        name.setText(c.getString(4));
        twitter.setText(!c.getString(1).isEmpty() ? "@" + c.getString(1) : "No info");
        data.setPicture(c.getString(5), image);               			
	
        return row;
    }                  
}