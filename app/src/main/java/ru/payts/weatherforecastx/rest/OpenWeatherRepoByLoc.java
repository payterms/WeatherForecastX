package ru.payts.weatherforecastx.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OpenWeatherRepoByLoc {
    private static OpenWeatherRepoByLoc singleton = null;
    private IOpenWeatherByLoc API;

    private OpenWeatherRepoByLoc() {
        API = createAdapter();
    }

    public static OpenWeatherRepoByLoc getSingleton() {
        if (singleton == null) {
            singleton = new OpenWeatherRepoByLoc();
        }

        return singleton;
    }

    public IOpenWeatherByLoc getAPI() {
        return API;
    }

    private IOpenWeatherByLoc createAdapter() {
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return adapter.create(IOpenWeatherByLoc.class);
    }
}
