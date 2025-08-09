package com.jcapps.weatherapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jcapps.weatherapp.data.local.dao.WeatherDao
import com.jcapps.weatherapp.data.local.entities.WeatherEntity

@Database(
    entities = [WeatherEntity::class],
    version = 1,
    exportSchema = true
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}