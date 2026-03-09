package com.nchitimilla.crosspromo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests for {@link CrossPromoManager}: ad selection logic, current-app filtering,
 * weighted random distribution, and disabled-ad filtering.
 *
 * @TestCaseIds TC-CP-003, TC-CP-007, TC-CP-008
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class CrossPromoManagerTest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences("crosspromo_cache", Context.MODE_PRIVATE)
                .edit().clear().commit();
    }

    @Test
    public void getBestAd_excludesCurrentAppPackage() throws Exception {
        String currentPkg = context.getPackageName();

        JSONArray arr = new JSONArray();
        arr.put(makeAd("Self", "This app", currentPkg, 10, true));
        arr.put(makeAd("Other", "Another app", "com.other.app", 10, true));

        cacheJson(arr.toString());

        HouseAd ad = CrossPromoManager.getBestAd(context);
        assertNotNull("Should return an ad", ad);
        assertEquals("com.other.app", ad.getPackageName());
    }

    @Test
    public void getBestAd_filtersOutDisabledAds() throws Exception {
        JSONArray arr = new JSONArray();
        arr.put(makeAd("Disabled", "Off", "com.disabled", 100, false));
        arr.put(makeAd("Enabled", "On", "com.enabled", 1, true));

        cacheJson(arr.toString());

        HouseAd ad = CrossPromoManager.getBestAd(context);
        assertNotNull(ad);
        assertEquals("com.enabled", ad.getPackageName());
    }

    @Test
    public void getBestAd_returnsNullWhenAllFiltered() throws Exception {
        String currentPkg = context.getPackageName();

        JSONArray arr = new JSONArray();
        arr.put(makeAd("Self", "This app", currentPkg, 10, true));
        arr.put(makeAd("Disabled", "Off", "com.disabled", 10, false));

        cacheJson(arr.toString());

        HouseAd ad = CrossPromoManager.getBestAd(context);
        assertNull("Should be null when all ads are filtered out", ad);
    }

    @Test
    public void getBestAd_weightedRandomFavorsHigherPriority() throws Exception {
        JSONArray arr = new JSONArray();
        arr.put(makeAd("High", "High prio", "com.high", 90, true));
        arr.put(makeAd("Low", "Low prio", "com.low", 10, true));

        cacheJson(arr.toString());

        Map<String, Integer> counts = new HashMap<>();
        counts.put("com.high", 0);
        counts.put("com.low", 0);

        int iterations = 1000;
        for (int i = 0; i < iterations; i++) {
            HouseAd ad = CrossPromoManager.getBestAd(context);
            assertNotNull(ad);
            counts.put(ad.getPackageName(), counts.get(ad.getPackageName()) + 1);
        }

        int highCount = counts.get("com.high");
        assertTrue("High-priority ad should be picked >60% of the time (was " + highCount + "/1000)",
                highCount > 600);
    }

    @Test
    public void getBestAd_singleEnabledAd_alwaysReturnsSame() throws Exception {
        JSONArray arr = new JSONArray();
        arr.put(makeAd("Solo", "Only one", "com.solo", 5, true));

        cacheJson(arr.toString());

        for (int i = 0; i < 10; i++) {
            HouseAd ad = CrossPromoManager.getBestAd(context);
            assertNotNull(ad);
            assertEquals("com.solo", ad.getPackageName());
        }
    }

    private JSONObject makeAd(String name, String desc, String pkg, int priority, boolean enabled) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("description", desc);
        obj.put("package", pkg);
        obj.put("icon", "https://example.com/icon.png");
        obj.put("priority", priority);
        obj.put("enabled", enabled);
        return obj;
    }

    private void cacheJson(String json) {
        SharedPreferences prefs = context.getSharedPreferences("crosspromo_cache", Context.MODE_PRIVATE);
        prefs.edit()
                .putString("apps_json", json)
                .putLong("last_fetch", System.currentTimeMillis())
                .apply();
    }
}
