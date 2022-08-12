package com.example.weatherapp.utils

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.weatherapp.R
import com.example.weatherapp.presentation.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WeatherWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, "")
        }
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, temperature: String) {
    RemoteViews(context.packageName, R.layout.weather_widget_layout).also { views ->
        CoroutineScope(Dispatchers.IO).launch {
            getStringPreference(context, "temperature").first {
                Log.d("WeatherWidget", "Temperature: $it")
                views.setTextViewText(R.id.txtTemperature, it)
                appWidgetManager.updateAppWidget(appWidgetId, views)
                true
            }
        }
    }
}

internal fun getStringPreference(context: Context, key: String) = context.dataStore.data.map { preferences ->
    preferences[stringPreferencesKey(key)] ?: ""
}