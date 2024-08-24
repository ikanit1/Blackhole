package com.example.blackhole;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotificationDao {

    @Insert
    void insertNotification(NotificationRecord notification);

    @Query("SELECT * FROM notifications WHERE notificationType = :type ORDER BY timestamp DESC")
    List<NotificationRecord> getNotificationsByType(String type);
}
