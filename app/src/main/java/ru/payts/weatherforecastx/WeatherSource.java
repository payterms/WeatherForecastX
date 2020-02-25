package ru.payts.weatherforecastx;

import java.util.List;

import ru.payts.weatherforecastx.dao.WeatherDao;
import ru.payts.weatherforecastx.model.City;
import ru.payts.weatherforecastx.model.CityWeather;
import ru.payts.weatherforecastx.model.WeatherRec;

// Вспомогательный класс, развязывающий
// зависимость между Room и RecyclerView
public class WeatherSource {

    private final WeatherDao weatherDao;

    // Буфер с данными, сюда будем подкачивать данные из БД
    private List<City> citys;

    public WeatherSource(WeatherDao weatherDao) {
        this.weatherDao = weatherDao;
    }

    // Получить всех студентов
    public List<City> getCitys() {
        // Если объекты еще не загружены, то загружаем их.
        // Сделано для того, чтобы не делать запросы в БД каждый раз
        if (citys == null) {
            loadCitys();
        }
        return citys;
    }

    // Загрузить студентов в буфер
    public void loadCitys() {
        citys = weatherDao.getAllCitys();
    }

    // Получить количество записей
    public long getCountCitys() {
        return weatherDao.getCountCitys();
    }

    // Добавить студента
    public void addCity(City city, WeatherRec weather) {
        long id;
        City cityFromDB = weatherDao.getCityByName(city.cityName);
        if (cityFromDB == null) {
            // такого города нет в базе - добавляем его
            id = weatherDao.insertCity(city);
        } else {
            // город уже в базе - просто берем его ID
            id = cityFromDB.id;
        }
        // привязываем запись о погоде к текущему городу
        weather.cityId = id;
        weatherDao.insertWeatherRec(weather);
        // После изменения БД надо перечитать буфер
        loadCitys();
    }

    public CityWeather getCityWeatherRec(long id) {
        return weatherDao.getOneCityWeatherRecs(id);
    }

    // Заменить студента
    public void updateCity(City city) {
        weatherDao.updateCity(city);
        loadCitys();
    }

    // Удалить студента из базы
    public void removeCity(long id) {
        weatherDao.deteleCityById(id);
        loadCitys();
    }

}
