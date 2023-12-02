package com.example.duan1.qrcode;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.duan1.Utils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Quét mã QR ở trên bàn để tiếp tục");
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Lấy kết quả từ IntentResult
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // Người dùng hủy quét mã QR
                Log.d("QRScanActivity", "Cancelled");
                finish();
            } else {
                // Đọc dữ liệu từ mã QR thành công
                String qrData = result.getContents();
                Log.d("QRScanActivity", "Scanned: " + qrData);
                // Xử lý dữ liệu qrData theo nhu cầu của bạn ở đây
                SharedPreferences sharedPreferences = getSharedPreferences(Utils.QR, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Utils.QR, qrData);
                editor.apply();
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
