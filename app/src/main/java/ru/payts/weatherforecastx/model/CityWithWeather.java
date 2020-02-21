package ru.payts.weatherforecastx.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

// Результат запроса по двум таблицам через соединение
// одним запросом
public class CityWithWeather {
    @ColumnInfo(name = "city_name")
    public String cityName;

    @ColumnInfo(name = "country_name")
    public String countryName;

    @Embedded
    public WeatherRec weatherRec;
}
