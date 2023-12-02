package com.example.duan1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;

import com.bumptech.glide.Glide;
import com.example.duan1.activity.ChooseActivity;
import com.example.duan1.admin.ui.fragment.ordermanagement.ConfirmFragment;
import com.example.duan1.admin.ui.fragment.ordermanagement.OrderAdminFragment;
import com.example.duan1.admin.ui.fragment.statistics.StatisticsSoldFragment;
import com.example.duan1.admin.ui.fragment.updateinformation.UpdateInformationActivity;
import com.example.duan1.dao.EmployeeDAO;
import com.example.duan1.admin.ui.activity.BaseActivity;
import com.example.duan1.admin.ui.fragment.BaseFragment;
import com.example.duan1.admin.ui.fragment.customermanagement.CustomerManagementFragment;
import com.example.duan1.admin.ui.fragment.employeemanagement.EmployeeManagementFragment;
import com.example.duan1.admin.ui.fragment.ordermanagement.OrderManagementFragment;
import com.example.duan1.admin.ui.fragment.productmanagement.ProductManagementFragment;
import com.example.duan1.admin.ui.fragment.statistics.StatisticsFragment;
import com.example.duan1.admin.ui.fragment.updateinformation.UpdateInformationFragment;
import com.example.duan1.databinding.ActivityMainBinding;
import com.example.duan1.eventbus.Search;
import com.example.duan1.eventbus.UpdateInfo;
import com.example.duan1.model.Employee;
import com.example.duan1.user.fragment.changepass.ChangePasswordFragment;
import com.google.android.material.imageview.ShapeableImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private Boolean isExit = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initEvent() {
        BaseFragment.add(this, ProductManagementFragment.newInstance());
        binding.tvTitle.setText(ProductManagementFragment.newInstance().getTAG());
        binding.navView.setCheckedItem(R.id.nav_product_management);
        binding.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawerLayout.openDrawer(binding.navView);
            }
        });
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.rltSearch.setVisibility(View.VISIBLE);
            }
        });
        binding.btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.rltSearch.setVisibility(View.GONE);
                binding.edtSearchView.setText("");
            }
        });

        binding.edtSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                EventBus.getDefault().post(new Search(String.valueOf(charSequence)));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.navView.setNavigationItemSelectedListener(item -> {
            BaseFragment fragment = null;
            if(item.getItemId() == R.id.nav_statistics){
                fragment = StatisticsFragment.newInstance();
            } else if (item.getItemId() == R.id.nav_product_management) {
                fragment = ProductManagementFragment.newInstance();
            } else if (item.getItemId() == R.id.nav_employee_management) {
                fragment = EmployeeManagementFragment.newInstance();
            } else if (item.getItemId() == R.id.nav_customer_management) {
                fragment = CustomerManagementFragment.newInstance();
            } else if (item.getItemId() == R.id.nav_order_management) {
                fragment = OrderManagementFragment.newInstance();
            } else if (item.getItemId() == R.id.nav_update_profile) {
                fragment = UpdateInformationFragment.newInstance();
            } else if (item.getItemId() == R.id.nav_change_password) {
                fragment = ChangePasswordFragment.newInstance();
            } else if (item.getItemId() == R.id.nav_logout) {
                startActivity(new Intent(MainActivity.this, ChooseActivity.class));
                finish();
            } else if (item.getItemId() == R.id.nav_order_management_admin) {
                fragment = OrderAdminFragment.newInstance();
            }  else if (item.getItemId() == R.id.nav_order_payment_confirmation) {
                fragment = ConfirmFragment.newInstance();
            }  else if (item.getItemId() == R.id.nav_statistics_sold) {
                fragment = StatisticsSoldFragment.newInstance();
            }
            if (fragment != null) {
                BaseFragment.add(MainActivity.this, fragment);
                binding.tvTitle.setText(fragment.getTAG());
            }
            item.setCheckable(true);
            binding.drawerLayout.closeDrawer(binding.navView);
            return true;
        });
    }

    @Override
    protected void initData() {
        loadData();
    }

    private void updateInformationIfNewAccount(String email){
        EmployeeDAO employeeDAO = new EmployeeDAO(this);
        Employee employee = employeeDAO.getByEmail(email);
        if(employee.getName() == null ||
                employee.getPhone() == null ||
                employee.getAddress() == null ||
                employee.getCitizenshipID() == null ||
                employee.getImage() == null){
            startActivity(new Intent(this, UpdateInformationActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (backStackEntryCount > 0) {
            super.onBackPressed();
        } else if (this.isExit.booleanValue()) {
            finish();
        } else {
            Toast.makeText(this, "Press again to exit!", Toast.LENGTH_SHORT).show();
            this.isExit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public final void run() {
                    isExit = false;
                }
            }, 3000L);
        }
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("USER", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String role = sharedPreferences.getString("role", "");
        Log.d("TAG", "loadData: " + email + " " + role);
        updateInformationIfNewAccount(email);

        switch (role) {
            case Utils.ADMIN:
                binding.navView.getMenu().findItem(R.id.nav_employee_management).setVisible(true);
                binding.navView.getMenu().findItem(R.id.nav_customer_management).setVisible(true);
                binding.navView.getMenu().findItem(R.id.nav_order_management_admin).setVisible(true);
                binding.navView.getMenu().findItem(R.id.nav_order_payment_confirmation).setVisible(true);
                binding.navView.getMenu().findItem(R.id.nav_statistics).setVisible(true);
                break;
            case Utils.EMPLOYEE_CHEF:
                binding.navView.getMenu().findItem(R.id.nav_order_management).setVisible(true);
                break;
            case Utils.EMPLOYEE_SP:
                binding.navView.getMenu().findItem(R.id.nav_order_payment_confirmation).setVisible(true);
                break;
        }

        EmployeeDAO employeeDAO = new EmployeeDAO(this);
        Employee employee = employeeDAO.getByEmail(email);
        ShapeableImageView imgAccount = binding.navView.getHeaderView(0).findViewById(R.id.imgAccount);
        TextView tvFullName = binding.navView.getHeaderView(0).findViewById(R.id.tvFullName);
        TextView tvAc = binding.navView.getHeaderView(0).findViewById(R.id.tvAc);
        imgAccount.setImageResource(R.drawable.baseline_person_24_ccc);
        tvFullName.setText(employee.getName());
        tvAc.setText(employee.getEmail());
        if(employee.getImage() != null){
            Glide.with(this).load(employee.getImage()).into(imgAccount);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateInfo event) {
        loadData();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}