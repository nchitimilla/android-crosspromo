package com.nchitimilla.crosspromo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.List;
import java.util.Random;

public class CrossPromoManager {

    public static HouseAd getBestAd(Context context) {

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

    public static void openPlayStore(Context context, String packageName) {

        try {
            context.startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + packageName)
            ));
        } catch (Exception e) {
            context.startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)
            ));
        }
    }
}
