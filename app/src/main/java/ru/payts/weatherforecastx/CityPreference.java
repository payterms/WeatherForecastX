package ru.payts.weatherforecastx;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

public class CityPreference {
    private SharedPreferences prefs;

    public CityPreference(Activity activity) {
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    public String getCity() {
        return prefs.getString("city", "London");
    }

    float getTemperature() {
        return prefs.getFloat("temperature", 0);
    }

    void setTemperature(float temp) {
        prefs.edit().putFloat("temperature", temp).commit();
    }

    void setCity(String city) {
        prefs.edit().putString("city", city).commit();
    }
    public LatLng getLatLng() {
        float lat = prefs.getFloat("lat", 51.4934f);
        float lng = prefs.getFloat("lng",  0.0098f);
        LatLng latLng = new LatLng(lat, lng);
        return latLng;
    }

    void setLatLng(float lat, float lng) {
        // сохраняем текущие координаты как последние известные
        prefs.edit().putFloat("lat", lat).commit();
        prefs.edit().putFloat("lng", lng).commit();
    }

}