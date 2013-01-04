package com.example.codebitsusers;

import java.util.HashMap;
import java.util.Map;

import android.graphics.drawable.Drawable;

public class PictureCache {
    private static PictureCache instance;

    private Map<String, Drawable> cache;

    private PictureCache() {
        cache = new HashMap<String, Drawable>();
    }

    public static PictureCache getInstance() {
        if (instance == null)
            instance = new PictureCache();
        return instance;
    }

    public Drawable get(String url) {
        if (cache.containsKey(url))
            return cache.get(url);
        return null;
    }

    public void put(String url, Drawable drawable) {
        cache.put(url, drawable);
    }
}
