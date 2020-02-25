package ru.payts.weatherforecastx.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

// @Entity - это признак табличного объекта, то есть объект
// будет сохранятся в базе данных в виде строки
// indices - указывает на индексы в таблице
@Entity(indices = {@Index(value = {City.CITY_NAME, City.COUNTRY_NAME})})
public class City {

    public final static String ID = "id";
    public final static String CITY_NAME = "city_name";
    public final static String COUNTRY_NAME = "country_name";

    // @PrimaryKey - указывает на ключевую запись,
    // autoGenerate = true - автоматическая генерация ключа
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    public long id;

    // Название города
    // @ColumnInfo - позволяет задавать параметры колонки в БД
    // name = "first_name" - такое будет имя колонки
    @ColumnInfo(name = CITY_NAME)
    public String cityName;

    // Название страны
    @ColumnInfo(name = COUNTRY_NAME)
    public String countryName;

    // @Embedded - хранить поля вложенного класса, как поля таблицы
    // в адресе хранится информация о долготе и широте города
    @Embedded
    public Coords coordinates;
}
