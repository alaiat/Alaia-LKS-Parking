package com.lksnext.parkingalaiat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReservaAdapterOld extends RecyclerView.Adapter<ReservaViewHolder> {
    private List<String> reservas;

    public ReservaAdapterOld(List<String> reservas) {
        this.reservas = reservas;
    }

    @Override
    public ReservaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reserva3, parent, false);
        return new ReservaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReservaViewHolder holder, int position) {
        String task = reservas.get(position);
        holder.render(task);
    }

    @Override
    public int getItemCount() {
        return reservas.size();
    }
}