package com.example.mystockassistant;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class ViewStock extends RecyclerView.ViewHolder {

    TextView symbol;
    TextView name;
    TextView price;
    TextView change;
    TextView percent;
    ImageView arrow;

    public ViewStock(@NonNull View itemView) {
        super(itemView);
        symbol = itemView.findViewById(R.id.stock_symbol);
        name = itemView.findViewById(R.id.stock_name);
        price = itemView.findViewById(R.id.stock_price);
        change = itemView.findViewById(R.id.stock_change);
        percent = itemView.findViewById(R.id.stock_percent);
        arrow = itemView.findViewById(R.id.arrowPic);
    }
}
