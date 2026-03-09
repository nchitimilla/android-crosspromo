package com.nchitimilla.crosspromo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

/**
 * Tests for {@link CrossPromoRepository}: JSON parsing, fallback JSON usage.
 *
 * @TestCaseIds TC-CP-006
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class CrossPromoRepositoryTest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences("crosspromo_cache", Context.MODE_PRIVATE)
                .edit().clear().commit();
    }

    @Test
    public void getApps_noCachedJson_usesFallback() {
        List<HouseAd> apps = CrossPromoRepository.getApps(context);

        assertFalse("Fallback should provide at least one ad", apps.isEmpty());
        assertEquals("SnapHome", apps.get(0).getName());
        assertEquals("com.nchitimilla.snaphome", apps.get(0).getPackageName());
        assertTrue(apps.get(0).isEnabled());
    }

    @Test
    public void getApps_withCachedJson_parsesCachedData() {
        String json = "["
                + "{\"name\":\"AppA\",\"description\":\"Desc A\",\"package\":\"com.a\","
                + "\"icon\":\"https://example.com/a.png\",\"priority\":5,\"enabled\":true},"
                + "{\"name\":\"AppB\",\"description\":\"Desc B\",\"package\":\"com.b\","
                + "\"icon\":\"https://example.com/b.png\",\"priority\":3,\"enabled\":false}"
                + "]";
        CrossPromoCache.save(context, json);

        List<HouseAd> apps = CrossPromoRepository.getApps(context);

        assertEquals(2, apps.size());
        assertEquals("AppA", apps.get(0).getName());
        assertEquals("Desc A", apps.get(0).getDescription());
        assertEquals("com.a", apps.get(0).getPackageName());
        assertEquals(5, apps.get(0).getPriority());
        assertTrue(apps.get(0).isEnabled());

        assertEquals("AppB", apps.get(1).getName());
        assertFalse(apps.get(1).isEnabled());
    }

    @Test
    public void getApps_invalidJson_returnsEmptyList() {
        CrossPromoCache.save(context, "NOT VALID JSON!");

        List<HouseAd> apps = CrossPromoRepository.getApps(context);

        assertTrue("Invalid JSON should result in empty list", apps.isEmpty());
    }

    @Test
    public void getApps_emptyArray_returnsEmptyList() {
        CrossPromoCache.save(context, "[]");

        List<HouseAd> apps = CrossPromoRepository.getApps(context);

        assertTrue("Empty array should result in empty list", apps.isEmpty());
    }
}
