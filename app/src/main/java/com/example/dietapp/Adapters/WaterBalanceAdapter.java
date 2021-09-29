package com.example.dietapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.dietapp.Model.WaterBalance;
import com.example.dietapp.R;

import java.util.ArrayList;

public class WaterBalanceAdapter extends ArrayAdapter<WaterBalance> {

    private Context mContext;
    private int mResource;

    public WaterBalanceAdapter(@NonNull Context context, int resource, @NonNull ArrayList<WaterBalance> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView txtTime = convertView.findViewById(R.id.txtTime);
        TextView txtQty = convertView.findViewById(R.id.txtQty);

        txtTime.setText(getItem(position).getTime());
        txtQty.setText(getItem(position).getQty());

        return convertView;
    }

}