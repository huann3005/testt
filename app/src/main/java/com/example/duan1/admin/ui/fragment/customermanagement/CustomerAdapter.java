package com.example.duan1.admin.ui.fragment.customermanagement;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.duan1.dao.CustomerDAO;
import com.example.duan1.databinding.ItemCustomerManagementBinding;
import com.example.duan1.model.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {
    private Context context;
    private List<Customer> list;

    public CustomerAdapter(Context context, List<Customer> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCustomerManagementBinding binding = ItemCustomerManagementBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Customer customer = list.get(holder.getAdapterPosition());
        holder.binding.tvHoTenCus.setText("Họ tên: " + customer.getName());
        holder.binding.tvDiaChiCus.setText("Dịa chỉ: " + customer.getAddress());
        holder.binding.tvNgaySinhCus.setText("Ngày sinh: " + customer.getBirthday());
        holder.binding.tvSDTCus.setText("SĐT: " + customer.getPhone());
        holder.binding.tvTaiKhoanCus.setText("Email: " + customer.getEmail());
        holder.binding.tvTrangThaiCus.setText("Trạng thái: " + customer.getStatus());
        holder.binding.imgCus.setImageResource(R.drawable.baseline_person_24_ccc);

        if(customer.getImage() != null){
            Glide.with(context).load(customer.getImage()).into(holder.binding.imgCus);
        }

        if(customer.getStatus().equals("active")) {
            holder.binding.shapeStatusCus.setBackgroundColor(context.getResources().getColor(R.color.active));
        }
        else {
            holder.binding.shapeStatusCus.setBackgroundColor(context.getResources().getColor(R.color.inactive));
        }

        holder.binding.ivUpdateCus.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_update_customer, null);
            builder.setView(view);
            builder.create();
            AlertDialog alertDialog = builder.create();

            EditText edtName = view.findViewById(R.id.edtName_up_cus);
            EditText edtPhone = view.findViewById(R.id.edtPhone_up_cus);
            EditText edtAddress = view.findViewById(R.id.edtAddress_up_cus);
            EditText edtEmail = view.findViewById(R.id.edtEmail_up_cus);
            EditText edtBirthday = view.findViewById(R.id.edtBirthday_up_em);
            Spinner spnStatus = view.findViewById(R.id.spnStatus_up_cus);
            Button btnCancel = view.findViewById(R.id.btnCancel_up_cus);
            Button btnSave = view.findViewById(R.id.btnSave_up_cus);
            ImageView imgCus = view.findViewById(R.id.imgCus_up);

            //đổ dữ liệu lên spinner
            List<String> spinnerData = new ArrayList<>();
            spinnerData.add("active");
            spinnerData.add("inactive");

            // Create an ArrayAdapter to populate the spinner with data
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerData);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Set the adapter to the spinner
            spnStatus.setAdapter(spinnerAdapter);

            edtName.setText(customer.getName());
            edtPhone.setText(customer.getPhone());
            edtAddress.setText(customer.getAddress());
            edtEmail.setText(customer.getEmail());
            edtBirthday.setText(customer.getBirthday());
            spnStatus.setSelection(spinnerData.indexOf(customer.getStatus()));
            imgCus.setImageResource(R.drawable.baseline_person_24_ccc);

            if (customer.getImage() != null) {
                Glide.with(context).load(customer.getImage()).into(imgCus);
            }
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   String status = spnStatus.getSelectedItem().toString();
                   customer.setStatus(status);

                   CustomerDAO customerDAO = new CustomerDAO(context);
                   if(customerDAO.updateStatus(customer, String.valueOf(customer.getId()))) {
                       Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                       notifyDataSetChanged();
                       alertDialog.dismiss();
                   }else {
                       Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                   }
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
        private ItemCustomerManagementBinding binding;
        public ViewHolder(@NonNull ItemCustomerManagementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
