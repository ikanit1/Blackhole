package com.example.blackhole;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "log_entries")
public class LogEntry {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String timestamp;
    public String phoneNumber;
    public String recipient;
    public String message;
    public int status;
    public String level;
    public String stackTrace;

    public LogEntry(String timestamp, String phoneNumber, String recipient, String message) {
        this.timestamp = timestamp;
        this.phoneNumber = phoneNumber;
        this.recipient = recipient;
        this.message = message;
        this.status = status;
        this.level = level;
        this.stackTrace = stackTrace;
    }
}
