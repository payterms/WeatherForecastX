package ru.payts.weatherforecastx.model;

import androidx.room.ColumnInfo;

// Класс, выделенный для обработки из класса City
// Однако в таблице, этот класс будет являтся полями таблицы lat
public class Coords {
    @ColumnInfo(name = "city_lon")
    public float lon;
    @ColumnInfo(name = "city_lat")
    public float lat;
}
