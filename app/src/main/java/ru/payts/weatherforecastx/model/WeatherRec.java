package ru.payts.weatherforecastx.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

// Таблица со списком электронных почт студента
// связана по полям id со стороны таблицы lat и
// student_id со стороны таблицы email (внешний ключ)
// При удалении студента, и все почтовые адреса тоже удаляются (CASCADE)
@Entity(foreignKeys = @ForeignKey(entity = City.class,
        parentColumns = "id",
        childColumns = "city_id", onDelete = CASCADE))

public class WeatherRec {
    @PrimaryKey(autoGenerate = true)
    public long id;

    // Внешний ключ
    @ColumnInfo(name = "city_id")
    public long cityId;

    @Embedded
    public MainRestRecord mainRestRecord;
}
