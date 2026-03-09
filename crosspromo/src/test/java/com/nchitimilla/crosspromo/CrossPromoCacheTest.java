package com.nchitimilla.crosspromo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Tests for {@link CrossPromoCache}: save/get round-trip, TTL-based refresh logic.
 *
 * @TestCaseIds TC-CP-004, TC-CP-005
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class CrossPromoCacheTest {

    private Context context;
    private static final String PREF = "crosspromo_cache";

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().clear().commit();
    }

    @Test
    public void get_returnsNullWhenEmpty() {
        assertNull(CrossPromoCache.get(context));
    }

    @Test
    public void saveAndGet_roundTrip() {
        String json = "[{\"name\":\"Test\"}]";
        CrossPromoCache.save(context, json);
        assertEquals(json, CrossPromoCache.get(context));
    }

    @Test
    public void shouldRefresh_returnsTrueWhenNoCache() {
        assertTrue(CrossPromoCache.shouldRefresh(context));
    }

    @Test
    public void shouldRefresh_returnsFalseWhenRecentlyCached() {
        CrossPromoCache.save(context, "[{\"name\":\"Test\"}]");
        assertFalse("Should not refresh when cache is fresh",
                CrossPromoCache.shouldRefresh(context));
    }

    @Test
    public void shouldRefresh_returnsTrueWhenCacheExpired() {
        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        prefs.edit()
                .putString("apps_json", "[{\"name\":\"Old\"}]")
                .putLong("last_fetch", System.currentTimeMillis() - (4 * 60 * 60 * 1000))
                .commit();

        assertTrue("Should refresh when cache is >3 hours old",
                CrossPromoCache.shouldRefresh(context));
    }

    @Test
    public void shouldRefresh_returnsFalseWhenCacheStillValid() {
        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        prefs.edit()
                .putString("apps_json", "[{\"name\":\"Recent\"}]")
                .putLong("last_fetch", System.currentTimeMillis() - (2 * 60 * 60 * 1000))
                .commit();

        assertFalse("Should not refresh when cache is <3 hours old",
                CrossPromoCache.shouldRefresh(context));
    }
}
