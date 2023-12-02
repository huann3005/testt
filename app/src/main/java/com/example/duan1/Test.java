package com.example.duan1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.duan1.qrcode.QRCodeGenerator;

public class Test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        QRCodeGenerator qrCodeGenerator = new QRCodeGenerator();
        ImageView imageView = findViewById(R.id.img);
        imageView.setImageBitmap(qrCodeGenerator.generateQRCode("1", 500, 500));
    }
}