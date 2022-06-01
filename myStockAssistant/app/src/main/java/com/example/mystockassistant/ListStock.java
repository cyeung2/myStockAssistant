package com.example.mystockassistant;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListStock extends RecyclerView.Adapter<ViewStock> {

    private ArrayList<Stock> sList;
    private MainActivity mainAct;

    public ListStock(MainActivity mainAct, ArrayList<Stock> sList) {
        this.sList = sList;
        this.mainAct = mainAct;
    }

    @NonNull
    @Override
    public ViewStock onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewstonk, parent, false);
        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);
        return new ViewStock(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewStock holder, int position) {
        Stock s = sList.get(position);
        holder.symbol.setText(s.getStockSymbol());
        holder.name.setText(s.getCName());
        holder.change.setText(Double.toString(s.getPChange()));
        holder.percent.setText("(" + Double.toString(s.getChangePercent()) + "%)");
        holder.price.setText(Double.toString(s.getPrice()));
        if (s.getValue().equals("Positive")) {
            holder.arrow.setImageResource(R.drawable.plus);
            holder.symbol.setTextColor(Color.GREEN);
            holder.name.setTextColor(Color.GREEN);
            holder.change.setTextColor(Color.GREEN);
            holder.percent.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
        }
        else {
            holder.arrow.setImageResource(R.drawable.minus);
            holder.symbol.setTextColor(Color.RED);
            holder.name.setTextColor(Color.RED);
            holder.change.setTextColor(Color.RED);
            holder.percent.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return sList.size();
    }
}
