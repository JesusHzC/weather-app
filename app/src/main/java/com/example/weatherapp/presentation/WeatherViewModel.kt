package com.example.weatherapp.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.location.LocationTracker
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker
): ViewModel() {

    var state by mutableStateOf(WeatherState())
        private set

    var stateLive = MutableLiveData<WeatherState>(WeatherState())
        private set

    fun loadWeatherInfo() {
        viewModelScope.launch {
            state = state.copy(
                weatherInfo = null,
                isLoading = true,
                error = null
            )
            stateLive.postValue(state)
            locationTracker.getCurrentLocation()?.let { location ->
                when(val result = repository.getWeatherData(
                    latitude = location.latitude,
                    longitude = location.longitude
                )) {
                    is Resource.Success -> {
                        state = state.copy(
                            weatherInfo = result.data,
                            isLoading = false,
                            error = null
                        )
                        stateLive.postValue(state)
                    }
                    is Resource.Error -> {
                        state = state.copy(
                            weatherInfo = null,
                            isLoading = false,
                            error = result.message
                        )
                        stateLive.postValue(state)
                    }
                }
            } ?: run {
                state = state.copy(
                    weatherInfo = null,
                    isLoading = false,
                    error = "Couldn't get location"
                )
                stateLive.postValue(state)
            }
        }
    }
}