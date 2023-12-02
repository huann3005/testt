package com.example.duan1.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.duan1.MainActivity;
import com.example.duan1.R;
import com.example.duan1.Utils;
import com.example.duan1.admin.ui.activity.BaseActivity;
import com.example.duan1.dao.AccountDAO;
import com.example.duan1.dao.CustomerDAO;
import com.example.duan1.dao.EmployeeDAO;
import com.example.duan1.databinding.ActivityLoginBinding;

import com.example.duan1.user.MainCustomer;
import com.example.duan1.database.DBHelper;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {
    private String type = "custom";
    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    String role = Utils.CUSTOMER;
    @Override
    protected void initEvent() {
        AccountDAO accountDAO = new AccountDAO(this);
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
            }
        });
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type.equals(Utils.CUSTOMER)){
                    if(accountDAO.checkLogin(binding.edtEmail.getText().toString(), binding.edtPassword.getText().toString(), DBHelper.TABLE_ACCOUNT_CUSTOMER)){

                        if(new CustomerDAO(LoginActivity.this).getStatus(binding.edtEmail.getText().toString()).equals("inactive")) {
                            Toast.makeText(LoginActivity.this, "Tài khoản của bạn đã bị khóa", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SharedPreferences sharedPreferences = getSharedPreferences(Utils.EMAIL, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Utils.EMAIL, binding.edtEmail.getText().toString());

                        editor.commit();

                        role = accountDAO.getRole(binding.edtEmail.getText().toString(), DBHelper.TABLE_ACCOUNT_CUSTOMER);

                        startActivity(new Intent(LoginActivity.this, MainCustomer.class));
                        finishAffinity();
                    }else{
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else if (type.equals(Utils.SHOP)) {
                    if(accountDAO.checkLogin(binding.edtEmail.getText().toString(), binding.edtPassword.getText().toString(), DBHelper.TABLE_ACCOUNT_SHOP)) {

                        Log.d("TAGggg", "onClick: " + new EmployeeDAO(LoginActivity.this).getStatus(binding.edtEmail.getText().toString()));
                        if(new EmployeeDAO(LoginActivity.this).getStatus(binding.edtEmail.getText().toString()).equals("inactive")) {
                            Toast.makeText(LoginActivity.this, "Tài khoản của bạn đã bị khóa", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SharedPreferences sharedPreferences = getSharedPreferences(Utils.EMAIL, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Utils.EMAIL, binding.edtEmail.getText().toString());

                        editor.commit();

                        role = accountDAO.getRole(binding.edtEmail.getText().toString(), DBHelper.TABLE_ACCOUNT_SHOP);

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finishAffinity();
                    }else{
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
                SharedPreferences sharedPreferences = getSharedPreferences("USER", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", binding.edtEmail.getText().toString());
                editor.putString("email" + type, binding.edtEmail.getText().toString());
                editor.putString("password" + type, binding.edtPassword.getText().toString());
                editor.putBoolean("isLogin" + type, binding.cbRememberPassword.isChecked());
                editor.putString("role", role);
                editor.commit();
            }
        });
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        type = intent.getStringExtra(Utils.TYPE);
        if(type == null){
            type = Utils.CUSTOMER;
        }
        if (type.equals(Utils.SHOP)) {
            binding.llSignUp.setVisibility(View.GONE);
            binding.edtEmail.setText("admin@gmail.com");
            binding.edtPassword.setText("password1");
        }else{
            binding.edtEmail.setText("customer1@gmail.com");
            binding.edtPassword.setText("password1");
        }
        SharedPreferences sharedPreferences = getSharedPreferences("USER", MODE_PRIVATE);
        boolean isLogin = sharedPreferences.getBoolean("isLogin" + type, false);
        binding.cbRememberPassword.setChecked(isLogin);
        if (isLogin) {
            String email = sharedPreferences.getString("email" + type, "");
            String password = sharedPreferences.getString("password" + type, "");
            binding.edtEmail.setText(email);
            binding.edtPassword.setText(password);
        }
    }
}
