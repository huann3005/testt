package com.example.duan1.qrcode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import android.graphics.Bitmap;

public class QRCodeGenerator {

    public static Bitmap generateQRCode(String data, int width, int height) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, width, height);
            int matrixWidth = matrix.getWidth();
            int matrixHeight = matrix.getHeight();
            int[] pixels = new int[matrixWidth * matrixHeight];

            for (int y = 0; y < matrixHeight; y++) {
                for (int x = 0; x < matrixWidth; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * matrixWidth + x] = 0xFF000000; // Màu đen cho điểm ảnh
                    } else {
                        pixels[y * matrixWidth + x] = 0xFFFFFFFF; // Màu trắng cho điểm ảnh
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
