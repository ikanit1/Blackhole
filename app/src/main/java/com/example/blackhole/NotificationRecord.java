package com.example.blackhole;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class NotificationRecord {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private long timestamp;
    private String appName;
    private String messageTitle;
    private String messageBody;
    private String notificationType; // "push" или "sms"

    // Конструкторы, геттеры и сеттеры

    public NotificationRecord(long timestamp, String appName, String messageTitle, String messageBody, String notificationType) {
        this.timestamp = timestamp;
        this.appName = appName;
        this.messageTitle = messageTitle;
        this.messageBody = messageBody;
        this.notificationType = notificationType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}
