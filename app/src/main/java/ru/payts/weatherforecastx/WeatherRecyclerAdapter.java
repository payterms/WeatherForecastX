package ru.payts.weatherforecastx;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.payts.weatherforecastx.model.WeatherRec;

public class WeatherRecyclerAdapter extends RecyclerView.Adapter<WeatherRecyclerAdapter.ViewHolder> {

    private List<WeatherRec> weatherRecs;

    public WeatherRecyclerAdapter(List<WeatherRec> weatherRecs){
        this.weatherRecs = weatherRecs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textWeather = new TextView(parent.getContext());
        textWeather.setTextSize(20);
        return new ViewHolder(textWeather);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textWeather.setTextSize(10);
        holder.textWeather.setText(weatherRecs.get(position).mainRestRecord.updateOn + ": T= " + Float.toString(weatherRecs.get(position).mainRestRecord.temp)+ "\u2103");
    }

    @Override
    public int getItemCount() {
        return weatherRecs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textWeather;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textWeather = (TextView) itemView;
        }
    }
}
