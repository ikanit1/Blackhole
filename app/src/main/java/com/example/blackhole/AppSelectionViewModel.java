package com.example.blackhole;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class AppSelectionViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final MutableLiveData<List<AppInfo>> installedApps = new MutableLiveData<>();
    private final MutableLiveData<List<AppInfo>> selectedApps = new MutableLiveData<>(new ArrayList<>());

    public AppSelectionViewModel(Application application) {
        super(application);
        db = AppDatabase.getDatabase(application);
    }

    public LiveData<List<AppInfo>> getInstalledApps() {
        return installedApps;
    }

    public LiveData<List<AppInfo>> getSelectedApps() {
        return selectedApps;
    }

    public void loadInstalledApps(PackageManager pm) {
        new Thread(() -> {
            List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            List<AppInfo> appInfos = new ArrayList<>();
            for (ApplicationInfo app : apps) {
                // Исключаем системные приложения
                if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    Drawable icon = pm.getApplicationIcon(app);
                    appInfos.add(new AppInfo(app.loadLabel(pm).toString(), app.packageName, icon));
                }
            }
            installedApps.postValue(appInfos);
        }).start();
    }

    public void toggleAppSelection(AppInfo appInfo) {
        List<AppInfo> currentSelection = selectedApps.getValue();
        if (currentSelection != null) {
            if (currentSelection.contains(appInfo)) {
                currentSelection.remove(appInfo);
            } else {
                currentSelection.add(appInfo);
            }
            selectedApps.postValue(currentSelection);
        }
    }

    public boolean isSelected(AppInfo appInfo) {
        List<AppInfo> currentSelection = selectedApps.getValue();
        return currentSelection != null && currentSelection.contains(appInfo);
    }

    public void saveSelectedApps() {
        new Thread(() -> {
            List<AppInfo> apps = selectedApps.getValue();
            if (apps != null) {
                List<SelectedApp> selectedAppEntities = new ArrayList<>();
                for (AppInfo app : apps) {
                    selectedAppEntities.add(new SelectedApp(app.getAppPackageName()));
                }
                db.selectedAppsDao().insertAll(selectedAppEntities);
            }
        }).start();
    }
}
