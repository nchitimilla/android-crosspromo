package com.nchitimilla.crosspromo;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CrossPromoFetcher {

    public static void fetch(Context context) {

        if (!CrossPromoCache.shouldRefresh(context)) {
            return;
        }

        new Thread(() -> {

            try {

                URL url = new URL(CrossPromoConfig.JSON_URL);

                HttpURLConnection conn =
                        (HttpURLConnection) url.openConnection();

                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(conn.getInputStream())
                        );

                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {

                    builder.append(line);

                }

                reader.close();

                CrossPromoCache.save(context, builder.toString());

            } catch (Exception e) {

                e.printStackTrace();

            }

        }).start();

    }

}