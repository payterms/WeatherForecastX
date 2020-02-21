package ru.payts.weatherforecastx;

import android.app.Application;

import androidx.room.Room;

import ru.payts.weatherforecastx.dao.WeatherDao;
import ru.payts.weatherforecastx.database.Migration_1_2;
import ru.payts.weatherforecastx.database.WeatherDatabase;

// паттерн синглтон, наследуем класс Application
// создаем базу данных в методе onCreate
public class App extends Application {

    private static App instance;

    // база данных
    private WeatherDatabase db;

    // Так получаем объект приложения
    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Это для синглтона, сохраняем объект приложения
        instance = this;

        // строим базу
        db = Room.databaseBuilder(
                    getApplicationContext(),
                    WeatherDatabase.class,
                    "weather_database")
                .allowMainThreadQueries() //Только для примеров и тестирования.
                .addMigrations(new Migration_1_2())
                .build();
    }

    // Получаем WeatherDao для составления запросов
    public WeatherDao getWeatherDao() {
        return db.getWeatherDao();
    }
}
