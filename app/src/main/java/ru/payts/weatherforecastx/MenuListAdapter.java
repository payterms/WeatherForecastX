package ru.payts.weatherforecastx;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.ViewHolder> {
    private ArrayList<String> cityList;
    private Activity activity;

    public MenuListAdapter(ArrayList<String> cityList, Activity activity) {
        this.activity = activity;
        if(cityList != null) {
            this.cityList = cityList;
        } else {
            this.cityList = new ArrayList<>();
            String[] defaultList = this.activity.getResources().getStringArray(R.array.citylist);
                for (int i = 0; i < defaultList.length; i++)
                {
                    cityList.add(defaultList[i]);
               }

        }
    }

    public void addItem(String city) {
        cityList.add(city);
        notifyItemInserted(cityList.size() - 1);
    }

    public void editItem(String newCity) {
        if(cityList.size() > 0) {
            int latestElement = cityList.size() - 1;
            cityList.set(latestElement, newCity);
            notifyItemChanged(latestElement);
        }
    }

    public void removeElement() {
        if(cityList.size() > 0) {
            int latestElement = cityList.size() - 1;
            cityList.remove(latestElement);
            notifyItemRemoved(latestElement);
        }
    }

    public void clearList() {
        cityList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_layout,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String city = cityList.get(position);
        holder.textView.setText(city);
        activity.registerForContextMenu(holder.textView);
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
