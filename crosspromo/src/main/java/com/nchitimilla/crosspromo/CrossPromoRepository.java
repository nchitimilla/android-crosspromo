package com.nchitimilla.crosspromo;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CrossPromoRepository {

    private static final String FALLBACK_JSON = "["
            + "{\"name\":\"SnapHome\","
            + "\"description\":\"Warranty Tracker\","
            + "\"package\":\"com.nchitimilla.snaphome\","
            + "\"icon\":\"https://nchitimilla.github.io/icons/snaphome.png\","
            + "\"priority\":10,"
            + "\"enabled\":true}"
            + "]";

    public static List<HouseAd> getApps(Context context) {

        List<HouseAd> apps = new ArrayList<>();

        try {

            String json = CrossPromoCache.get(context);

            if (json == null) {
                json = FALLBACK_JSON;
            }

            JSONArray array = new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                boolean enabled = obj.optBoolean("enabled", true);

                apps.add(new HouseAd(
                        obj.getString("name"),
                        obj.getString("description"),
                        obj.getString("package"),
                        obj.getString("icon"),
                        obj.getInt("priority"),
                        enabled
                ));
            }

        } catch (Exception ignored) {}

        return apps;
    }
}