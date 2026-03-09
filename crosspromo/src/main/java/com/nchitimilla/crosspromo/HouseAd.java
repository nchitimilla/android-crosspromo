package com.nchitimilla.crosspromo;

import androidx.annotation.NonNull;

/**
 * Immutable data model representing a single house ad entry in the
 * cross-promotion catalogue. Each ad has a display name, description,
 * target package, icon URL, selection priority, and enabled flag.
 */
public class HouseAd {

    private final String name;
    private final String description;
    private final String packageName;
    private final String iconUrl;
    private final int priority;
    private final boolean enabled;

    /**
     * Creates a new house ad entry.
     *
     * @param name        display name of the advertised app
     * @param description short description shown on the banner
     * @param packageName Play Store package name
     * @param iconUrl     URL to the app icon image
     * @param priority    selection weight (higher = more likely to be shown)
     * @param enabled     whether this ad is eligible for display
     */
    public HouseAd(
            @NonNull String name,
            @NonNull String description,
            @NonNull String packageName,
            @NonNull String iconUrl,
            int priority,
            boolean enabled
    ) {
        this.name = name;
        this.description = description;
        this.packageName = packageName;
        this.iconUrl = iconUrl;
        this.priority = priority;
        this.enabled = enabled;
    }

    /** Returns the display name of the advertised app. */
    @NonNull
    public String getName() { return name; }

    /** Returns a short description of the advertised app. */
    @NonNull
    public String getDescription() { return description; }

    /** Returns the Play Store package name. */
    @NonNull
    public String getPackageName() { return packageName; }

    /** Returns the URL of the app icon image. */
    @NonNull
    public String getIconUrl() { return iconUrl; }

    /** Returns the selection weight (higher = more likely to be shown). */
    public int getPriority() { return priority; }

    /** Returns whether this ad is eligible for display. */
    public boolean isEnabled() { return enabled; }
}
