package com.example.floklores.Infrastructure;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.floklores.Models.ProductItem;

@Database(entities = {ProductItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProductDao productDao();
}