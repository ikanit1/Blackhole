package com.example.blackhole;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LogEntryDao {
    @Insert
    void insert(LogEntry logEntry);

    @Query("SELECT * FROM log_entries ORDER BY timestamp DESC")
    List<LogEntry> getAllLogs();

    @Query("SELECT * FROM log_entries WHERE level = :level ORDER BY timestamp DESC")
    List<LogEntry> getLogsByLevel(String level);
}
