package ru.payts.weatherforecastx.ui.gallery;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;
import java.util.Objects;

import ru.payts.weatherforecastx.App;
import ru.payts.weatherforecastx.CityCardDialogFragment;
import ru.payts.weatherforecastx.CityPreference;
import ru.payts.weatherforecastx.CityRecyclerAdapter;
import ru.payts.weatherforecastx.R;
import ru.payts.weatherforecastx.WeatherFragment;
import ru.payts.weatherforecastx.WeatherSource;
import ru.payts.weatherforecastx.dao.WeatherDao;
import ru.payts.weatherforecastx.model.City;
import ru.payts.weatherforecastx.model.CityWeather;
import ru.payts.weatherforecastx.model.WeatherRec;

public class GalleryFragment extends Fragment {

    private static final String CITYLIST_FRAGMENT_TAG = "CITYLISTFRAGMENT";
    private WeatherSource weatherSource;
    private CityRecyclerAdapter adapter;
    private GalleryFragment frag;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        /*if (frag == null) {
            frag = new GalleryFragment();
        }
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_gallery, frag, CITYLIST_FRAGMENT_TAG);
        transaction.commit();*/

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        initRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        handleMenuItemClick(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.view_context:
                City viewCity = weatherSource
                        .getCitys()
                        .get((int) adapter.getMenuPosition());
                CityWeather cityWeather = weatherSource.getCityWeatherRec(viewCity.id);
                CityCardDialogFragment.create(cityWeather).show(getChildFragmentManager(), "CityCard");
                return true;
            // Добавить запись
            case R.id.add_context:
                // Получаем студента со случайными данными
                City cityForInsert = WeatherFragment.getCurrentCity();
                WeatherRec weatherForInsert = WeatherFragment.getCurrentWeather();
                // Добавляем студента
                weatherSource.addCity(cityForInsert, weatherForInsert);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.update_context:
                // Изменение имени и фамилии у студента
                City oldCity = weatherSource
                        .getCitys()
                        .get((int) adapter.getMenuPosition());
                City cityForUpdate = WeatherFragment.getCurrentCity();
                weatherSource.updateCity(cityForUpdate);
                adapter.notifyItemChanged((int) adapter.getMenuPosition());
                return true;
            case R.id.remove_context:
                // Удалить запись из базы
                City cityForRemove = weatherSource
                        .getCitys()
                        .get((int) adapter.getMenuPosition());
                weatherSource.removeCity(cityForRemove.id);
                adapter.notifyItemRemoved((int) adapter.getMenuPosition());
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    private void handleMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_add: {
                adapter.addItem(WeatherFragment.getCurrentCity(), WeatherFragment.getCurrentWeather());
                break;
            }
            case R.id.menu_search: {
                //showInputDialog();
                break;
            }
            default: {

            }
            ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (bar != null) {
                bar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }


    private void initRecyclerViewAdapter() {
        WeatherDao weatherDao = App
                .getInstance()
                .getWeatherDao();

        weatherSource = new WeatherSource(weatherDao);

        adapter = new CityRecyclerAdapter(weatherSource, getActivity());
    }

    public void updateDataInAdapter(){
        adapter.notifyDataSetChanged();
    }


}