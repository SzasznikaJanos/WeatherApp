package com.jcapps.weatherapp.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    
    private val lastSearchedCityIdKey = intPreferencesKey("last_searched_city_id")
    
    val lastSearchedCityId: Flow<Int?> = dataStore.data.map { preferences ->
        preferences[lastSearchedCityIdKey]
    }
    
    suspend fun changeCityId(cityId: Int) {
        dataStore.edit { preferences ->
            preferences[lastSearchedCityIdKey] = cityId
        }
    }
}