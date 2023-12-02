package com.example.duan1.admin.ui.fragment.ordermanagement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.duan1.BillStatus;
import com.example.duan1.R;
import com.example.duan1.activity.BillDetailActivity;
import com.example.duan1.admin.ui.fragment.BaseFragment;
import com.example.duan1.dao.BillDAO;
import com.example.duan1.databinding.FragmentWaitingBinding;
import com.example.duan1.model.Bill;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class WaitingFragment extends BaseFragment<FragmentWaitingBinding> {
    public static WaitingFragment newInstance() {

        Bundle args = new Bundle();

        WaitingFragment fragment = new WaitingFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_waiting;
    }

    @Override
    protected void initEvent() {
    }

    BillDAO billDAO;
    List<Bill> list;
    OrderManagerAdapter adapter;
    @Override
    protected void initData() {
        billDAO = new BillDAO(getContext());
        list = billDAO.getAllWithStatus(BillStatus.WAITING);
        adapter = new OrderManagerAdapter(list, getContext()) {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), BillDetailActivity.class);
                intent.putExtra("id_bill", list.get(position).getId() + "");
                intent.putExtra("text_btn", "Đang làm");
                onPause();
                startActivity(intent);
            }
        };
        binding.rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcv.setAdapter(adapter);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadData(SendEvent event){
        list = billDAO.getAllWithStatus(BillStatus.WAITING);
        adapter.setData(list);
        binding.rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public String getTAG() {
        return "Đang chờ";
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