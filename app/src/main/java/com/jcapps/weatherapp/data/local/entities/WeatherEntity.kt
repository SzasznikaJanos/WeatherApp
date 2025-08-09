package com.jcapps.weatherapp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey
    val cityId: Int,
    
    @ColumnInfo(name = "city_name")
    val cityName: String,
    
    @ColumnInfo(name = "temperature")
    val temperature: Double,
    
    @ColumnInfo(name = "condition")
    val condition: String,
    
    @ColumnInfo(name = "icon_url")
    val iconUrl: String,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "humidity")
    val humidity: Int,
    
    @ColumnInfo(name = "wind_speed")
    val windSpeed: Double,
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long
)