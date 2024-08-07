package com.example.blackhole;

import java.util.List;

public class AppDataHolder {
    private static AppDataHolder instance;
    private List<AppInfo> selectedApps;

    private AppDataHolder() {}

    public static synchronized AppDataHolder getInstance() {
        if (instance == null) {
            instance = new AppDataHolder();
        }
        return instance;
    }

    public List<AppInfo> getSelectedApps() {
        return selectedApps;
    }

    public void setSelectedApps(List<AppInfo> selectedApps) {
        this.selectedApps = selectedApps;
    }
}
