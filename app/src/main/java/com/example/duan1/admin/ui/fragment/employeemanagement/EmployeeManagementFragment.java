package com.example.duan1.admin.ui.fragment.employeemanagement;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.duan1.R;
import com.example.duan1.admin.ui.fragment.BaseFragment;
import com.example.duan1.dao.AccountDAO;
import com.example.duan1.dao.EmployeeDAO;
import com.example.duan1.databinding.FragmentEmployeeManagementBinding;
import com.example.duan1.eventbus.Search;
import com.example.duan1.model.Account;
import com.example.duan1.model.Employee;
import com.example.duan1.model.Product;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kotlin.text.Regex;

public class EmployeeManagementFragment extends BaseFragment<FragmentEmployeeManagementBinding> {
    public static String TAG = "Quản lý nhân viên";
    EmployeeDAO employeeDAO;
    List<Employee> list;
    EmployeeAdapter adapter;

    public static EmployeeManagementFragment newInstance() {

        Bundle args = new Bundle();

        EmployeeManagementFragment fragment = new EmployeeManagementFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_employee_management;
    }

    @Override
    protected void initEvent() {
        addEmployee();
        filterStatus();
    }

    @Override
    protected void initData() {
        loadData();
    }

    @Override
    public String getTAG() {
        return TAG;
    }

    public void loadData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.rccEmployee.setLayoutManager(linearLayoutManager);
        employeeDAO = new EmployeeDAO(getContext());
        list = employeeDAO.getAll();
        adapter = new EmployeeAdapter(getContext(), list);
        binding.rccEmployee.setAdapter(adapter);

    }

    public void addEmployee() {
        binding.fltAddEm.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.dialog_add_employee, null);
            builder.setView(view);
            AlertDialog alertDialog = builder.create();

            EditText edtEmail = view.findViewById(R.id.edtEmail_add_em);
            Spinner spnRole = view.findViewById(R.id.spnRole_add_em);
            Button btnThem = view.findViewById(R.id.btnLuu_add_em);
            Button btnHuy = view.findViewById(R.id.btnHuy_add_em);

            // Tạo danh sách dữ liệu
            List<String> data = new ArrayList<>();
            data.add("employeeSp");
            data.add("employeeChef");

            // Tạo Adapter để đổ dữ liệu vào Spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, data);

            // Định dạng Spinner
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnRole.setAdapter(adapter);

            btnThem.setOnClickListener(v1 -> {
                Employee employee = new Employee();
                String email = edtEmail.getText().toString();
                String role = spnRole.getSelectedItem().toString();

                if (email.isEmpty()) {
                    Toast.makeText(getContext(), "Không để trống Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                AccountDAO accountDAO = new AccountDAO(getContext());
                if (accountDAO.insertEmployee(new Account(email, "chicken", role))) {
                    employee.setEmail(email);
                    //get current date
                    Date currentDate = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String formattedDate = dateFormat.format(currentDate);

                    employee.setDate(formattedDate);

                    if (employeeDAO.insert(employee)) {
                        ;
                        Toast.makeText(getContext(), "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        loadData();
                    } else {
                        Toast.makeText(getContext(), "Thêm nhân viên thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            });
            btnHuy.setOnClickListener(v1 -> alertDialog.dismiss());
            alertDialog.show();
        });
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
                    list = employeeDAO.getAllByStatus("active");
                    adapter = new EmployeeAdapter(getContext(), list);
                    binding.rccEmployee.setAdapter(adapter);
                }else if (status.equals("Inactive")) {
                    list = employeeDAO.getAllByStatus("inactive");
                    adapter = new EmployeeAdapter(getContext(), list);
                    binding.rccEmployee.setAdapter(adapter);
                }else if (status.equals("All") || status.equals("Chọn cách sắp xếp")) {
                    list = employeeDAO.getAll();
                    adapter = new EmployeeAdapter(getContext(), list);
                    binding.rccEmployee.setAdapter(adapter);
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
        List<Employee> templist = employeeDAO.getAll();
        list.clear();
        for (Employee e : templist) {
            if (e.getName().toLowerCase().contains(search.getText().toLowerCase())) {
                list.add(e);
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
