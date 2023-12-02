package com.example.duan1.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.duan1.R;
import com.example.duan1.admin.ui.activity.BaseActivity;
import com.example.duan1.dao.CartDAO;
import com.example.duan1.dao.ProductDAO;
import com.example.duan1.dao.ProductDetailDAO;
import com.example.duan1.databinding.ActivityDetailProductBinding;
import com.example.duan1.model.Product;
import com.example.duan1.model.ProductDetail;


public class DetailProductActivity extends BaseActivity<ActivityDetailProductBinding> {
    ProductDetail productDetail;
    Product product;
    int productId;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail_product;
    }

    @Override
    protected void initEvent() {
        SharedPreferences sharedPreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        binding.btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CartDAO cartDAO = new CartDAO(DetailProductActivity.this);
                if (cartDAO.insertToCart(product, email)) {
                    Toast.makeText(DetailProductActivity.this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailProductActivity.this, "Thêm sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        productId = intent.getIntExtra("product_id", 0);
        ProductDetailDAO productDetailDAO = new ProductDetailDAO(this);
        ProductDAO productDAO = new ProductDAO(this);
        product = productDAO.getID(productId);
        productDetail = productDetailDAO.getID(productId);

        if (productDetail != null) {
            binding.tvProductName.setText(product.getName());
            binding.tvDiscreption.setText(productDetail.getDescription());
            binding.tvPrice.setText("Giá tiền :" +product.getPrice() + " VND");
            binding.tvQuantitySold.setText( product.getQuantitySold() + " đã bán");

            if(product.getImage() != null){
                Glide.with(this).load(product.getImage()).into(binding.ivImageProductDetail);
            }else {
                Glide.with(this).load(R.drawable.improduct1).into(binding.ivImageProductDetail);
            }
        }
    }
}