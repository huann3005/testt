package com.example.duan1.admin.ui.fragment.statistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.duan1.R;
import com.example.duan1.model.Product;

import java.util.List;

public class StaticsSoldAdapter extends RecyclerView.Adapter<StaticsSoldAdapter.MyViewHolder> {
    List<Product> list;
    Context context;

    public StaticsSoldAdapter(List<Product> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_statics_sold, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product product = list.get(position);
        holder.tvName.setText(product.getName());
        holder.tvQuantitySold.setText("Đã bán: "+String.valueOf(product.getQuantitySold()));
        holder.tvPrice.setText(product.getPrice() + " VNĐ");
        if(product.getImage() == null){
            holder.imgProduct.setImageResource(R.drawable.improduct1);
        }else {
            Glide.with(context).load(product.getImage()).into(holder.imgProduct);
        }
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvQuantitySold, tvPrice;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img);
            tvName = itemView.findViewById(R.id.tv_name);
            tvQuantitySold = itemView.findViewById(R.id.tv_quantity_sold);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
