package com.example.duan1.admin.ui.fragment.customermanagement;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.R;
import com.example.duan1.admin.ui.fragment.employeemanagement.EmployeeAdapter;
import com.example.duan1.dao.CustomerDAO;
import com.example.duan1.databinding.FragmentCustomerManagementBinding;
import com.example.duan1.admin.ui.fragment.BaseFragment;
import com.example.duan1.eventbus.Search;
import com.example.duan1.model.Customer;
import com.example.duan1.model.Product;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class CustomerManagementFragment extends BaseFragment<FragmentCustomerManagementBinding> {
    public static String TAG = "Quản lý khách hàng";
    CustomerAdapter adapter;
    CustomerDAO customerDAO;
    List<Customer> list;

    public static CustomerManagementFragment newInstance() {

        Bundle args = new Bundle();

        CustomerManagementFragment fragment = new CustomerManagementFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_customer_management;
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void initData() {
        loadData();
        filterStatus();
    }

    @Override
    public String getTAG() {
        return TAG;
    }

    public void loadData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.rccCustomer.setLayoutManager(linearLayoutManager);
        customerDAO = new CustomerDAO(getContext());
        list = customerDAO.getAll();
        adapter = new CustomerAdapter(getContext(), list);
        binding.rccCustomer.setAdapter(adapter);
    }

    public void filterStatus() {
        // Tạo danh sách dữ liệu
        List<String> data = new ArrayList<>();
        data.add("Chọn cách sắp xếp");
        data.add("Active");
        data.add("Inactive");
        data.add("All");

        // Tạo Adapter để đổ dữ liệu vào Spinner
        ArrayAdapter<String> adapterSPN = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, data);

        // Định dạng Spinner
        adapterSPN.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spnFilter.setAdapter(adapterSPN);

        binding.spnFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String status = binding.spnFilter.getSelectedItem().toString();
                if (status.equals("Active")) {
                    list = customerDAO.getAllByStatus("active");
                    adapter = new CustomerAdapter(getContext(), list);
                    binding.rccCustomer.setAdapter(adapter);
                } else if (status.equals("Inactive")) {
                    list = customerDAO.getAllByStatus("inactive");
                    adapter = new CustomerAdapter(getContext(), list);
                    binding.rccCustomer.setAdapter(adapter);
                } else if (status.equals("All") || status.equals("Chọn cách sắp xếp")) {
                    list = customerDAO.getAll();
                    adapter = new CustomerAdapter(getContext(), list);
                    binding.rccCustomer.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
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
        List<Customer> templist = customerDAO.getAll();
        list.clear();
        for (Customer customer : templist) {
            if (customer.getName().toLowerCase().contains(search.getText().toLowerCase())) {
                list.add(customer);
            }
        }
        if(list.isEmpty()){
            binding.tvNoInf.setVisibility(View.VISIBLE);
        }else {
            binding.tvNoInf.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }
}
