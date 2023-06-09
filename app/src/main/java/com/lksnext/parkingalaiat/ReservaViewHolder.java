package com.lksnext.parkingalaiat;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ReservaViewHolder  extends RecyclerView.ViewHolder{

    private TextView reserva;

    public ReservaViewHolder(View view) {
        super(view);
        reserva = view.findViewById(R.id.reserva);
    }

    public void render(String reservaT) {
        reserva.setText(reservaT);
    }



}
