package com.example.duan1.user.fragment.bill;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.duan1.R;
import com.example.duan1.activity.BillDetailActivity;
import com.example.duan1.admin.ui.fragment.BaseFragment;
import com.example.duan1.dao.BillDAO;
import com.example.duan1.dao.BillDetailDAO;
import com.example.duan1.databinding.FragmentOrderCustomerBinding;
import com.example.duan1.model.Bill;
import com.example.duan1.model.BillDetail;

import java.util.ArrayList;
import java.util.List;

public class BillFragment extends BaseFragment<FragmentOrderCustomerBinding> {
    public static final String TAG = "Đơn hàng";
    public static BillFragment newInstance() {
        
        Bundle args = new Bundle();
        
        BillFragment fragment = new BillFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_order_customer;
    }

    @Override
    protected void initEvent() {
        binding.rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcv.setAdapter(new BillAdapter(list, getContext()) {
            @Override
            public void onItemClick(int id) {
                Intent intent = new Intent(getContext(), BillDetailActivity.class);
                intent.putExtra("id_bill", id + "");
                intent.putExtra("text_btn", "none");
                startActivity(intent);
            }
        });
    }
    BillDAO billDAO;
    List<Bill> list;
    @Override
    protected void initData() {
        list = new ArrayList<>();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("USER", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        billDAO = new BillDAO(getContext());
        list = billDAO.getAllCus(email);
    }

    @Override
    public String getTAG() {
        return TAG;
    }
}
