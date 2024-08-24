package com.example.blackhole;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {NotificationRecord.class, LogEntry.class, SelectedApp.class}, version = 2) // Добавляем SelectedApp
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract SelectedAppsDao selectedAppsDao();

    public abstract NotificationDao notificationDao();

    public abstract LogEntryDao logEntryDao();  // Реализуем метод logEntryDao()

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
