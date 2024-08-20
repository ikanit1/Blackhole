package com.example.blackhole;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppSelectionViewModel extends AndroidViewModel {

    private MutableLiveData<List<AppInfo>> installedApps = new MutableLiveData<>();
    private MutableLiveData<List<AppInfo>> selectedApps = new MutableLiveData<>(new ArrayList<>());

    public AppSelectionViewModel(Application application) {
        super(application);
    }

    public LiveData<List<AppInfo>> getInstalledApps() {
        return installedApps;
    }

    public LiveData<List<AppInfo>> getSelectedApps() {
        return selectedApps;
    }

    public void loadInstalledApps(PackageManager packageManager) {
        List<AppInfo> appInfos = new ArrayList<>();

        List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
        for (PackageInfo packageInfo : packages) {
            // Проверяем, не является ли приложение системным
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                String packageName = packageInfo.packageName;
                Drawable appIcon = packageInfo.applicationInfo.loadIcon(packageManager);
                appInfos.add(new AppInfo(appName, packageName, appIcon));
            }
        }

        // Обновляем LiveData с загруженными приложениями
        installedApps.postValue(appInfos);
    }


    public boolean isSelected(AppInfo appInfo) {
        return selectedApps.getValue().contains(appInfo);
    }

    public void toggleSelection(AppInfo appInfo) {
        List<AppInfo> currentSelection = selectedApps.getValue();
        if (currentSelection.contains(appInfo)) {
            currentSelection.remove(appInfo);
        } else {
            currentSelection.add(appInfo);
        }
        selectedApps.setValue(currentSelection);
    }

    public void saveSelectedApps() {
        List<AppInfo> selectedAppsList = selectedApps.getValue();
        if (selectedAppsList == null || selectedAppsList.isEmpty()) {
            return;
        }

        SharedPreferences preferences = getApplication().getSharedPreferences("selected_apps", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Set<String> selectedAppPackageNames = new HashSet<>();
        for (AppInfo appInfo : selectedAppsList) {
            selectedAppPackageNames.add(appInfo.getAppPackageName());
        }

        editor.putStringSet("selected_app_packages", selectedAppPackageNames);
        editor.apply();
    }

    public void restoreSelectedApps() {
        SharedPreferences preferences = getApplication().getSharedPreferences("selected_apps", Context.MODE_PRIVATE);
        Set<String> selectedAppPackageNames = preferences.getStringSet("selected_app_packages", new HashSet<>());

        List<AppInfo> restoredApps = new ArrayList<>();
        for (AppInfo appInfo : installedApps.getValue()) {
            if (selectedAppPackageNames.contains(appInfo.getAppPackageName())) {
                restoredApps.add(appInfo);
            }
        }

        selectedApps.setValue(restoredApps);
    }
}
