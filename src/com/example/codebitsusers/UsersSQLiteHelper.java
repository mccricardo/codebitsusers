package com.example.codebitsusers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UsersSQLiteHelper extends SQLiteOpenHelper {
    public static final String USERS_TABLE = "users_table";
    
    public static final String COLUMN_ID = "_id";
    
    public static final String COLUMN_TWITTER = "twitter";
    
    public static final String COLUMN_BLOG = "blog";
    
    public static final String COLUMN_NICK = "nick";
    
    public static final String COLUMN_NAME = "name";
    
    public static final String COLUMN_MD5 = "md5";
    
    private static final String DATABASE_NAME = "codebits_users.db";
    
    private static final int DATABASE_VERSION = 10;
    
    private static final String CREATE_USERS_TABLE = "create table " + USERS_TABLE
            + "( " + COLUMN_ID + " integer, " + 
	             COLUMN_TWITTER + " text, " + 
	             COLUMN_BLOG + " text, " + 
	             COLUMN_NICK + " text, " + 
	             COLUMN_NAME + " text, " + 
	             COLUMN_MD5 + " text, " + 
	             "PRIMARY KEY (" + COLUMN_ID + "));"; 

    public UsersSQLiteHelper(Context context) {
	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {	
	db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {	
	db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);	
	onCreate(db);	
    }        
}
