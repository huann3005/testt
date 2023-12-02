package com.example.duan1.admin.ui.fragment.ordermanagement;

import android.content.Context;
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
import com.example.duan1.model.Bill;
import com.example.duan1.model.BillDetail;
import com.example.duan1.model.Product;

import java.util.List;

public abstract class OrderManagerAdapter extends RecyclerView.Adapter<OrderManagerAdapter.BillViewHolder> {
    List<Bill> list;
    BillDAO billDAO;
    ProductDAO productDAO;
    EmployeeDAO employeeDAO;
    BillDetailDAO billDetailDAO;
    Context context;

    public void setData(List<Bill> list){
        this.list = list;
    }
    public abstract void onItemClick(int position);

    public OrderManagerAdapter(List<Bill> list, Context context) {
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
        holder.llDate.setVisibility(View.GONE);
        holder.llAddress.setVisibility(View.GONE);
        if(bill.getStatus().equals("Xác nhận thanh toán")){
            holder.tvStatus.setText("Trạng thái: " + "Chờ xác nhận thanh toán");
        }else {
            holder.tvStatus.setText("Trạng thái: " + bill.getStatus());
        }

        holder.tvPaymentMethod.setText("Phương thức thanh toán: " + bill.getPaymentMethod());

        BillDetailDAO billDetailDAO = new BillDetailDAO(context);
        List<BillDetail> listBillDetail = billDetailDAO.getAllByIdBill(String.valueOf(bill.getId()));

        if(listBillDetail.size() > 0){
            int idPro = listBillDetail.get(0).getIdProduct();
            Product product = productDAO.getID(idPro);

            if(product.getImage() != null){
                Glide.with(context).load(product.getImage()).into(holder.img);
            }else {
                Glide.with(context).load(R.drawable.improduct1).into(holder.img);
            }
        }

        holder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public class BillViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView tvIdBill, tvNameEmployee, tvTotalPrice, tvDate, tvAddress, tvStatus, tvPaymentMethod;
        View llDate, llAddress, llItem;
        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.ivBill);
            tvIdBill = itemView.findViewById(R.id.tvIdBill);
            tvNameEmployee = itemView.findViewById(R.id.tvNameEmployee);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            llDate = itemView.findViewById(R.id.llDate);
            llAddress = itemView.findViewById(R.id.llAddress);
            llItem = itemView.findViewById(R.id.llItem);
            tvPaymentMethod = itemView.findViewById(R.id.tvpaymentMethod);
        }
    }
}
