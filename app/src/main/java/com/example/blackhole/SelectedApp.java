package com.example.blackhole;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "selected_apps")
public class SelectedApp {
    @PrimaryKey
    @NonNull
    public String packageName;

    public SelectedApp(@NonNull String packageName) {
        this.packageName = packageName;
    }
}
