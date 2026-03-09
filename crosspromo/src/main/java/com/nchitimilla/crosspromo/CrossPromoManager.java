package com.nchitimilla.crosspromo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Random;

/**
 * Selects the best house ad to display based on weighted random priority,
 * filtering out the current app and disabled ads.
 */
public class CrossPromoManager {

    private static final String PLAY_STORE_MARKET_URI = "market://details?id=";
    private static final String PLAY_STORE_WEB_URI =
            "https://play.google.com/store/apps/details?id=";

    /**
     * Returns the best ad to display, or {@code null} if no eligible ads exist.
     * Filters out the current app's package and any disabled ads, then selects
     * one using weighted random based on each ad's priority value.
     *
     * @param context application context used to resolve the current package name
     * @return a {@link HouseAd} to display, or null if none are available
     */
    @Nullable
    public static HouseAd getBestAd(@NonNull Context context) {
        List<HouseAd> apps = CrossPromoRepository.getApps(context);

        String currentPackage = context.getPackageName();

        apps.removeIf(app ->
                !app.isEnabled() ||
                app.getPackageName().equals(currentPackage)
        );

        if (apps.isEmpty()) return null;

        int totalPriority = 0;
        for (HouseAd ad : apps) {
            totalPriority += ad.getPriority();
        }

        int random = new Random().nextInt(totalPriority);
        int cumulative = 0;

        for (HouseAd ad : apps) {
            cumulative += ad.getPriority();
            if (random < cumulative) {
                return ad;
            }
        }

        return apps.get(0);
    }

    /**
     * Opens the Play Store listing for the given package name.
     * Falls back to the web URL if the Play Store app is not available.
     *
     * @param context     context for starting the activity
     * @param packageName the advertised app's package name
     */
    public static void openPlayStore(@NonNull Context context, @NonNull String packageName) {
        try {
            context.startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(PLAY_STORE_MARKET_URI + packageName)
            ));
        } catch (Exception e) {
            context.startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(PLAY_STORE_WEB_URI + packageName)
            ));
        }
    }
}
