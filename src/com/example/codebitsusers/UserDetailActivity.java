package com.example.codebitsusers;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class UserDetailActivity extends Activity {
    private int userId;
    
    private UsersDataSource data;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_user_detail);		
	
	Bundle extras = getIntent().getExtras(); 	
	userId = extras.getInt("userId");
	
	UsersDataSource.setContext(this);
	data = UsersDataSource.getInstance();
	
	setUpUI();
    }
    
    private void setUpUI() {
	String userInfo[] = data.getUserInfo(userId);
		
	TextView name = (TextView) findViewById(R.id.name);
	TextView twitter = (TextView) findViewById(R.id.twitter);
	TextView blog = (TextView) findViewById(R.id.blog);
	TextView nick = (TextView) findViewById(R.id.nick);
	
	name.setText("Name: " + userInfo[0]);
	twitter.setText("Twitter: " + userInfo[1]);
	blog.setText("Blog: " + userInfo[2]);
	nick.setText("Nick: " + userInfo[3]);
    }
}
