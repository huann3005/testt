package com.example.duan1.user.fragment.changepass;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.duan1.R;
import com.example.duan1.Utils;
import com.example.duan1.activity.ChooseActivity;
import com.example.duan1.admin.ui.fragment.BaseFragment;
import com.example.duan1.dao.AccountDAO;
import com.example.duan1.database.DBHelper;
import com.example.duan1.databinding.FragmentChangePasswordBinding;
import com.example.duan1.model.Account;

public class ChangePasswordFragment extends BaseFragment<FragmentChangePasswordBinding> {
    public static final String TAG = "Đổi mật khẩu";

    public static ChangePasswordFragment newInstance() {

        Bundle args = new Bundle();

        ChangePasswordFragment fragment = new ChangePasswordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_change_password;
    }

    @Override
    protected void initEvent() {

    }

    String email;
    @Override
    protected void initData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utils.EMAIL, MODE_PRIVATE);
        email = sharedPreferences.getString(Utils.EMAIL, "");
        Log.d(TAG, "initDataEmail: " + email);

        binding.btnSaveChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPass = binding.edtOldPass.getText().toString();
                String newPass = binding.edtNewPass.getText().toString();
                String reNewPass = binding.edtReNewPass.getText().toString();
                if(oldPass.equals("") || newPass.equals("") || reNewPass.equals("")){
                    Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else if(!newPass.equals(reNewPass)) {
                    Toast.makeText(getContext(), "Mật khẩu xác nhận không trùng khớp", Toast.LENGTH_SHORT).show();
                }
                else {
                    String TABLE;
                    AccountDAO accountDAO = new AccountDAO(getContext());
                    SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences("USER", MODE_PRIVATE);
                    String role = sharedPreferences1.getString(Utils.ROLE, "");
                    Account account = new Account();
                    if (role.equals(Utils.CUSTOMER)){
                        TABLE = DBHelper.TABLE_ACCOUNT_CUSTOMER;
                    }else{
                        TABLE = DBHelper.TABLE_ACCOUNT_SHOP;
                    }

                    account = accountDAO.getEmail(email, TABLE);
                    Log.d(TAG, "initData: " + role);

                    String password = binding.edtNewPass.getText().toString();
                    int minLength = 6;

                    if (password.length() < minLength) {
                        Toast.makeText(getContext(), "Mật khẩu phải có ít nhất " + minLength + " ký tự", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(account.getPassword().equals(oldPass)) {
                        account.setPassword(newPass);
                        if (accountDAO.updatee(account , email, TABLE)) {
                            Toast.makeText(getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), ChooseActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public String getTAG() {
        return TAG;
    }

}
