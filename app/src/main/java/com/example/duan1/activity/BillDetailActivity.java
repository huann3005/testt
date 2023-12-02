package com.example.duan1.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.duan1.BillStatus;
import com.example.duan1.R;
import com.example.duan1.Utils;
import com.example.duan1.activity.adapter.BillDetailAdapter;
import com.example.duan1.admin.ui.activity.BaseActivity;
import com.example.duan1.admin.ui.fragment.ordermanagement.SendEvent;
import com.example.duan1.dao.BillDAO;
import com.example.duan1.dao.BillDetailDAO;
import com.example.duan1.dao.EmployeeDAO;
import com.example.duan1.databinding.ActivityBillDetailBinding;
import com.example.duan1.model.BillDetail;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class BillDetailActivity extends BaseActivity<ActivityBillDetailBinding> {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_bill_detail;
    }

    @Override
    protected void initEvent() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    String idBill;
    List<BillDetail> list;
    BillDetailDAO dao;
    @Override
    protected void initData() {
        list = new ArrayList<>();
        dao = new BillDetailDAO(this);

        String textBtn;

        Intent intent = getIntent();
        idBill = intent.getStringExtra("id_bill");
        textBtn = intent.getStringExtra("text_btn");

        list = dao.getAllByIdBill(idBill);
        binding.rcv.setLayoutManager(new LinearLayoutManager(this));
        binding.rcv.setAdapter(new BillDetailAdapter(list, this));
        binding.btnChangeStatus.setText(textBtn);

        if(textBtn.trim().equals("none")){
            binding.btnChangeStatus.setVisibility(View.GONE);
        }

        binding.btnChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatus();
                initData();
            }
        });
    }
    BillDAO billDAO;
    void changeStatus(){
        billDAO = new BillDAO(this);

        SharedPreferences sharedPreferences = getSharedPreferences(Utils.EMAIL, MODE_PRIVATE);
        String email = sharedPreferences.getString(Utils.EMAIL, "");

        EmployeeDAO employeeDAO = new EmployeeDAO(this);

        if(binding.btnChangeStatus.getText().toString().toLowerCase().equals("xác nhận thanh toán")){
            billDAO.updateStatus(BillStatus.WAITING, idBill);
        }
        else if(binding.btnChangeStatus.getText().toString().toLowerCase().equals("đang làm")){
            billDAO.updateStatus(BillStatus.DOING, idBill);
            billDAO.addEmployeeToBill(idBill, employeeDAO.getByEmail(email).getId() + "");
        }else if (binding.btnChangeStatus.getText().toString().toLowerCase().equals("hoàn thành")){
            billDAO.updateStatus(BillStatus.DONE, idBill);
        }
        EventBus.getDefault().post(new SendEvent());
        finish();
    }
}
