package com.example.blackhole;

import java.util.ArrayList;
import java.util.List;

public class AppDataHolder {
    private static final AppDataHolder INSTANCE = new AppDataHolder();

    private List<AppInfo> selectedApps;

    private AppDataHolder() {
        selectedApps = new ArrayList<>();
    }

    public static AppDataHolder getInstance() {
        return INSTANCE;
    }

    public List<AppInfo> getSelectedApps() {
        return selectedApps;
    }

    public void setSelectedApps(List<AppInfo> selectedApps) {
        this.selectedApps = selectedApps;
    }
}
