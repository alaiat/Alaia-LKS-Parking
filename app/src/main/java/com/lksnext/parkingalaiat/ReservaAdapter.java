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
        TextView startH, endH, date, number;

        ViewHolder(View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconImageView);
            startH = itemView.findViewById(R.id.startHour);
            endH = itemView.findViewById(R.id.endHour);
            date = itemView.findViewById(R.id.date);
            number = itemView.findViewById(R.id.number);

        }

        void bindData(final Reserva item) {
            //iconImage.setColorFilter(Color.parseColor(item.getColor()), PorterDuff.Mode.SRC_IN);
            String type=item.getType();
            if(type.equals("Normal")){
                iconImage.setImageResource(R.drawable.normal_imagen);


            }else if(type.equals("Motor" )){
                iconImage.setImageResource(R.drawable.moto_imagen);


            }else if(type.equals("Electric")){
                iconImage.setImageResource(R.drawable.electrico_imagen);

            }else if(type.equals("Handicapped")){
                iconImage.setImageResource(R.drawable.discapacitados_icono);
            }


            startH.setText(item.getStartHour());
            endH.setText(item.getEndHour());
            date.setText(item.getDate());
            number.setText("Nº"+item.getSpot().toString());


        }


    }
}
