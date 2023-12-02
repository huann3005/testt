package com.example.duan1.user.fragment.bill;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.duan1.R;
import com.example.duan1.dao.BillDAO;
import com.example.duan1.dao.BillDetailDAO;
import com.example.duan1.dao.EmployeeDAO;
import com.example.duan1.dao.ProductDAO;
import com.example.duan1.database.DBHelper;
import com.example.duan1.model.Bill;
import com.example.duan1.model.BillDetail;
import com.example.duan1.model.Product;

import java.util.List;

public abstract class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {
    private Context context;
    private List<Bill> list;
    BillDAO billDAO;
    ProductDAO productDAO;
    EmployeeDAO employeeDAO;
    BillDetailDAO billDetailDAO;


    public BillAdapter(List<Bill> list, Context context) {
        this.list = list;
        this.context = context;
        billDAO = new BillDAO(context);
        productDAO = new ProductDAO(context);
        employeeDAO = new EmployeeDAO(context);
        billDetailDAO = new BillDetailDAO(context);
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BillViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_placed, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = list.get(position);
        holder.tvIdBill.setText("Mã hóa đơn: " + String.valueOf(bill.getId()));
        if(bill.getIdEmployee() != null){
            holder.tvNameEmployee.setText("Nhân viên: " + (employeeDAO.getID(bill.getIdEmployee()) == null ? "" : employeeDAO.getID(bill.getIdEmployee()).getName()));
        }else {
            holder.tvNameEmployee.setText("Nhân viên: ");
        }
        holder.tvTotalPrice.setText("Tổng tiền: " + billDetailDAO.getTotalPrice(bill.getId()));

        BillDetailDAO billDetailDAO = new BillDetailDAO(context);
        List<BillDetail> listBillDetail = billDetailDAO.getAllByIdBill(String.valueOf(bill.getId()));
        int idPro = listBillDetail.get(0).getIdProduct();
        Product product = productDAO.getID(idPro);

        if(product.getImage() != null){
            Glide.with(context).load(product.getImage()).into(holder.ivBill);
        }else {
            Glide.with(context).load(R.drawable.improduct1).into(holder.ivBill);
        }

        Log.i("tongtien", String.valueOf(billDetailDAO.getTotalPrice(bill.getId())));
        holder.tvDate.setText(bill.getDate());
        holder.tvAddress.setText(bill.getShippingAddress());
        if(bill.getStatus().equals("Xác nhận thanh toán")){
            holder.tvStatus.setText("Trạng thái: " + "Chờ xác nhận thanh toán");
        }else {
            holder.tvStatus.setText("Trạng thái: " + bill.getStatus());
        }

        holder.tvpaymentMethod.setText("Phương thức thanh toán: " + bill.getPaymentMethod());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick(bill.getId());
            }
        });
    }

    public abstract void onItemClick(int id);

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public class BillViewHolder extends RecyclerView.ViewHolder{
        ImageView ivBill;
        TextView tvIdBill, tvNameEmployee, tvTotalPrice, tvDate, tvAddress, tvStatus, tvpaymentMethod;
        View layout;
        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBill = itemView.findViewById(R.id.ivBill);
            tvIdBill = itemView.findViewById(R.id.tvIdBill);
            tvNameEmployee = itemView.findViewById(R.id.tvNameEmployee);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvpaymentMethod = itemView.findViewById(R.id.tvpaymentMethod);
            layout = itemView.findViewById(R.id.llItem);
        }
    }
}
