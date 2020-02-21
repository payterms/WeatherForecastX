package ru.payts.weatherforecastx.ui.gallery;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.payts.weatherforecastx.App;
import ru.payts.weatherforecastx.CityCardDialogFragment;
import ru.payts.weatherforecastx.CityRecyclerAdapter;
import ru.payts.weatherforecastx.R;
import ru.payts.weatherforecastx.WeatherFragment;
import ru.payts.weatherforecastx.WeatherSource;
import ru.payts.weatherforecastx.dao.WeatherDao;
import ru.payts.weatherforecastx.model.City;
import ru.payts.weatherforecastx.model.CityWeather;
import ru.payts.weatherforecastx.ui.send.SendViewModel;

public class GalleryFragment extends Fragment {

    private WeatherSource weatherSource;
    private CityRecyclerAdapter adapter = null;

    private GalleryViewModel galleryViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        RecyclerView recyclerView = root.getRootView().findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        WeatherDao weatherDao = App
                .getInstance()
                .getWeatherDao();

        weatherSource = new WeatherSource(weatherDao);

        adapter = new CityRecyclerAdapter(weatherSource, getActivity());
        recyclerView.setAdapter(adapter);
        return root;
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
                // Добавляем студента
                weatherSource.addCity(cityForInsert);
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
                adapter.addItem(WeatherFragment.getCurrentCity());
                break;
            }
            case R.id.menu_search: {
                //showInputDialog();
                break;
            }
            case R.id.menu_edit: {
                //showInputDialog();
                adapter.editItem(WeatherFragment.getCurrentCity());
                break;
            }
            case R.id.menu_remove: {
                adapter.removeElement(1);
                break;
            }
            case R.id.menu_clear: {
                adapter.clearList();
                break;
            }
            default: {

            }
            ActionBar bar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (bar != null) {
                bar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }


    private void initRecyclerView() {
        RecyclerView recyclerView = getActivity().findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        WeatherDao weatherDao = App
                .getInstance()
                .getWeatherDao();
        weatherSource = new WeatherSource(weatherDao);

        adapter = new CityRecyclerAdapter(weatherSource, getActivity());
        recyclerView.setAdapter(adapter);
    }


}