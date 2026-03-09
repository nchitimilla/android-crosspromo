package com.nchitimilla.crosspromo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

/**
 * Displays a cross-promotion house ad banner when the primary ad network
 * (e.g. AdMob) fails to load. The banner is inflated into the provided
 * container, replacing any existing children.
 *
 * <p>The banner is designed to look like a premium app recommendation card,
 * matching the visual quality users expect from Play Store suggestions.</p>
 */
public class AdFallbackManager {

    private static final String TAG = "AdFallbackManager";
    private static final int ICON_CORNER_RADIUS_DP = 12;

    /**
     * Inflates a cross-promo banner into {@code container} using the best
     * available house ad. If no eligible ads exist, the container is left unchanged.
     *
     * @param context   context for layout inflation and Glide image loading
     * @param container the ViewGroup where the banner will be placed
     */
    public static void showCrossPromo(@NonNull Context context, @NonNull ViewGroup container) {
        HouseAd ad = CrossPromoManager.getBestAd(context);

        if (ad == null) {
            Log.w(TAG, "No cross-promo ad available");
            return;
        }

        Log.d(TAG, "Showing cross-promo for: " + ad.getName());

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

        int cornerPx = (int) (ICON_CORNER_RADIUS_DP * context.getResources().getDisplayMetrics().density);

        icon.setImageResource(android.R.drawable.sym_def_app_icon);

        Glide.with(context)
                .load(ad.getIconUrl())
                .transform(new CenterCrop(), new RoundedCorners(cornerPx))
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
                .into(icon);

        container.removeAllViews();
        container.addView(banner);
        container.setVisibility(View.VISIBLE);
    }
}
