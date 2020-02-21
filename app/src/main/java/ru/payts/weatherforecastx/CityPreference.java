package ru.payts.weatherforecastx;

import android.app.Activity;
import android.content.SharedPreferences;

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

}