package com.example.duan1.admin.ui.fragment.ordermanagement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.widget.Toast;

import com.example.duan1.BillStatus;
import com.example.duan1.R;
import com.example.duan1.activity.BillDetailActivity;
import com.example.duan1.admin.ui.fragment.BaseFragment;
import com.example.duan1.dao.BillDAO;
import com.example.duan1.databinding.FragmentConfirmBinding;
import com.example.duan1.model.Bill;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class ConfirmFragment extends BaseFragment<FragmentConfirmBinding> {
    public static ConfirmFragment newInstance() {
        Bundle args = new Bundle();
        ConfirmFragment fragment = new ConfirmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    BillDAO billDAO;
    List<Bill> list;
    OrderManagerAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_confirm;
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initData() {
        billDAO = new BillDAO(getContext());
        list = billDAO.getAllWithStatus(BillStatus.CONFIRM);
        adapter = new OrderManagerAdapter(list, getContext()) {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), BillDetailActivity.class);
                intent.putExtra("id_bill", list.get(position).getId() + "");
                intent.putExtra("text_btn", "Xác nhận thanh toán");
                onPause();
                startActivity(intent);
            }
        };
        binding.rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcv.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadData(SendEvent event) {
        list = billDAO.getAllWithStatus(BillStatus.CONFIRM);
        adapter.setData(list);
        binding.rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public String getTAG() {
        return "Xác nhận thanh toán";
    }
    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }
}