package com.example.duan1.user.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.duan1.R;
import com.example.duan1.activity.DetailProductActivity;
import com.example.duan1.admin.ui.fragment.BaseFragment;
import com.example.duan1.dao.CustomerDAO;
import com.example.duan1.dao.ProductDAO;
import com.example.duan1.databinding.FragmentHomeCustomerBinding;
import com.example.duan1.eventbus.Search;
import com.example.duan1.model.Employee;
import com.example.duan1.model.Product;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment<FragmentHomeCustomerBinding> {
    public static final String TAG = "Trang chủ";
    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_customer;
    }

    List<Product> list;
    ProductDAO productDAO;
    @Override
    protected void initEvent() {
        binding.rcvHomePage1.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        binding.rcvHomePage1.setAdapter(new HomeAdapter(getContext(), list) {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), DetailProductActivity.class);
                intent.putExtra("product_id", list.get(position).getId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {
        list = new ArrayList<>();
        productDAO = new ProductDAO(getContext());

        list = productDAO.getAllCus();
    }

    @Override
    public String getTAG() {
        return TAG;
    }

    //nhận sự kiện eventbus
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this); // Đăng ký để nhận sự kiện
    }
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this); // Hủy đăng ký khi Fragment không còn hiển thị
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Search search) {
        List<Product> templist = productDAO.getAllCus();
        list.clear();
        for (Product p : templist) {
            if (p.getName().toLowerCase().contains(search.getText().toLowerCase())) {
                list.add(p);
            }
        }
        if(list.isEmpty()){
            binding.tvNoInf.setVisibility(View.VISIBLE);
        }else {
            binding.tvNoInf.setVisibility(View.GONE);
        }
        binding.rcvHomePage1.getAdapter().notifyDataSetChanged();
    }
}
