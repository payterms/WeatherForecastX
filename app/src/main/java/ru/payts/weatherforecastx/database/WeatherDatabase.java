package ru.payts.weatherforecastx.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ru.payts.weatherforecastx.dao.WeatherDao;
import ru.payts.weatherforecastx.model.City;
import ru.payts.weatherforecastx.model.DateConverter;
import ru.payts.weatherforecastx.model.WeatherRec;

@Database(entities = {City.class, WeatherRec.class}, version = 2)
@TypeConverters(DateConverter.class)
public abstract class WeatherDatabase extends RoomDatabase {
    public abstract WeatherDao getWeatherDao();
}
