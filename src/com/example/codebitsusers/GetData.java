package com.example.codebitsusers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;

public class GetData {        
    
    private final String user = "mcc.ricardo@gmail.com";
    
    private final String password = "QC4cFxYj";
    
    private String usersUrl = "https://services.sapo.pt/Codebits/users?user="
	    + user + "&password=" + password + "&token=";

    private String tokenUrl = "https://services.sapo.pt/Codebits/gettoken?user="
	    + user + "&password=" + password;

    private String gravatarUrl = "http://www.gravatar.com/avatar/"; 
	    
    private HttpResponse response;                            
    
    public GetData() {	
    }           
    
    private String validateToken() {
	String tokenResponse = sendRequest(tokenUrl);
	JSONObject tokenData = null;
	String newToken = null;

	try {
	    tokenData = new JSONObject(tokenResponse);
	} catch (JSONException e) {
	    e.printStackTrace();
	}

	if (tokenData.has("token")) {
	    try {
		newToken = tokenData.getString("token");
	    } catch (JSONException e) {
		e.printStackTrace();
	    }
	}
	return newToken;
    }

    public JSONArray syncData() {	
	String authToken = validateToken();
	String usersResponse = sendRequest(usersUrl + authToken);
	JSONArray usersData = null;
	try {
	    usersData = new JSONArray(usersResponse);
	} catch (JSONException e) {
	    e.printStackTrace();
	}
			
	return usersData;	
    }
    	
    private String sendRequest(String url) {
	String ret = null;	
		
	HttpClient client = new DefaultHttpClient();
	HttpGet request = new HttpGet(url);
	try {
	    response = client.execute(request);
	    if (response != null) {
		ret = EntityUtils.toString(response.getEntity());
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return ret;
    }        
    
    public Drawable getAvatar(String md5) {
	PictureCache cache = PictureCache.getInstance();
	String userUrl = gravatarUrl + md5;
	
	if (cache.get(userUrl) == null) {
	    try {
		URL u = new URL(userUrl);
		HttpURLConnection huc = (HttpURLConnection)u.openConnection();
		huc.setRequestMethod("GET");
		huc.setUseCaches(true);				
		huc.connect();		
		if (huc.getResponseCode() == 404)
		    throw new Exception();		
		Drawable drawable = Drawable.createFromStream(
			(java.io.InputStream)huc.getContent(), "name");		
		cache.put(userUrl, drawable);		
	    	} catch (Exception e) {
	    	    e.printStackTrace();
	    	    return null;
	    	}
	}
	return cache.get(userUrl);
    }
}
