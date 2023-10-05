package com.lksnext.parkingalaiat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parkingalaiat.domain.CurrentParking;
import com.lksnext.parkingalaiat.domain.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
    private List<Booking> bookings = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;
    private OnItemClickListener onItemClickListener;


    public BookingAdapter(List<Booking> bookings, Context context) {
        this.bookings = bookings;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BookingAdapter.ViewHolder holder, final int position) {
        holder.bindData(bookings.get(position));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public void setItems(List<Booking> items) {
        bookings = items;
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

        void bindData(final Booking item) {
            //iconImage.setColorFilter(Color.parseColor(item.getColor()), PorterDuff.Mode.SRC_IN);
            String type = CurrentParking.getInstance().getTypeById(item.getSpot());

            if (type != null) {
                if (type.equals("CAR")) {
                    iconImage.setImageResource(R.drawable.normal_image);

                } else if (type.equals("MOTORCYCLE")) {
                    iconImage.setImageResource(R.drawable.moto_image);

                } else if (type.equals("ELECTRIC")) {
                    iconImage.setImageResource(R.drawable.electric_image);

                } else if (type.equals("HANDICAPPED")) {
                    iconImage.setImageResource(R.drawable.handicapped_image);
                }
            }

            startH.setText(item.getStartTime());
            endH.setText(item.getEndTime());
            date.setText(item.getDate());
            number.setText("Nº" + CurrentParking.getInstance().getNumById(item.getSpot()));
        }
    }
}
