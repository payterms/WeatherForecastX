package ru.payts.weatherforecastx;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import ru.payts.weatherforecastx.dao.WeatherDao;
import ru.payts.weatherforecastx.ui.gallery.GalleryFragment;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;

    CityPreference cp;

    String currentCity;
    private AppBarConfiguration mAppBarConfiguration;
    WeatherFragment weatherFragment;
    private WeatherSource weatherSource;

    private MenuListAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // загружаем последний выбранный город из настроек
        cp = new CityPreference(this);
        currentCity = cp.getCity();
        setContentView(R.layout.activity_main);
        initViews();
        initList();
        initFabNext();
        initFabPrev();
        //initRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        handleMenuItemClick(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void initList() {
        WeatherDao weatherDao = App
                .getInstance()
                .getWeatherDao();

        weatherSource = new WeatherSource(weatherDao);
        /*ArrayList<String> data = new ArrayList<>();
        data.add("Tula");
        data.add("Orel");
        adapter = new MenuListAdapter(data, this);*/
        //LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        //RecyclerView recyclerView = findViewById(R.id.recyclerView);
        //recyclerView.setLayoutManager(manager);
        //recyclerView.setAdapter(adapter);

    }

    private void initFabNext() {
        FloatingActionButton fab = findViewById(R.id.fabNext);
        fab.setOnClickListener(view -> Snackbar.make(view, "Will Activate next city", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    private void initFabPrev() {
        FloatingActionButton fab = findViewById(R.id.fabPrev);
        fab.setOnClickListener(view -> Snackbar.make(view, "Will Activate prev city", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();

        drawer.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "Нажатие!", Toast.LENGTH_SHORT).show());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        currentCity = cp.getCity();

        //weatherFragment = new WeatherFragment();
        weatherFragment = (WeatherFragment) getSupportFragmentManager().findFragmentByTag("WEATHER");
        if (!isWeatherFragmentVisible() && weatherFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.weather_container, weatherFragment);
            transaction.commit();
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    private void handleMenuItemClick(MenuItem item) {
        boolean dataChanged = false;
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_add: {
                weatherSource.addCity(weatherFragment.getCurrentCity(), weatherFragment.getCurrentWeather());
                dataChanged = true;
                break;
            }
            case R.id.menu_search: {
                showInputDialog();
                break;
            }

            case R.id.menu_refresh: {
                weatherFragment = (WeatherFragment) getSupportFragmentManager().findFragmentByTag("WEATHER");
                if (weatherFragment != null) {
                    weatherFragment.updateWeatherData(cp.getCity(), Locale.getDefault().getLanguage());
                    dataChanged = true;
                }
                break;
            }
            default: {

            }


            ActionBar bar = getSupportActionBar();
            if (bar != null) {
                bar.setDisplayHomeAsUpEnabled(true);
            }
        }
        if(dataChanged){
            GalleryFragment cityListFragment = (GalleryFragment) getSupportFragmentManager().findFragmentByTag("CITYLISTFRAGMENT");

            // Check if the fragment is available
            if (cityListFragment!= null) {
                // Call your method in the GalleryFragment
                cityListFragment.updateDataInAdapter();
            }
        }
    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.changecity));
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton(getString(R.string.gobutton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                weatherFragment = (WeatherFragment) getSupportFragmentManager().findFragmentByTag("WEATHER");
                if (weatherFragment != null) {
                    weatherFragment.updateWeatherData(input.getText().toString(), Locale.getDefault().getLanguage());
                }

            }
        });
        builder.show();
    }

    public void changeCity(String city, String lang) {
        weatherFragment.changeCity(city, lang);
        currentCity = city;
        new CityPreference(this).setCity(city);
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("onStart()");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle saveInstanceState) {
        super.onRestoreInstanceState(saveInstanceState);
        System.out.println("Повторный запуск!! - onRestoreInstanceState()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause()");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        System.out.println("onSaveInstanceState()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStop()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("onRestart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy()");
    }

    private Boolean isWeatherFragmentVisible() {
        return getSupportFragmentManager().findFragmentByTag("WEATHER") != null && getSupportFragmentManager().findFragmentByTag("WEATHER").isVisible();
    }


}
