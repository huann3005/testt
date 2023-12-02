package com.example.duan1.admin.ui.fragment.productmanagement;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.duan1.R;
import com.example.duan1.admin.ui.fragment.BaseFragment;
import com.example.duan1.dao.ProductDAO;
import com.example.duan1.dao.ProductDetailDAO;
import com.example.duan1.databinding.FragmentProductManagementBinding;
import com.example.duan1.eventbus.Search;
import com.example.duan1.model.Product;
import com.example.duan1.model.ProductDetail;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductManagementFragment extends BaseFragment<FragmentProductManagementBinding> {
    ProductDAO productDAO;
    List<Product> list;
    AdapterProductManagement adapter;
    String filePath = "";
    ImageView ivImagePro;
    TextView tvStatusImage;
    ProductDetailDAO productDetailDAO;
    ProductDetail productDetail;
    String linkImage = "";
    int PERMISSION_CODE = 1;
    View loading;
    Product product;
    boolean isChooseImage = false;


    public static String TAG = "Quản lý sản phẩm";

    public static ProductManagementFragment newInstance() {
        Bundle args = new Bundle();

        ProductManagementFragment fragment = new ProductManagementFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_product_management;
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void initData() {
        requestPermission();
        item1();
        loadData();
        addProduct();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        configCloudinary();
    }

    @Override
    public String getTAG() {
        return TAG;
    }

    public void loadData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.rcvProduct.setLayoutManager(linearLayoutManager);
        productDAO = new ProductDAO(getContext());
        list = productDAO.getAll();
        adapter = new AdapterProductManagement(getContext(), list) {
            @Override
            public void click(int position) {
                product = list.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update_products, null);
                builder.setView(view);
                builder.create();
                AlertDialog alertDialog = builder.create();

                EditText edtTenSP = view.findViewById(R.id.edt_title_updatepro);
                EditText edtDonGia = view.findViewById(R.id.edt_price_updatepro);
                EditText edtmota = view.findViewById(R.id.edt_mota_uppro);
                Spinner spinnerTrangThai = view.findViewById(R.id.spn_updatepro);
                Button btnUpdate = view.findViewById(R.id.btn_submit_updatepro);
                Button btnCancel = view.findViewById(R.id.btn_canupdatepro);
                ivImagePro = view.findViewById(R.id.iv_update_product);


                List<String> data = new ArrayList<>();
                data.add("Còn hàng");
                data.add("Hết hàng");

                // Tạo Adapter để đổ dữ liệu vào Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, data);

                // Định dạng Spinner
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Gán Adapter cho Spinner
                spinnerTrangThai.setAdapter(adapter);
                edtTenSP.setText(product.getName());
                productDetailDAO = new ProductDetailDAO(getContext());
                productDetail = productDetailDAO.getID(product.getId());
                edtmota.setText(productDetail.getDescription());
                edtDonGia.setText(String.valueOf(product.getPrice()));
                spinnerTrangThai.setSelection(data.indexOf(product.getStatus()));

                if (product.getImage() != null) {
                    Glide.with(getContext()).load(product.getImage()).into(ivImagePro);
                } else {
                    Glide.with(getContext()).load(R.drawable.improduct1).into(ivImagePro);
                }

                if (product.getStatus().equals("Còn hàng")) {
                    spinnerTrangThai.setSelection(0);
                } else {
                    spinnerTrangThai.setSelection(1);
                }

                ivImagePro.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        accessTheGallery();
                    }
                });

                btnUpdate.setOnClickListener(v1 -> {
                    ProductDAO productDAO = new ProductDAO(getContext());
                    String status = spinnerTrangThai.getSelectedItem().toString();
                    String name = edtTenSP.getText().toString();
                    String gia = edtDonGia.getText().toString();
                    String mota = edtmota.getText().toString();
                    if (!validate(name, gia, mota)) {
                        return;
                    }
                    product.setStatus(status);
                    product.setName(name);
                    product.setPrice(Integer.parseInt(gia));
                    productDetail.setDescription(mota);

                    if(!isChooseImage) {
                        if(product.getImage() == null){
                            Toast.makeText(getContext(), "Chưa chọn ảnh", Toast.LENGTH_SHORT).show();
                            return;
                        }else {
                            productDAO.updatee(product, product.getId());
                            productDetailDAO.update(productDetail);
                            loadData();
                            Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        uploadToCloudinary(filePath, product, product.getId() + "");
                    }

                    alertDialog.dismiss();
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        };
        binding.rcvProduct.setAdapter(adapter);
    }

    public void addProduct() {
        binding.fltAddPro.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.dialog_add_products, null);
            builder.setView(view);
            AlertDialog alertDialog = builder.create();

            EditText edtName = view.findViewById(R.id.edt_name_addpro);
            EditText edtPrice = view.findViewById(R.id.edt_price_addpro);
            EditText edtmota = view.findViewById(R.id.edt_mota_addpro);
            Spinner spnRole = view.findViewById(R.id.spn_addpro);
            Button btnThem = view.findViewById(R.id.btn_submit_addpro);
            Button btnHuy = view.findViewById(R.id.btn_canaddpro);
            ivImagePro = view.findViewById(R.id.ivImageProduct);
            tvStatusImage = view.findViewById(R.id.tvStatusImage);

            // Tạo danh sách dữ liệu
            List<String> data = new ArrayList<>();
            data.add("Còn hàng");
            data.add("Hết hàng");

            // Tạo Adapter để đổ dữ liệu vào Spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, data);

            // Định dạng Spinner
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnRole.setAdapter(adapter);

            btnThem.setOnClickListener(v1 -> {
                Product product = new Product();
                String name = edtName.getText().toString();
                String price = edtPrice.getText().toString();
                String status = spnRole.getSelectedItem().toString();
                String mota = edtmota.getText().toString();

                if (!validate(name, price, mota)) {
                    return;
                }
                product.setName(name);
                product.setPrice(Integer.parseInt(price));
                product.setStatus(status);
                product.setImage("" + linkImage);

                productDetailDAO = new ProductDetailDAO(getContext());

                if(!isChooseImage) {
                    Toast.makeText(getContext(), "Chưa chọn ảnh", Toast.LENGTH_SHORT).show();
                    return;
                }

                productDetailDAO.insert(list.size() + 1, mota, linkImage);
                Log.i("TAG", "Link ảnh đây nàyyyy: " + linkImage);
                uploadToCloudinary(filePath, product, null);
                alertDialog.dismiss();
            });
            ivImagePro.setOnClickListener(v1 -> {
                accessTheGallery();
            });

            btnHuy.setOnClickListener(v1 -> alertDialog.dismiss());
            alertDialog.show();
        });
    }

    public void accessTheGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        myLauncher.launch(i);
    }

    private ActivityResultLauncher<Intent> myLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
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
                    ivImagePro.setImageBitmap(bitmap);

                    isChooseImage = true;

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

    private void updateProduct(Product product, String id) {
        product.setImage(linkImage);

        productDetailDAO = new ProductDetailDAO(getContext());
        productDetailDAO.update(productDetail);

        if (productDAO.updatee(product, product.getId())) {
            Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            loadData();
        } else {
            Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertProduct(Product product) {
        product.setImage(linkImage);
        if (productDAO.insert(product)) {
            Toast.makeText(getContext(), "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
            loadData();
            binding.rcvProduct.smoothScrollToPosition(list.size() - 1);
        } else {
            Toast.makeText(getContext(), "Thêm sản phẩm thất bại", Toast.LENGTH_SHORT).show();
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

    private void uploadToCloudinary(String filePath, Product product, String id) {
        MediaManager.get().upload(filePath).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                binding.spinKit.setVisibility(View.VISIBLE);
                binding.fltAddPro.setEnabled(false);
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {

            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                binding.spinKit.setVisibility(View.GONE);
                binding.fltAddPro.setEnabled(true);

                linkImage = resultData.get("url").toString();
                Log.i("TAG", "linkImage nhaa: " + linkImage);

                if (id != null) {
                    updateProduct(product, id);
                } else {
                    insertProduct(product);
                }
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

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Cấp quyền thành công", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Cấp quyền thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void item1() {
        List<String> data = new ArrayList<>();
        data.add("Chọn sắp xếp");
        data.add("Sắp xếp tăng dần");
        data.add("Sắp xếp giảm dần");

        // Tạo Adapter để đổ dữ liệu vào Spinner
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, data);

        // Định dạng Spinner
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Gán Adapter cho Spinner
        binding.spinnerProduct.setAdapter(adapter1);
        binding.spinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (data.get(i).equals("Sắp xếp tăng dần")) {
                    adapter.sort2();
                    Toast.makeText(getContext(), "Đã sắp xếp tăng dần", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                } else if (data.get(i).equals("Sắp xếp giảm dần")) {
                    adapter.sort1();
                    Toast.makeText(getContext(), "Đã sắp xếp giảm dần", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private boolean validate(String ten, String gia, String moTa) {
        if (ten.trim().equals("") || gia.trim().equals("") || moTa.trim().equals("")) {
            Toast.makeText(getContext(), "Không được để trống!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!gia.trim().matches("[0-9]+")) {
            Toast.makeText(getContext(), "Giá phải là số", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    private void updateProduct1() {
        if (productDAO.updatee(product, product.getId())) {
            Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            loadData();
        } else {
            Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    //nhận sự kiện eventbus
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this); // Đăng ký để nhận sự kiện
    }
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this); // Hủy đăng ký khi Fragment không còn hiển thị
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Search search) {
        List<Product> templist = productDAO.getAll();
        list.clear();
        for (Product p : templist) {
            if (p.getName().toLowerCase().contains(search.getText().toLowerCase())) {
                list.add(p);
            }
        }
        if(list.isEmpty()){
            binding.tvNoInf.setVisibility(View.VISIBLE);
        }
        else {
            binding.tvNoInf.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }
}
