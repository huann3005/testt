package com.example.duan1.admin.ui.fragment.employeemanagement;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.duan1.R;
import com.example.duan1.dao.EmployeeDAO;
import com.example.duan1.databinding.ItemEmployeeManagerBinding;
import com.example.duan1.model.Employee;

import java.util.ArrayList;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {
    private Context context;
    private List<Employee> list;

    public EmployeeAdapter(Context context, List<Employee> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEmployeeManagerBinding binding = ItemEmployeeManagerBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employee employee = list.get(position);
        holder.binding.tvTaiKhoanEm.setText("Email: " + employee.getEmail());
        holder.binding.tvHoTenEm.setText("Họ tên: " + (employee.getName() == null ? "" : employee.getName()));
        holder.binding.tvSDTEm.setText("SĐT: " + (employee.getPhone() == null ? "" : employee.getPhone()));
        holder.binding.tvDiaChiEm.setText("Địa chỉ: " + (employee.getAddress() == null ? "" : employee.getAddress()));
        holder.binding.tvCCCDEm.setText("CCCD: " + (employee.getCitizenshipID() == null ? "" : employee.getCitizenshipID()));
        if(employee.getStatus().equals("active")){
            holder.binding.tvTrangThaiEm.setText("Đang làm việc");
        }else {
            holder.binding.tvTrangThaiEm.setText("Đã nghỉ việc");
        }

//        holder.binding.tvTrangThaiEm.setText(employee.getStatus());
        holder.binding.tvNgayVaoLamEm.setText("Ngày vào làm: " + employee.getDate());
        holder.binding.imgEm.setImageResource(R.drawable.baseline_person_24_ccc);

        if(employee.getImage() != null){
            Glide.with(context).load(employee.getImage()).into(holder.binding.imgEm);
        }

        if (employee.getStatus().equals("active")) {
            holder.binding.shapeStatusCus.setBackgroundColor(context.getResources().getColor(R.color.active));
        } else {
            holder.binding.shapeStatusCus.setBackgroundColor(context.getResources().getColor(R.color.inactive));
        }

        holder.binding.ivUpdate.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_update_employee, null);
            builder.setView(view);
            builder.create();
            AlertDialog alertDialog = builder.create();

            EditText edtEmail = view.findViewById(R.id.edtTaiKhoan_up_em);
            EditText edtHoTen = view.findViewById(R.id.edtHoTen_up_em);
            EditText edtSDT = view.findViewById(R.id.edtSDT_up_em);
            EditText edtDiaChi = view.findViewById(R.id.edtDiaChi_up_em);
            EditText edtCCCD = view.findViewById(R.id.edtCCCD_up_em);
            Spinner spinnerTrangThai = view.findViewById(R.id.spnTrangThai_up_em);
            Button btnUpdate = view.findViewById(R.id.btnLuu_up_em);
            Button btnCancel = view.findViewById(R.id.btnHuy_up_em);
            ImageView imgEm_up = view.findViewById(R.id.imgEm_up);

            edtHoTen.setText(employee.getName());
            edtEmail.setText(employee.getEmail());
            edtSDT.setText(employee.getPhone());
            edtDiaChi.setText(employee.getAddress());
            edtCCCD.setText(employee.getCitizenshipID());
            imgEm_up.setImageResource(R.drawable.baseline_person_24_ccc);

            if(employee.getImage() != null){
            Glide.with(context).load(employee.getImage()).into(imgEm_up);
            }

            List<String> data = new ArrayList<>();
            data.add("Đang làm việc");
            data.add("Đã nghỉ việc");

            // Tạo Adapter để đổ dữ liệu vào Spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, data);

            // Định dạng Spinner
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Gán Adapter cho Spinner
            spinnerTrangThai.setAdapter(adapter);
            spinnerTrangThai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i == 0){
                        employee.setStatus("active");
                    }else {
                        employee.setStatus("inactive");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            if(employee.getStatus().equals("active")) {
                spinnerTrangThai.setSelection(0);
            } else {
                spinnerTrangThai.setSelection(1);
            }

            btnUpdate.setOnClickListener(v1 -> {
                EmployeeDAO employeeDAO = new EmployeeDAO(context);
//                employee.setStatus(spinnerTrangThai.getSelectedItem().toString());
                if(employeeDAO.updateStatus(employee, String.valueOf(employee.getId()))) {
                    Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                    alertDialog.dismiss();
                }else {
                    Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemEmployeeManagerBinding binding;
        public ViewHolder(@NonNull ItemEmployeeManagerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
