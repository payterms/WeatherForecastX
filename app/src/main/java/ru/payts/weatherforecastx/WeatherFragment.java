package ru.payts.weatherforecastx;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

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


    public WeatherFragment() {
        handler = new Handler();
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
        /*weatherIconImage = (ImageView) rootView.findViewById(R.id.weather_icon_image);*/
        weatherIcon.setTypeface(weatherFont);
        thermometerView = rootView.findViewById(R.id.thermometerView);
        thermometerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WeatherFragment.super.getContext(), "Нажали на градусник", Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(Objects.requireNonNull(getActivity()).getAssets(), "fonts/weather.ttf");
        updateWeatherData(new CityPreference(getActivity()).getCity(), Locale.getDefault().getLanguage());
    }

    private void updateWeatherData(final String city, final String lang) {
        new Thread() {
            public void run() {
                final JSONObject json = RemoteFetch.getJSON(getActivity(), city, lang);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
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
                            /*Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();*/
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    public String getIcon() {
        return icon;
    }

    private String getIconLink() {
        return "http://openweathermap.org/img/w/" + icon + ".png";
    }

    private void setIcon(String icon) {
        this.icon = icon;
    }

    @SuppressLint("DefaultLocale")
    private void renderWeather(JSONObject json) {
        try {
            String currentField;
            currentField = json.getString("name").toUpperCase(Resources.getSystem().getConfiguration().locale) +
                    ", " +
                    json.getJSONObject("sys").getString("country");
            cityField.setText(currentField);
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            currentField = details.getString("description").toUpperCase(Resources.getSystem().getConfiguration().locale) +
                    "\n" + getString(R.string.temperature) + main.getString("temp") + " C" +
                    "\n" + getString(R.string.humidity) + main.getString("humidity") + "%" +
                    "\n" + getString(R.string.pressure) + main.getString("pressure") + " hPa";
            detailsField.setText(currentField);

            setIcon(details.getString("icon"));
            currentField = String.format("%.2f", main.getDouble("temp")) + " ℃";
            currentTemperatureField.setText(currentField);

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt") * 1000));
            currentField = getString(R.string.lastupdate) + updatedOn;
            updatedField.setText(currentField);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        } catch (JSONException e) {
            Log.e("Weather", "One or more fields not found in the JSON data");
        } catch (Exception e) {
            Log.e("Weather", "Network Connection Exception");
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        Activity currentActivity = getActivity();
        if (currentActivity == null)
            return;
        String icon = "";
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = currentActivity.getString(R.string.weather_sunny);
            } else {
                icon = currentActivity.getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    icon = currentActivity.getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = currentActivity.getString(R.string.weather_drizzle);
                    break;
                case 7:
                    icon = currentActivity.getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = currentActivity.getString(R.string.weather_cloudy);
                    break;
                case 6:
                    icon = currentActivity.getString(R.string.weather_snowy);
                    break;
                case 5:
                    icon = currentActivity.getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);

        URL url;
        Bitmap bmp = null;
        try {
            url = new URL(getIconLink());
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        weatherIconImage.setImageBitmap(bmp);
    }

    public void changeCity(String city, String lang) {
        updateWeatherData(city, lang);
    }
}