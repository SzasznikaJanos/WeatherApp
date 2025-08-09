package com.jcapps.weatherapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.jcapps.weatherapp.data.local.entities.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Upsert
    suspend fun upsert(weather: WeatherEntity)

    @Query("SELECT * FROM weather WHERE cityId = :cityId")
    fun observeCityWeather(cityId: Int): Flow<WeatherEntity?>
}