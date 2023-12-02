package com.example.duan1.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.duan1.R;
import com.example.duan1.Utils;
import com.example.duan1.admin.ui.activity.BaseActivity;
import com.example.duan1.dao.CustomerDAO;
import com.example.duan1.databinding.ActivityChooseBinding;

public class ChooseActivity extends BaseActivity<ActivityChooseBinding> {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_choose;
    }

    @Override
    protected void initEvent() {
        Intent intent = new Intent(this, LoginActivity.class);
        binding.btnCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra(Utils.TYPE, Utils.CUSTOMER);
                startActivity(intent);
            }
        });
        binding.btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra(Utils.TYPE, Utils.SHOP);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {

    }
}
