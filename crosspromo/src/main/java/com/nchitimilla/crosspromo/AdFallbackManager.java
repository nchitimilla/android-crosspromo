package com.nchitimilla.crosspromo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

public class AdFallbackManager {

    public static void showCrossPromo(Context context, FrameLayout container) {

        HouseAd ad = CrossPromoManager.getBestAd(context);

        if (ad == null) return;

        View banner = LayoutInflater.from(context)
                .inflate(R.layout.cross_promo_banner, container, false);

        ImageView icon = banner.findViewById(R.id.cpIcon);
        TextView title = banner.findViewById(R.id.cpTitle);
        TextView desc = banner.findViewById(R.id.cpDescription);
        Button install = banner.findViewById(R.id.cpInstall);

        title.setText(ad.getName());
        desc.setText(ad.getDescription());

        install.setOnClickListener(v ->
                CrossPromoManager.openPlayStore(context, ad.getPackageName())
        );

        loadIcon(icon, ad.getIconUrl());

        container.removeAllViews();
        container.addView(banner);

    }

    private static void loadIcon(ImageView view, String url) {

        new Thread(() -> {

            try {

                InputStream stream = new URL(url).openStream();

                Bitmap bmp = BitmapFactory.decodeStream(stream);

                view.post(() -> view.setImageBitmap(bmp));

            } catch (Exception ignored) {}

        }).start();

    }

}
