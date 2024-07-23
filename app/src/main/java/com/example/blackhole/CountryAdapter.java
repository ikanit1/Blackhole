package com.example.blackhole;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CountryAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private String[] mCountryCodes;
    private int[] mFlags;

    public CountryAdapter(@NonNull Context context, String[] countryCodes, int[] flags) {
        super(context, R.layout.spinner_item, countryCodes);
        this.mContext = context;
        this.mCountryCodes = countryCodes;
        this.mFlags = flags;
    }

    @Override
    public int getCount() {
        return mCountryCodes.length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.spinner_item, parent, false);
        }

        ImageView imageViewFlag = convertView.findViewById(R.id.imageViewFlag);
        TextView textViewCode = convertView.findViewById(R.id.textViewCode);

        imageViewFlag.setImageResource(mFlags[position]);
        textViewCode.setText(mCountryCodes[position]);

        return convertView;
    }
}
