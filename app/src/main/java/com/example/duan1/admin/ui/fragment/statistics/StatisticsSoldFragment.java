package com.example.duan1.admin.ui.fragment.statistics;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.duan1.R;
import com.example.duan1.admin.ui.fragment.BaseFragment;
import com.example.duan1.dao.BillDAO;
import com.example.duan1.dao.ProductDAO;
import com.example.duan1.databinding.FragmentStatisticsBinding;
import com.example.duan1.databinding.FragmentStatisticsSoldBinding;
import com.example.duan1.model.Product;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatisticsSoldFragment extends BaseFragment<FragmentStatisticsSoldBinding> {
    public static String TAG = "Thống kê bán hàng";

    public static StatisticsSoldFragment newInstance() {

        Bundle args = new Bundle();

        StatisticsSoldFragment fragment = new StatisticsSoldFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_statistics_sold;
    }

    List<Product> list;
    ProductDAO productDAO;
    String sort = "DESC";
    @Override
    protected void initEvent() {
//        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i == 0){
//                    sort = "ASC";
//                }else if(i == 1){
//                    sort = "DESC";
//                }
//                list = productDAO.getAllByQuantitySold(sort);
//                binding.rcv.setAdapter(new StaticsSoldAdapter(list, getActivity()));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
    }

    @Override
    protected void initData() {
        //add data into spinner
//        List<String> data = new ArrayList<>();
//        data.add("Bán đỡ chạy");
//        data.add("Bán chạy");
//
//        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, data);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        binding.spinner.setAdapter(spinnerAdapter);

        productDAO = new ProductDAO(getActivity());
        list = productDAO.getAllByQuantitySold(sort);
        binding.rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcv.setAdapter(new StaticsSoldAdapter(list, getActivity()));
    }

    @Override
    public String getTAG() {
        return TAG;
    }
}
