package com.example.administrator.GoAndDo;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Task.class}, version = 4, exportSchema=false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}
