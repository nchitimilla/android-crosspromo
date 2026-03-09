package com.nchitimilla.crosspromo;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the cross-promo ad catalogue from cached JSON. Falls back to a
 * built-in JSON string when no cache is available, ensuring at least one
 * house ad is always returned.
 */
public class CrossPromoRepository {

    private static final String TAG = "CrossPromoRepository";

    private static final String FALLBACK_JSON = "["
            + "{\"name\":\"SnapHome\","
            + "\"description\":\"Warranty Tracker\","
            + "\"package\":\"com.nchitimilla.snaphome\","
            + "\"icon\":\"https://nchitimilla.github.io/icons/snaphome.png\","
            + "\"priority\":10,"
            + "\"enabled\":true}"
            + "]";

    /**
     * Returns the list of house ads parsed from the cache (or fallback JSON).
     * Returns an empty list if the JSON is malformed.
     *
     * @param context application context for cache access
     * @return mutable list of {@link HouseAd} entries
     */
    @NonNull
    public static List<HouseAd> getApps(@NonNull Context context) {
        List<HouseAd> apps = new ArrayList<>();

        try {
            String json = CrossPromoCache.get(context);
            if (json == null) {
                json = FALLBACK_JSON;
            }

            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                apps.add(new HouseAd(
                        obj.getString("name"),
                        obj.getString("description"),
                        obj.getString("package"),
                        obj.getString("icon"),
                        obj.getInt("priority"),
                        obj.optBoolean("enabled", true)
                ));
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to parse cross-promo JSON", e);
        }

        return apps;
    }
}
