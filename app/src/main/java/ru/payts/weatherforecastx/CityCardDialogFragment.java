package ru.payts.weatherforecastx;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.payts.weatherforecastx.model.CityWeather;

public class CityCardDialogFragment extends DialogFragment {

    private final static String CITY_CARD = "CITY_CARD";

    public static CityCardDialogFragment create(CityWeather city){
        CityCardDialogFragment fragment = new CityCardDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(CITY_CARD, city);
        fragment.setArguments(args);
        return fragment;
    }

    private CityWeather getCity(){
        return (CityWeather) getArguments().getSerializable(CITY_CARD);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_history_dialog, null);

        CityWeather city = getCity();
        initViews(view, city);
        initRecycler(view, city);

        return view;
    }

    private void initRecycler(View view, CityWeather city) {
        RecyclerView recyclerView = view.findViewById(R.id.cityRecycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        WeatherRecyclerAdapter adapter = new WeatherRecyclerAdapter(city.weatherRecs);
        recyclerView.setAdapter(adapter);
    }

    private void initViews(View view, CityWeather city) {
        TextView textCity = view.findViewById(R.id.textCityName);
        //TextView textCountry = view.findViewById(R.id.textCountryName);
        TextView textCityLon = view.findViewById(R.id.textCityLon);
        TextView textCityLat = view.findViewById(R.id.textCityLat);

        textCity.setText(city.city.cityName + ", " + city.city.countryName);
        //textCountry.setText(city.city.countryName);
        textCityLon.setText("Longitude:" + ((city.city.coordinates.lon >= 0) ? "E" : "W") + Float.toString(Math.abs(city.city.coordinates.lon)));
        textCityLat.setText("Latitude:" + ((city.city.coordinates.lat >= 0)?"N":"S") + Float.toString(Math.abs(city.city.coordinates.lat)));

    }
}

