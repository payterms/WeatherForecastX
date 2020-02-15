package ru.payts.weatherforecastx.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.Locale;
import java.util.Objects;

import ru.payts.weatherforecastx.CityPreference;
import ru.payts.weatherforecastx.R;
import ru.payts.weatherforecastx.WeatherFragment;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        homeViewModel.setText(new CityPreference(Objects.requireNonNull(this.getActivity())).getCity());
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WeatherFragment weatherFragment = new WeatherFragment();
        weatherFragment.changeCity(new CityPreference(Objects.requireNonNull(this.getActivity())).getCity(), Locale.getDefault().getLanguage());
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.weather_container, weatherFragment);
        transaction.commit();
    }
}