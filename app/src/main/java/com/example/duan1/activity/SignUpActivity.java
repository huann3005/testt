package com.example.duan1.activity;

import android.content.Intent;
import android.widget.Toast;

import com.example.duan1.R;
import com.example.duan1.admin.ui.activity.BaseActivity;
import com.example.duan1.dao.AccountDAO;
import com.example.duan1.dao.CustomerDAO;
import com.example.duan1.databinding.ActivitySignUpBinding;
import com.example.duan1.model.Account;
import com.example.duan1.model.Customer;

public class SignUpActivity extends BaseActivity<ActivitySignUpBinding> {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected void initEvent() {
        binding.btnLogin.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void initData() {
        binding.btnSignUp.setOnClickListener(view -> {
            String email = binding.edtEmail.getText().toString();
            String password = binding.edtPassword.getText().toString();
            String confirmPassword = binding.edtConfirmPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showToast("Vui lòng điền đầy đủ thông tin");
                return;
            }
            if(!password.equals(confirmPassword)) {
                showToast("Xác nhận mật khẩu không đúng");
                return;
            }
            if(password.length() < 6) {
                showToast("Mật khẩu phải có ít nhất 6 ký tự");
                return;
            }
            if(!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
                showToast("Email không hợp lệ");
                return;
            }
            if(!binding.cbAgree.isChecked()) {
                showToast("Bạn phải đồng ý điều khoản");
                return;
            }
            AccountDAO accountDAO = new AccountDAO(this);
            if(accountDAO.insert(new Account(email, password, null))){
                if(new CustomerDAO(this).insert(new Customer(0,email, null, null, null, null, "active", null))){
                    showToast("Đăng ký thành công");
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                }
            }else {
                showToast("Email đã tồn tại");
            }
        });
    }
    void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
