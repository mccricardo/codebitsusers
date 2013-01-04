package com.example.codebitsusers;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class UsersAdapter extends SimpleCursorAdapter {
    private Cursor c;
    
    private Context context;
    
    private UsersDataSource data;
    
    private GetUserPic operation;    
    
    private class GetUserPic extends AsyncTask<String, Void, String> {
	
	private final ImageView view;
	
	private Drawable pic;		
	
	public GetUserPic(ImageView _view, Context _context) {
	    super();
	    view = _view;
	    context = _context;	    
	}
	@Override
	protected String doInBackground(String... params) {
	    pic = data.getPicture(c.getString(5));
	    return null;
	}      
	
	@Override
	protected void onPostExecute(String result) {
	    view.setImageDrawable(pic);
	    view.postInvalidate();
	}			
    }
                
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
        twitter.setText(c.getString(1)); 
                
        if (operation != null) {
	    operation.cancel(true);
	}
	operation = new GetUserPic(image, context);
	operation.execute();
		
        return row;
    }           	
}