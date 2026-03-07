package com.nchitimilla.crosspromo;

public class HouseAd {

    private String name;
    private String description;
    private String packageName;
    private String iconUrl;
    private int priority;
    private boolean enabled;

    public HouseAd(
            String name,
            String description,
            String packageName,
            String iconUrl,
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

    public String getName() { return name; }

    public String getDescription() { return description; }

    public String getPackageName() { return packageName; }

    public String getIconUrl() { return iconUrl; }

    public int getPriority() { return priority; }

    public boolean isEnabled() { return enabled; }

}