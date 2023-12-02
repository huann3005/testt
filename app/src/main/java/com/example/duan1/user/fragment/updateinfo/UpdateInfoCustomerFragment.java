package com.example.duan1.user.fragment.updateinfo;

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
import com.example.duan1.R;
import com.example.duan1.admin.ui.fragment.BaseFragment;
import com.example.duan1.dao.CustomerDAO;
import com.example.duan1.databinding.FragmentUpdateInfoCustomerBinding;
import com.example.duan1.eventbus.UpdateInfo;
import com.example.duan1.model.Customer;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateInfoCustomerFragment extends BaseFragment<FragmentUpdateInfoCustomerBinding> {

    String filePath = "";
    String linkImageCus = "";
    String emailData = "";
    Customer customer;

    public static UpdateInfoCustomerFragment newInstance() {

        Bundle args = new Bundle();

        UpdateInfoCustomerFragment fragment = new UpdateInfoCustomerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_update_info_customer;
    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        configCloudinary();
    }

    @Override
    protected void initData() {
        loadData();
        update();
    }

    @Override
    public String getTAG() {
        return "Cập nhật thông tin";
    }

    public void loadData() {
        CustomerDAO customerDAO = new CustomerDAO(getContext());
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("USER", MODE_PRIVATE);
        emailData = sharedPreferences.getString("email", "");
        customer = customerDAO.getByEmail(emailData);
        binding.edtNameUpEm.setText(customer.getName());
        binding.edtPhone.setText(customer.getPhone());
        binding.edtEmail.setText(customer.getEmail());
        binding.edtAddress.setText(customer.getAddress());
        binding.edtBirthday.setText(customer.getBirthday());

        if (customer.getImage() != null) {
            Glide.with(getContext()).load(customer.getImage()).into(binding.imgCustomer);
        } else {
            binding.imgCustomer.setImageResource(R.drawable.baseline_person_24_ccc);
        }


    }

    public void update() {
        binding.imgCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accessTheGallery();
            }
        });
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (filePath.equals("") && customer.getImage() != null) {
//                    updateInfo();
//                } else if (filePath.equals("") && customer.getImage() == null) {
//                    updateInfo();
                if(!validateForm())
                    return;
                if(filePath.equals("")){
                    updateInfo();
                }else {
                    uploadToCloudinary(filePath);
                }

            }
        });
    }


    public void accessTheGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
            //get the image's file location
            filePath = getRealPathFromUri(result.getData().getData(), getActivity());

            if (result.getResultCode() == RESULT_OK) {
                try {
                    //set picked image to the mProfile
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), result.getData().getData());
                    //hiển thị hình ảnh lên imgview
                    Glide.with(getContext()).load(bitmap).into(binding.imgCustomer);
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

    HashMap<String, String> config = new HashMap<>();

    private void configCloudinary() {
        try {
            config.put("cloud_name", "diqi1klub");
            config.put("api_key", "712862419224312");
            config.put("api_secret", "XfHe0kvB7wBt4vgIfbHcoXP8L3M");
            MediaManager.init(getContext(), config);
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
                linkImageCus = resultData.get("url").toString();
                Log.i("TAG", "linkImage nhaa: " + linkImageCus);

                CustomerDAO customerDAO = new CustomerDAO(getContext());
                customer = customerDAO.getByEmail(emailData);
                customer.setName(binding.edtNameUpEm.getText().toString());
                customer.setPhone(binding.edtPhone.getText().toString());
                customer.setAddress(binding.edtAddress.getText().toString());
                customer.setBirthday(binding.edtBirthday.getText().toString());
                if (linkImageCus.equals("")) {
                    customer.setImage(null);
                } else {
                    customer.setImage(linkImageCus);
                }
                if (customerDAO.updateInfo(customer, emailData)) {
                    Toast.makeText(getContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new UpdateInfo());
                } else {
                    Toast.makeText(getContext(), "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
                }
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

    public void updateInfo() {
        CustomerDAO customerDAO = new CustomerDAO(getContext());
        customer = customerDAO.getByEmail(emailData);
        customer.setName(binding.edtNameUpEm.getText().toString());
        customer.setPhone(binding.edtPhone.getText().toString());
        customer.setAddress(binding.edtAddress.getText().toString());
        customer.setBirthday(binding.edtBirthday.getText().toString());
        if (customerDAO.updateInfo(customer, emailData)) {
            Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new UpdateInfo());
        } else {
            Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean validateForm() {
        if (binding.edtNameUpEm.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Tên không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.edtPhone.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Số điện thoại không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.edtEmail.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Email không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.edtAddress.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Địa chỉ không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.edtBirthday.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Ngày sinh không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
