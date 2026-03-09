package com.nchitimilla.crosspromo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class AdFallbackManager {

    private static final String TAG = "AdFallbackManager";

    public static void showCrossPromo(Context context, ViewGroup container) {

        HouseAd ad = CrossPromoManager.getBestAd(context);

        if (ad == null) {
            android.util.Log.w(TAG, "No cross-promo ad available");
            return;
        }

        android.util.Log.d(TAG, "Showing cross-promo for: " + ad.getName());

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

        // Default icon while loading
        icon.setImageResource(android.R.drawable.sym_def_app_icon);

        // Glide handles SSL + caching + threading
        Glide.with(context)
                .load(ad.getIconUrl())
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
                .into(icon);

        container.removeAllViews();
        container.addView(banner);
        container.setVisibility(View.VISIBLE);
    }

}