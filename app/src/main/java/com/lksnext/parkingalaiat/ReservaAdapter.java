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
import com.lksnext.parkingalaiat.domain.ReservaOld;

import java.util.ArrayList;
import java.util.List;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ViewHolder> {
    private List<ReservaOld> reservas=new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;
    private OnItemClickListener onItemClickListener;


    public ReservaAdapter(List<ReservaOld> reservas, Context context) {
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

    public void setItems(List<ReservaOld> items) {
        reservas = items;
        notifyDataSetChanged();

    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });

        }

        void bindData(final ReservaOld item) {
            //iconImage.setColorFilter(Color.parseColor(item.getColor()), PorterDuff.Mode.SRC_IN);
            String type=item.getSpot().toString();
            if(type.equals("CAR")){
                iconImage.setImageResource(R.drawable.normal_imagen);


            }else if(type.equals("MOTORCYCLE" )){
                iconImage.setImageResource(R.drawable.moto_imagen);


            }else if(type.equals("ELECTRIC")){
                iconImage.setImageResource(R.drawable.electrico_imagen);

            }else if(type.equals("HANDICAPPED")){
                iconImage.setImageResource(R.drawable.discapacitados_icono);
            }


            startH.setText(item.getStartTime());
            endH.setText(item.getEndTime());
            date.setText(item.getDate());
            number.setText("Nº"+item.getSpot().getNumber());


        }


    }
}
