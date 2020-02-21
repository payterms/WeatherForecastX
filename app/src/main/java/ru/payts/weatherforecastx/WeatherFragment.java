package ru.payts.weatherforecastx;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.payts.weatherforecastx.rest.OpenWeatherRepo;
import ru.payts.weatherforecastx.rest.entities.WeatherRequestRestModel;

public class WeatherFragment extends Fragment {

    private Typeface weatherFont;
    private TextView cityField;
    private TextView updatedField;
    private TextView detailsField;
    private TextView currentTemperatureField;
    private TextView weatherIcon;
    private ImageView weatherIconImage;
    private ThermometerView thermometerView;
    private String icon;
    private Handler handler;
    private CityPreference cityPreference;
    private WeatherFragment weatherFragment;
    private float currentTemp;


    public WeatherFragment() {
        handler = new Handler();
        weatherFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = rootView.findViewById(R.id.city_field);
        updatedField = rootView.findViewById(R.id.updated_field);
        detailsField = rootView.findViewById(R.id.details_field);
        currentTemperatureField = rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = rootView.findViewById(R.id.weather_icon);
        weatherIconImage = rootView.findViewById(R.id.weather_icon_image);
        weatherIcon.setTypeface(weatherFont);
        thermometerView = rootView.findViewById(R.id.thermometerView);
        thermometerView.setOnClickListener(v -> Toast.makeText(WeatherFragment.super.getContext(), "Нажали на градусник", Toast.LENGTH_SHORT).show());
        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weatherFont = Typeface.createFromAsset(Objects.requireNonNull(getActivity()).getAssets(), "fonts/weather.ttf");
        cityPreference = new CityPreference(getActivity());
        String city = cityPreference.getCity();
        currentTemp = cityPreference.getTemperature();
        updateWeatherData(city, Locale.getDefault().getLanguage());

    }

    void updateWeatherData(final String city, final String lang) {
        OpenWeatherRepo.getSingleton().getAPI().loadWeather(city,
                "fea82f030303d179dd680b5ade7deeb0", "metric")
                .enqueue(new Callback<WeatherRequestRestModel>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherRequestRestModel> call,
                                           @NonNull Response<WeatherRequestRestModel> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            renderWeather(response.body());
                        } else {
                            //Похоже, код у нас не в диапазоне [200..300) и случилась ошибка
                            //обрабатываем ее
                            if (response.code() == 500) {
                                //ой, случился Internal Server Error. Решаем проблему
                                // Создаем билдер и передаем контекст приложения
                                AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                                // в билдере указываем заголовок окна (можно указывать как ресурс, так и строку)
                                builder.setTitle(R.string.press_button)
                                        // указываем сообщение в окне (также есть вариант со строковым параметром)
                                        .setMessage(R.string.place_not_found)
                                        // можно указать и пиктограмму
                                        .setIcon(R.drawable.moose)
                                        // из этого окна нельзя выйти кнопкой back
                                        .setCancelable(false)
                                        // устанавливаем кнопку (название кнопки также можно задавать строкой)
                                        .setPositiveButton(R.string.button,
                                                // Ставим слушатель, нажатие будем обрабатывать
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        //Toast.makeText(getActivity(), "Кнопка нажата", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else if (response.code() == 401) {
                                //не авторизованы, что-то с этим делаем
                                Toast.makeText(getActivity(), "Not Authorised", Toast.LENGTH_SHORT).show();
                            }// и так далее
                        }
                    }

                    //сбой при интернет подключении
                    @Override
                    public void onFailure(Call<WeatherRequestRestModel> call, Throwable t) {
                        Toast.makeText(getActivity().getBaseContext(), getString(R.string.network_error),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getIconLink() {
        return "http://openweathermap.org/img/w/" + icon + ".png";
    }

    private void setIcon(String icon) {
        this.icon = icon;
    }

    private void renderWeather(WeatherRequestRestModel model) {
        setPlaceName(model.name, model.sys.country);
        setDetails(model.weather[0].description, model.main.humidity, model.main.pressure);
        setCurrentTemp(model.main.temp);
        setUpdatedText(model.dt);
        setWeatherIcon(model.weather[0].id,
                model.sys.sunrise * 1000,
                model.sys.sunset * 1000);
        setWeatherIconImage(model.weather[0].icon);
    }

    private void setWeatherIconImage(String iconID) {
        String iconLink;
        setIcon(iconID);
        iconLink = getIconLink();
        Picasso.get().load(iconLink).into(weatherIconImage);
    }

    private void setPlaceName(String name, String country) {
        String cityText = name.toUpperCase() + ", " + country;
        cityField.setText(cityText);
        cityPreference.setCity(name.toUpperCase());
    }

    private void setDetails(String description, float humidity, float pressure) {
        String detailsText = description.toUpperCase() + "\n"
                + "Humidity: " + humidity + "%" + "\n"
                + "Pressure: " + pressure + "hPa";
        detailsField.setText(detailsText);
    }

    private void setCurrentTemp(float temp) {
        currentTemp = temp;
        cityPreference.setTemperature(temp);
        String currentTextText = String.format(Locale.getDefault(), "%.2f", temp) + "\u2103";
        currentTemperatureField.setText(currentTextText);
    }

    private void setUpdatedText(long dt) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String updateOn = dateFormat.format(new Date(dt * 1000));
        String updatedText = "Last update: " + updateOn;
        updatedField.setText(updatedText);
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        Activity currentActivity = getActivity();
        if (currentActivity == null)
            return;
        String textIcon = "";
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                textIcon = currentActivity.getString(R.string.weather_sunny);
            } else {
                textIcon = currentActivity.getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    textIcon = currentActivity.getString(R.string.weather_thunder);
                    break;
                case 3:
                    textIcon = currentActivity.getString(R.string.weather_drizzle);
                    break;
                case 7:
                    textIcon = currentActivity.getString(R.string.weather_foggy);
                    break;
                case 8:
                    textIcon = currentActivity.getString(R.string.weather_cloudy);
                    break;
                case 6:
                    textIcon = currentActivity.getString(R.string.weather_snowy);
                    break;
                case 5:
                    textIcon = currentActivity.getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(textIcon);
    }

    public void changeCity(String city, String lang) {
        updateWeatherData(city, lang);
    }
}