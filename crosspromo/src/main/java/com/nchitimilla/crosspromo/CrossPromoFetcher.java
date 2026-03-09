package com.nchitimilla.crosspromo;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Fetches the cross-promo ad catalogue from a remote JSON endpoint on a
 * background thread. Respects the cache TTL defined in {@link CrossPromoCache}
 * and only performs a network call when a refresh is needed.
 */
public class CrossPromoFetcher {

    private static final String TAG = "CrossPromoFetcher";
    private static final int CONNECT_TIMEOUT_MS = 5_000;
    private static final int READ_TIMEOUT_MS = 5_000;

    /**
     * Fetches the ad catalogue if the cache has expired or is empty.
     * Runs the network call on a dedicated background thread.
     *
     * @param context application context for cache access
     */
    public static void fetch(@NonNull Context context) {
        if (!CrossPromoCache.shouldRefresh(context)) {
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(CrossPromoConfig.JSON_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
                conn.setReadTimeout(READ_TIMEOUT_MS);

                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();

                CrossPromoCache.save(context, builder.toString());

            } catch (javax.net.ssl.SSLHandshakeException e) {
                Log.w(TAG, "SSL handshake failed (emulator CA issue?): " + e.getMessage());
            } catch (Exception e) {
                Log.w(TAG, "Failed to fetch cross-promo ads", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }
}
