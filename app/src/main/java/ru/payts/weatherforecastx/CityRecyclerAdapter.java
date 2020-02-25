package ru.payts.weatherforecastx;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.payts.weatherforecastx.model.City;
import ru.payts.weatherforecastx.model.WeatherRec;

// Адаптер для RecyclerView
public class CityRecyclerAdapter extends RecyclerView.Adapter<CityRecyclerAdapter.ViewHolder> {

    private Activity activity;
    // Источник данных
    private WeatherSource dataSource;
    // Позиция в списке, где было нажато меню
    private long menuPosition;

    public CityRecyclerAdapter(WeatherSource dataSource, Activity activity) {
        this.dataSource = dataSource;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Заполнение данными записи на экране
        List<City> citys = dataSource.getCitys();
        City city = citys.get(position);
        holder.cityName.setText(city.cityName);
        holder.countryName.setText(city.countryName);
        holder.cityLon.setText(((city.coordinates.lon >= 0)?"E":"W") + Float.toString(Math.abs(city.coordinates.lon)));
        holder.cityLat.setText(((city.coordinates.lat >= 0)?"N":"S") + Float.toString(Math.abs(city.coordinates.lat)));

        // Тут определим, в каком пункте меню было нажато
        holder.cardView.setOnLongClickListener(view -> {
            menuPosition = position;
            return false;
        });

        // регистрируем контекстное меню
        if (activity != null) {
            activity.registerForContextMenu(holder.cardView);
        }
    }

    @Override
    public int getItemCount() {
        return (int) dataSource.getCountCitys();
    }

    public long getMenuPosition() {
        return menuPosition;
    }

    public void addItem(City city, WeatherRec weather) {
        dataSource.addCity(city, weather);
        notifyItemInserted(getItemCount() - 1);
    }

    public void editItem(City newCity) {
        if (getItemCount() > 0) {
            dataSource.updateCity(newCity);
        }
    }

    public void removeElement(int id) {
        if (getItemCount() > 0) {
            dataSource.removeCity(id);
            notifyItemRemoved(getItemCount()-1);
        }
    }

    public void clearList() {
        /*cityList.clear();
        notifyDataSetChanged();*/
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView cityName;
        TextView countryName;
        TextView cityLon;
        TextView cityLat;
        View cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView;
            cityName = cardView.findViewById(R.id.textCityName);
            countryName = cardView.findViewById(R.id.textCountryName);
            cityLon = cardView.findViewById(R.id.textCityLon);
            cityLat = cardView.findViewById(R.id.textCityLat);
        }
    }
}
