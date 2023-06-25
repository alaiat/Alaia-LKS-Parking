package com.lksnext.parkingalaiat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parkingalaiat.domain.Reserva;

import java.util.ArrayList;
import java.util.List;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ViewHolder> {
    private List<Reserva> reservas=new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;


    public ReservaAdapter(List<Reserva> reservas, Context context) {
        this.reservas = reservas;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }
    public ReservaAdapter() {

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = mInflater.inflate(R.layout.reserva_item, null);
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.reserva_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReservaAdapter.ViewHolder holder, final int position) {
        holder.bindData(reservas.get(position));
    }

    @Override
    public int getItemCount() {
        return reservas.size();
    }

    public void setItems(List<Reserva> items) {
        reservas = items;
        notifyDataSetChanged();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView startH, endH, date, status;

        ViewHolder(View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconImageView);
            startH = itemView.findViewById(R.id.startHour);
            endH = itemView.findViewById(R.id.endHour);
            date = itemView.findViewById(R.id.date);
            status = itemView.findViewById(R.id.status);
        }

        void bindData(final Reserva item) {
            //iconImage.setColorFilter(Color.parseColor(item.getColor()), PorterDuff.Mode.SRC_IN);
            startH.setText(item.getStartHour());
            endH.setText(item.getEndHour());
            date.setText(item.getDate());
            status.setText(item.getStatus());


        }


    }
}
