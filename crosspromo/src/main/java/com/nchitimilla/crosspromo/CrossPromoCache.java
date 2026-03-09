package com.nchitimilla.crosspromo;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * SharedPreferences-based cache for the cross-promo ad catalogue JSON.
 * Stores the raw JSON string and a timestamp, applying a configurable TTL
 * to decide when a network refresh is needed.
 */
public class CrossPromoCache {

    private static final String PREF = "crosspromo_cache";
    private static final String KEY_JSON = "apps_json";
    private static final String KEY_TIMESTAMP = "last_fetch";

    /** Cache time-to-live: 3 hours in milliseconds. */
    static final long CACHE_DURATION_MS = 3 * 60 * 60 * 1000L;

    /**
     * Saves the ad catalogue JSON and records the current timestamp.
     *
     * @param context application context
     * @param json    raw JSON string to cache
     */
    public static void save(@NonNull Context context, @NonNull String json) {
        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_JSON, json)
                .putLong(KEY_TIMESTAMP, System.currentTimeMillis())
                .apply();
    }

    /**
     * Returns {@code true} if the cache is empty or has exceeded {@link #CACHE_DURATION_MS}.
     *
     * @param context application context
     * @return whether a network refresh should be performed
     */
    public static boolean shouldRefresh(@NonNull Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_JSON, null);

        if (json == null) {
            return true;
        }

        long lastFetch = prefs.getLong(KEY_TIMESTAMP, 0);
        return (System.currentTimeMillis() - lastFetch) > CACHE_DURATION_MS;
    }

    /**
     * Returns the cached JSON string, or {@code null} if nothing is cached.
     *
     * @param context application context
     * @return cached JSON or null
     */
    @Nullable
    public static String get(@NonNull Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return prefs.getString(KEY_JSON, null);
    }
}
