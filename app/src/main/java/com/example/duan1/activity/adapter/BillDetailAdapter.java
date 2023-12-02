package com.example.duan1.activity.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.duan1.R;
import com.example.duan1.dao.ProductDAO;
import com.example.duan1.model.BillDetail;
import com.example.duan1.model.Product;

import java.util.List;

public class BillDetailAdapter extends RecyclerView.Adapter<BillDetailAdapter.BillDetailViewHolder> {

    List<BillDetail> list;
    ProductDAO productDAO;

    public BillDetailAdapter(List<BillDetail> list, Context context) {
        this.list = list;
        productDAO = new ProductDAO(context);
    }

    @NonNull
    @Override
    public BillDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BillDetailViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BillDetailViewHolder holder, int position) {
        BillDetail detail = list.get(position);

        holder.tvName.setText("Tên: " + (productDAO.getID(detail.getIdProduct()) != null ? productDAO.getID(detail.getIdProduct()).getName() : ""));
        holder.tvPrice.setText("Giá: " + detail.getPrice() + " VNĐ");
        holder.tvQuantity.setText("Số lượng: " + detail.getQuantity());
        holder.tvTotal.setText("Thành tiền: " + (detail.getQuantity() * detail.getPrice()) + " VNĐ");

        ProductDAO productDAO = new ProductDAO(holder.itemView.getContext());
        Product product = productDAO.getID(detail.getIdProduct());
        if(product.getImage() != null){
            Glide.with(holder.itemView).load(product.getImage()).into(holder.ivBillDetail);
        }else {
            Glide.with(holder.itemView).load(R.drawable.improduct1).into(holder.ivBillDetail);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BillDetailViewHolder extends RecyclerView.ViewHolder{
        ImageView ivBillDetail;
        TextView tvName, tvPrice, tvQuantity, tvTotal;

        public BillDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBillDetail = itemView.findViewById(R.id.ivBillDetail);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvTotal = itemView.findViewById(R.id.tv_total);
        }
    }
}
