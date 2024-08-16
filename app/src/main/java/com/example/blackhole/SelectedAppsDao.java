package com.example.blackhole;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SelectedAppsDao {
    @Insert
    void insertAll(List<SelectedApp> selectedApps);

    @Query("SELECT packageName FROM selected_apps")
    List<String> getAllSelectedAppPackageNames();

    @Query("SELECT * FROM selected_apps WHERE packageName = :packageName LIMIT 1")
    SelectedApp getSelectedAppByPackageName(String packageName);  // Добавьте этот метод
}

