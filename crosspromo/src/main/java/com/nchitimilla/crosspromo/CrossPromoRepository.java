package com.nchitimilla.crosspromo;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CrossPromoRepository {

    public static List<HouseAd> getApps(Context context) {

        List<HouseAd> apps = new ArrayList<>();

        try {

            String json = CrossPromoCache.get(context);

            if (json == null) return apps;

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