package com.example.duan1.admin.ui.fragment.updateinformation;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.duan1.MainActivity;
import com.example.duan1.R;
import com.example.duan1.admin.ui.activity.BaseActivity;
import com.example.duan1.admin.ui.fragment.BaseFragment;
import com.example.duan1.dao.EmployeeDAO;
import com.example.duan1.databinding.ActivityUpdateInformationBinding;
import com.example.duan1.databinding.FragmentUpdateInformationBinding;
import com.example.duan1.model.Employee;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class UpdateInformationActivity extends BaseActivity<ActivityUpdateInformationBinding> {
    public static String TAG = "Cập nhật thông tin";
    private String filePath = "";
    private String linkImageEm = "";
    String emailData = "";
    Employee employee;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_update_information;
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initData() {
        configCloudinary();
        loadData();
        update();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("USER", MODE_PRIVATE);
        emailData = sharedPreferences.getString("email", "");
        EmployeeDAO employeeDAO = new EmployeeDAO(this);
        Employee employee = employeeDAO.getByEmail(emailData);
        binding.edtNameUpEm.setText(employee.getName());
        binding.edtPhone.setText(employee.getPhone());
        binding.edtEmail.setText(employee.getEmail());
        binding.edtAddress.setText(employee.getAddress());
        binding.edtCitizen.setText(employee.getCitizenshipID());
        binding.edtDate.setText(employee.getDate());
        binding.imgEmployee.setImageResource(R.drawable.baseline_person_24_ccc);

        if (employee.getImage() != null) {
            Glide.with(this).load(employee.getImage()).into(binding.imgEmployee);
        }
    }

    public void update() {
        binding.imgEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accessTheGallery();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateForm()) return;
                if (filePath.equals("")) {
                    updateInf();
                } else {
                    uploadToCloudinary(filePath);
                }
            }
        });
    }


    public void accessTheGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        myLauncher1.launch(i);
    }

    private ActivityResultLauncher<Intent> myLauncher1 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result == null) {
                return;
            }
            if (result.getData() == null) {
                return;
            }
            isChooseImage = true;
            //get the image's file location
            filePath = getRealPathFromUri(result.getData().getData(), UpdateInformationActivity.this);

            if (result.getResultCode() == RESULT_OK) {
                try {
                    //set picked image to the mProfile
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(UpdateInformationActivity.this.getContentResolver(), result.getData().getData());
                    //hiển thị hình ảnh lên imgview
//                    binding.imgEmployee.setImageBitmap(bitmap);
                    Glide.with(UpdateInformationActivity.this).load(bitmap).into(binding.imgEmployee);
//                    uploadToCloudinary(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    });

    private String getRealPathFromUri(Uri imageUri, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(imageUri, null, null, null, null);

        if (cursor == null) {
            return imageUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    boolean isChooseImage = false;

    //validate form
    private boolean validateForm() {
        if (binding.edtNameUpEm.getText().toString().equals("")) {
            Toast.makeText(this, "Tên không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.edtPhone.getText().toString().equals("")) {
            Toast.makeText(this, "Số điện thoại không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.edtEmail.getText().toString().equals("")) {
            Toast.makeText(this, "Email không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.edtAddress.getText().toString().equals("")) {
            Toast.makeText(this, "Địa chỉ không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.edtCitizen.getText().toString().equals("")) {
            Toast.makeText(this, "CMND/CCCD không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.edtDate.getText().toString().equals("")) {
            Toast.makeText(this, "Ngày sinh không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (isChooseImage == false) {
            Toast.makeText(this, "Chưa chọn ảnh đại diện", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    HashMap<String, String> config = new HashMap<>();

    private void configCloudinary() {
        try {
            config.put("cloud_name", "diqi1klub");
            config.put("api_key", "712862419224312");
            config.put("api_secret", "XfHe0kvB7wBt4vgIfbHcoXP8L3M");
            MediaManager.init(UpdateInformationActivity.this, config);
        } catch (Exception e) {
            Log.i("TAG", "configCloudinary: " + e);
        }
    }

    private void uploadToCloudinary(String filePath) {
        Log.d("A", "sign up uploadToCloudinary- ");
        MediaManager.get().upload(filePath).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                binding.spinKit.setVisibility(View.VISIBLE);
                binding.btnSave.setEnabled(false);
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {

            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                binding.spinKit.setVisibility(View.GONE);
                binding.btnSave.setEnabled(true);
                linkImageEm = resultData.get("url").toString();
                Log.i("TAG", "linkImage nhaa: " + linkImageEm);
                EmployeeDAO employeeDAO = new EmployeeDAO(UpdateInformationActivity.this);
                employee = employeeDAO.getByEmail(emailData);
                employee.setName(binding.edtNameUpEm.getText().toString());
                employee.setPhone(binding.edtPhone.getText().toString());
                employee.setAddress(binding.edtAddress.getText().toString());
                employee.setImage(linkImageEm);
                employee.setCitizenshipID(binding.edtCitizen.getText().toString());

                if (employeeDAO.updateInfoNewAccount(employee, emailData)) {
                    Toast.makeText(UpdateInformationActivity.this, "Câp nhập thông tin thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UpdateInformationActivity.this, "Cập nhật thông tin thât bại", Toast.LENGTH_SHORT).show();
                }
                startActivity(new Intent(UpdateInformationActivity.this, MainActivity.class));
                isChooseImage = false;
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
//                tvStatusImage.setText("Lỗi " + error.getDescription());
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
//                tvStatusImage.setText("Reshedule " + error.getDescription());
            }
        }).dispatch();
    }

    public void updateInf() {
        EmployeeDAO employeeDAO = new EmployeeDAO(UpdateInformationActivity.this);
        employee = employeeDAO.getByEmail(emailData);
        employee.setName(binding.edtNameUpEm.getText().toString());
        employee.setPhone(binding.edtPhone.getText().toString());
        employee.setAddress(binding.edtAddress.getText().toString());
        if (employeeDAO.updateInfoNewAccount(employee, emailData)) {
            startActivity(new Intent(UpdateInformationActivity.this, MainActivity.class));
            finish();
            Toast.makeText(UpdateInformationActivity.this, "Câp nhập thông tin thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(UpdateInformationActivity.this, "Cập nhật thông tin thât bại", Toast.LENGTH_SHORT).show();
        }
    }

}

