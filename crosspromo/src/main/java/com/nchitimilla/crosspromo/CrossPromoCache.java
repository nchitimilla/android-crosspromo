package com.nchitimilla.crosspromo;

import android.content.Context;
import android.content.SharedPreferences;

public class CrossPromoCache {

    private static final String PREF = "crosspromo_cache";
    private static final String KEY_JSON = "apps_json";
    private static final String KEY_TIMESTAMP = "last_fetch";

    private static final long CACHE_DURATION = 3 * 60 * 60 * 1000;

    public static void save(Context context, String json) {

        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        prefs.edit()
                .putString(KEY_JSON, json)
                .putLong(KEY_TIMESTAMP, System.currentTimeMillis())
                .apply();
    }

    public static boolean shouldRefresh(Context context) {

        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        String json = prefs.getString(KEY_JSON, null);

        if (json == null) {
            return true;
        }

        long lastFetch = prefs.getLong(KEY_TIMESTAMP, 0);
        long now = System.currentTimeMillis();
        long diff = now - lastFetch;
        return diff > CACHE_DURATION;
    }

    public static String get(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return prefs.getString(KEY_JSON, null);
    }
}