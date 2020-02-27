package ru.payts.weatherforecastx.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.payts.weatherforecastx.rest.entities.WeatherRequestRestModel;

public interface IOpenWeatherByLoc {
    @GET("data/2.5/weather")
    Call<WeatherRequestRestModel> loadWeather(@Query("lat") String lat,
                                              @Query("lon") String lon,
                                              @Query("appid") String keyApi,
                                              @Query("units") String units,
                                              @Query("lang") String lang);
}
