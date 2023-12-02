package com.example.duan1.user.fragment.Cart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.duan1.R;
import com.example.duan1.activity.DetailProductActivity;
import com.example.duan1.dao.CartDAO;
import com.example.duan1.databinding.FragmentCartBinding;
import com.example.duan1.databinding.ItemCartBinding;
import com.example.duan1.model.Cart;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public abstract class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Cart> list;
    String email;

    public abstract void click(int totalPrice);

    public abstract void clickBtnReduce();

    public abstract void onItemClick(int position);

    public CartAdapter(Context context, ArrayList<Cart> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartBinding binding = ItemCartBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CartAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartDAO cartDAO = new CartDAO(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

        Cart cart = list.get(position);
        holder.binding.tvName.setText(cart.getName());
        holder.binding.tvPrice.setText(cart.getPrice() + " Đ");
        holder.binding.tvQuantity.setText("" + cart.getQuantity());
        if (cart.getImage() != null) {
            Glide.with(context).load(cart.getImage()).into(holder.binding.ivImageCart);
        } else {
            Glide.with(context).load(R.drawable.improduct1).into(holder.binding.ivImageCart);
        }

        //nếu số lượng = 0 thì k giảm đc nữa
        if (list.get(holder.getLayoutPosition()).getQuantity() == 0) {
            holder.binding.btnReduce.setEnabled(false);
        } else
            holder.binding.btnReduce.setEnabled(true);

        //nếu số lượng của sản phẩm bị giảm về 0 thì hiện thông báo xóa sản phẩm
        if (list.get(holder.getLayoutPosition()).getQuantity() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Bạn chắc chắn muốn xóa sản phẩm");


            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (cartDAO.augmentQuantity(list.get(holder.getAdapterPosition()), email)) {
                        list.clear();
                        list.addAll(cartDAO.getAllCartCus(email));
                        notifyDataSetChanged();
                    }
                }
            });

            builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (cartDAO.deleteCart(list.get(holder.getAdapterPosition()).getId(), email)) {
                        Toast.makeText(context, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        list.clear();
                        list.addAll(cartDAO.getAllCartCus(email));
                        notifyDataSetChanged();
                        clickBtnReduce();
                    }
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        }

        //tính tổng tiền
        int totalPrice = 0;
        for (Cart item : list) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        click(totalPrice);

        holder.binding.btnAgument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cartDAO.augmentQuantity(list.get(holder.getAdapterPosition()), email)) {
                    list.clear();
                    list.addAll(cartDAO.getAllCartCus(email));
                    notifyDataSetChanged();
                }
            }
        });
        holder.binding.btnReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cartDAO.reduceQuantity(list.get(holder.getAdapterPosition()), email)) {
                    list.clear();
                    list.addAll(cartDAO.getAllCartCus(email));
                    notifyDataSetChanged();
                }
            }
        });

        holder.binding.llLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailProductActivity.class);
                intent.putExtra("product_id", list.get(holder.getAdapterPosition()).getIdProduct());
                context.startActivity(intent);
            }
        });
        holder.binding.tvQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int totalPrice = 0;
                for (Cart item : list) {
                    totalPrice += item.getPrice() * item.getQuantity();
                }
                click(totalPrice);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemCartBinding binding;

        public ViewHolder(@NonNull ItemCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

