package com.printer.sdk;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.printf.model.BluetoothModel;

import java.util.List;

public class RecyclerShowAdapter extends
        BaseRecyclerViewAdapter<RecyclerShowAdapter.RecyclerShowAdapterHolder,BluetoothModel> {


    public RecyclerShowAdapter(Context context, List<BluetoothModel> dates) {
        super(context, dates);
    }

    @NonNull
    @Override
    public RecyclerShowAdapterHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_show_blue,viewGroup,false);
        RecyclerShowAdapterHolder recyclerShowAdapterHolder = new RecyclerShowAdapterHolder(itemView);
        return recyclerShowAdapterHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerShowAdapterHolder recyclerShowAdapterHolder, int position) {
        BluetoothModel bluetoothModel = dates.get(position);
        recyclerShowAdapterHolder.tv_blue_list_address.setText(bluetoothModel.getBluetoothMac());
        recyclerShowAdapterHolder.tv_blue_list_name.setText(bluetoothModel.getBluetoothName());
    }

    class RecyclerShowAdapterHolder extends BaseRecyclerViewAdapter.BaseRecyclerViewAdapterHolder{

        TextView tv_blue_list_name;
        TextView tv_blue_list_address;

        public RecyclerShowAdapterHolder(View itemView) {
            super(itemView);
            tv_blue_list_name = itemView.findViewById(R.id.tv_blue_list_name);
            tv_blue_list_address = itemView.findViewById(R.id.tv_blue_list_address);
        }
    }
}
