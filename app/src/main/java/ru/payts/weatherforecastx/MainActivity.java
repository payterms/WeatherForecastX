package ru.payts.weatherforecastx;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Locale;

import ru.payts.weatherforecastx.dao.WeatherDao;
import ru.payts.weatherforecastx.ui.gallery.GalleryFragment;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 10;
    private final int RC_SIGN_IN = 100;
    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    SignInButton signInButton;
    boolean isSignedIn = false;

    private int drawableResourceId = R.drawable.login;

    TextView usrName ;
    TextView usrEmail;
    ImageView usrImg;

    String userName;
    String userMail;
    Uri userPhoto;

    GoogleSignInClient googleSignInClient;

    private BroadcastReceiver statesMessageReceiver = new StatesMessageReceiver();

    CityPreference cp;

    String currentCity;
    private AppBarConfiguration mAppBarConfiguration;
    WeatherFragment weatherFragment;
    private WeatherSource weatherSource;

    /**
     * The link to a {@link LocationManager} instance for quick access.
     */
    private LocationManager mLocManager = null;

    /**
     * The link to a Location Listener instance.
     */
    private LocListener mLocListener = null;

    Location currentLocation;
    String cityFound;

    LatLng currentCoordinates;

    private GoogleMap mMap;
    private Marker currentMarker;

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
        requestPemissions();
    }

    private void requestPemissions() {
        // Проверим на пермиссии, и если их нет, запросим у пользователя
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // запросим координаты
            requestLocation();
        } else {
            // пермиссии нет, будем запрашивать у пользователя
            requestLocationPermissions();
        }

    }

    // Запрос координат
    private void requestLocation() {
        // Create Location Listener object (if needed)
        if (mLocListener == null) mLocListener = new LocListener();
        // Setting up Location Listener
        // min time - 3 seconds
        // min distance - 1 meter
        // Если пермиссии все таки нет - то просто выйдем, приложение не имеет смысла
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        // Получить менеджер геолокаций
        mLocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        // получим наиболее подходящий провайдер геолокации по критериям
        // Но можно и самому назначать какой провайдер использовать.
        // В основном это LocationManager.GPS_PROVIDER или LocationManager.NETWORK_PROVIDER
        // но может быть и LocationManager.PASSIVE_PROVIDER, это когда координаты уже кто-то недавно получил.
        String provider = mLocManager.getBestProvider(criteria, true);
        if (provider != null) {
            currentLocation = mLocManager.getLastKnownLocation(provider);
            mLocManager.requestLocationUpdates(provider, 3000L, 1.0F, mLocListener);
        }
    }

    // Запрос пермиссии для геолокации
    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            // Запросим эти две пермиссии у пользователя
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }


    // Это результат запроса у пользователя пермиссии
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {   // Это та самая пермиссия, что мы запрашивали?
            if (grantResults.length == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                // Все препоны пройдены и пермиссия дана
                // Запросим координаты
                requestLocation();
            }
        }
    }

    // Получаем адрес по координатам
    private void getAddress(final LatLng location) {
        final Geocoder geocoder = new Geocoder(this);
        // Поскольку geocoder работает по интернету, создадим отдельный поток
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
                    cityFound = addresses.get(0).getLocality();
                    Log.d(TAG, "Current Locality:" + cityFound);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Получаем адрес по координатам
    private void getAddressGUI(final LatLng location) {
        final Geocoder geocoder = new Geocoder(this);
        try {
            cityFound = null;
            final List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            cityFound = addresses.get(0).getLocality();
            Log.d(TAG, "Current Locality:" + cityFound);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.menu_sign_in).setIcon(drawableResourceId);
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
        // Конфигурация запроса на регистрацию пользователя, чтобы получить
        // идентификатор пользователя, его почту и основной профайл (регулируется параметром)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        WeatherDao weatherDao = App
                .getInstance()
                .getWeatherDao();

        weatherSource = new WeatherSource(weatherDao);
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

        weatherFragment = (WeatherFragment) getSupportFragmentManager().findFragmentByTag("WEATHER");
        if (!isWeatherFragmentVisible() && weatherFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.weather_container, weatherFragment);
            transaction.commit();
        }

        View header = navigationView.getHeaderView(0);
        usrName = (TextView) header.findViewById(R.id.userName);
        usrEmail = (TextView) header.findViewById(R.id.userEmail);
        usrImg = (ImageView) header.findViewById(R.id.userImg);
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
            case R.id.menu_current: {
                weatherFragment = (WeatherFragment) getSupportFragmentManager().findFragmentByTag("WEATHER");
                if (weatherFragment != null) {
                    // запросим координаты
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                    if (currentLocation == null) {
                        currentLocation = mLocManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    }
                    if (currentLocation != null) {
                        LatLng coord = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        getAddressGUI(coord);
                        weatherFragment.updateWeatherDataByLoc(coord, Locale.getDefault().getLanguage());
                    }
                }
                break;
            }
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
                    weatherFragment.updateWeatherDataByLoc(cp.getLatLng(), Locale.getDefault().getLanguage());
                    dataChanged = true;
                }
                break;
            }
            case R.id.menu_sign_in: {
                if (isSignedIn == false){
                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                    drawableResourceId = R.drawable.logout;
                }
                else{
                    googleSignInClient.signOut();
                    isSignedIn = false;
                    drawableResourceId = R.drawable.login;
                }
                invalidateOptionsMenu();
                break;
            }



            default: {

            }


            ActionBar bar = getSupportActionBar();
            if (bar != null) {
                bar.setDisplayHomeAsUpEnabled(true);
            }
        }
        if (dataChanged) {
            GalleryFragment cityListFragment = (GalleryFragment) getSupportFragmentManager().findFragmentByTag("CITYLISTFRAGMENT");

            // Check if the fragment is available
            if (cityListFragment != null) {
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
                    weatherFragment.updateWeatherDataByCity(input.getText().toString(), Locale.getDefault().getLanguage());
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
        // Проверим, заходил ли пользователь в этом приложении через Гугл
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            // Обновим почтовый адрес этого пользователя и выведем его на экран
            isSignedIn = true;
            userName = account.getDisplayName();
            usrName.setText(userName);
            userMail = account.getEmail();
            usrEmail.setText(userMail);
            userPhoto = account.getPhotoUrl();
            usrImg.setImageURI(userPhoto);
            drawableResourceId = R.drawable.logout;
        }
        else{
            isSignedIn = false;
        }
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
        IntentFilter ourFilter = new IntentFilter();
        ourFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        ourFilter.addAction(Intent.ACTION_BATTERY_LOW);
        ourFilter.addAction(Intent.ACTION_BATTERY_OKAY);
        ourFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(statesMessageReceiver, ourFilter);
        initNotificationChannel();
        requestLocation();

    }

    @Override
    protected void onPause() {
        // Remove Location Listener
        if (mLocListener != null)
            if (mLocManager != null)
                mLocManager.removeUpdates(mLocListener);
        super.onPause();
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
        unregisterReceiver(statesMessageReceiver);
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

    // инициализация канала нотификаций
    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("2", "name", importance);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Class that implements Location Listener interface
     */
    private final class LocListener implements LocationListener {

        /**
         * Called when the location has changed.
         */
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged: " + location.toString());
            currentLocation = location;
            double lat = location.getLatitude();// Широта
            double lng = location.getLongitude();// Долгота
            String accuracy = Float.toString(location.getAccuracy());   // Точность
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { /* Empty */ }

        @Override
        public void onProviderEnabled(String provider) { /* Empty */ }

        @Override
        public void onProviderDisabled(String provider) { /* Empty */ }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            isSignedIn = true;
            drawableResourceId = R.drawable.logout;
            // Signed in successfully, show authenticated UI.
            userName = account.getDisplayName();
            usrName.setText(userName);
            userMail = account.getEmail();
            usrEmail.setText(userMail);
            userPhoto = account.getPhotoUrl();
            usrImg.setImageURI(userPhoto);
            Log.w(TAG, "signInResult:success code=" + userName + userMail);
            //Здесь мы отправляем токен на сервер.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
}
