package ru.payts.weatherforecastx.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.payts.weatherforecastx.model.City;
import ru.payts.weatherforecastx.model.CityWeather;
import ru.payts.weatherforecastx.model.CityWithWeather;
import ru.payts.weatherforecastx.model.WeatherRec;

// Описание объекта, обрабатывающего данные
// @Dao - доступ к данным
// В этом классе описывается, как будет происходить обработка данных
@Dao
public interface WeatherDao {

    // Метод для добавления города в базу данных
    // @Insert - признак добавления
    // onConflict - что делать, если такая запись уже есть
    // В данном случае просто заменим ее
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCity(City city);

    // Метод для замены данных города
    @Update
    void updateCity(City city);

    // Удалим данные города
    @Delete
    void deleteCity(City city);

    // Удалим данные города, просто зная ключ
    @Query("DELETE FROM City WHERE id = :id")
    void deteleCityById(long id);

    // Заберем данные по всем городам
    @Query("SELECT * FROM City")
    List<City> getAllCitys();

    // Получим данные только одного города по id
    @Query("SELECT * FROM City WHERE id = :id")
    City getCityById(long id);

    // Получим данные только одного города по id
    @Query("SELECT * FROM City WHERE city_name = :cityName")
    City getCityByName(String cityName);

    //Получить количество записей в таблице
    @Query("SELECT COUNT() FROM City")
    long getCountCitys();

    // Запрос сразу из двух таблиц
    @Query("SELECT city_name, country_name " +
            "FROM City " +
            "INNER JOIN WeatherRec ON City.id = WeatherRec.City_id")
    List<CityWithWeather> getCityWithWeather();

    // Получим запись погоды одного города
    @Query("SELECT * FROM WeatherRec WHERE City_id = :CityId")
    List<WeatherRec> getWeatherByCity(long CityId);

    // Запрос через Relation
    @Query("SELECT * FROM City")
    List<CityWeather> getCityWeatherRecs();

    // Получить через Relation одного города
    @Query("SELECT * FROM City WHERE id = :id")
    CityWeather getOneCityWeatherRecs(long id);

    @Insert
    long insertWeatherRec(WeatherRec weatherRec);

    @Query("SELECT id, city_name, country_name FROM City")
    Cursor getCityCursor();

    @Query("SELECT id, city_name, country_name FROM City WHERE id = :id")
    Cursor getCityCursor(long id);
}
