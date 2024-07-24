package com.example.blackhole;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class AppSelectionViewModel extends ViewModel {

    private MutableLiveData<List<AppInfo>> installedApps = new MutableLiveData<>(new ArrayList<>());
    private List<AppInfo> selectedApps = new ArrayList<>();

    public LiveData<List<AppInfo>> getInstalledApps() {
        return installedApps;
    }

    public void loadInstalledApps(PackageManager packageManager) {
        new Thread(() -> {
            List<AppInfo> apps = new ArrayList<>();
            List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo packageInfo : packages) {
                if (packageManager.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                    String name = packageManager.getApplicationLabel(packageInfo).toString();
                    Drawable icon = packageManager.getApplicationIcon(packageInfo);
                    apps.add(new AppInfo(name, packageInfo.packageName, icon));
                }
            }
            installedApps.postValue(apps);
        }).start();
    }

    public void toggleAppSelection(AppInfo appInfo) {
        if (selectedApps.contains(appInfo)) {
            selectedApps.remove(appInfo);
        } else {
            selectedApps.add(appInfo);
        }
    }

    public boolean isSelected(AppInfo appInfo) {
        return selectedApps.contains(appInfo);
    }
}