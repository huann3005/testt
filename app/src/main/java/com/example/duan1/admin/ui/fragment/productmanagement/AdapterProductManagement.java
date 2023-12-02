package com.example.duan1.admin.ui.fragment.productmanagement;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.duan1.R;
import com.example.duan1.dao.ProductDAO;
import com.example.duan1.databinding.ItemProductManagerBinding;
import com.example.duan1.model.Cart;
import com.example.duan1.model.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AdapterProductManagement extends RecyclerView.Adapter<AdapterProductManagement.ViewHolder> {
    private Context context;
    private List<Product> list;
    public abstract void click(int position);
    public AdapterProductManagement(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductManagerBinding binding = ItemProductManagerBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = list.get(position);
        holder.binding.tvTitleProduct.setText(product.getName());
        holder.binding.tvPriceProduct.setText("Đơn giá: " + product.getPrice() + " VND");
        holder.binding.tvQuantityProduct.setText("Số lượng đã bán: " + product.getQuantitySold() + "");
        holder.binding.tvStatusProduct.setText("Trạng thái: "+product.getStatus());

        if(product.getImage() != null){
            Glide.with(context).load(product.getImage()).into(holder.binding.ivImageProduct);
        }else{
            holder.binding.ivImageProduct.setImageResource(R.drawable.improduct1);
        }

        //chọn lại ảnh
        holder.binding.ivImageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        holder.itemView.setOnClickListener(v -> {
            click(position);
        });
    }
    public void sort2(){
        Collections.sort(list, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                if (o1.getPrice() > o2.getPrice()){
//                    Toast.makeText(context, "Đã sắp xếp tăng dần", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                    return 1;
                }else {
                    if (o1.getPrice() == o2.getPrice()){
                        return 0;
                    }else return -1;
                }
            }
        });
    }
    public void sort1(){
        Collections.sort(list, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                if (o1.getPrice() < o2.getPrice()){
//                    Toast.makeText(context, "Đã sắp xếp giảm dần", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                    return 1;
                }else {
                    if (o1.getPrice() == o2.getPrice()){
                        return 0;
                    }else return -1;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemProductManagerBinding binding;

        public ViewHolder(@NonNull ItemProductManagerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
