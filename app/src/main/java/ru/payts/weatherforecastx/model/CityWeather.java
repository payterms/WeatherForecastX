package ru.payts.weatherforecastx.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.List;

// Результат запроса через Relation
// первый запрос по городам,
// далее запросы по каждому студенту для получения WeatherRec
public class CityWeather implements Serializable {

    @Embedded
    public City city;

    @Relation(parentColumn = "id", entityColumn = "city_id")
    public List<WeatherRec> weatherRecs;
}
