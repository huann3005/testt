package com.example.duan1.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.duan1.database.DBHelper;
import com.example.duan1.model.Cart;
import com.example.duan1.model.Product;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    Context context;
    private DBHelper dbHelper;
    public ProductDAO(Context context){
        this.dbHelper = new DBHelper(context);
    }

    public List<Product> getAll(){
        String sql = "SELECT * FROM " + DBHelper.TABLE_PRODUCT;
        List<Product> list = getData(sql);
        if (list.size() > 0){
            return list;
        }
        return null;
    }

    public List<Product> getAllCus(){
        String sql = "SELECT * FROM " + DBHelper.TABLE_PRODUCT + " WHERE status = 'Còn hàng'";
        List<Product> list = getData(sql);
        if (list.size() > 0){
            return list;
        }
        return null;
    }

    public Product getID(int id){
        String sql = "SELECT * FROM " + DBHelper.TABLE_PRODUCT + " WHERE id = ?";
        List<Product> list = getData(sql, String.valueOf(id));
        if (list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    public boolean insert(Product product){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "INSERT INTO " + DBHelper.TABLE_PRODUCT + "(name, price, quantitySold, status, image)" + " VALUES(?,?,?,?,?)";
        db.execSQL(sql, new String[]{String.valueOf(product.getName()), String.valueOf(product.getPrice()), String.valueOf(product.getQuantitySold()), product.getStatus(), product.getImage()});
        if (db != null){
            db.close();
        }
        return true;
    }
    public boolean updatee(Product product, int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "UPDATE " + DBHelper.TABLE_PRODUCT + " SET name = ?, image = ?, price = ?, quantitySold = ?, status = ?" + " WHERE id = ?";
        db.execSQL(sql, new String[]{product.getName(), product.getImage(), String.valueOf(product.getPrice()), String.valueOf(product.getQuantitySold()), product.getStatus(), String.valueOf(id)});
        if (db != null){
            db.close();
        }
        return true;
    }
    private List<Product> getData(String sql, String...selectionArgs){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Product> list = new ArrayList<>();

        Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()){
            list.add(new Product(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5)));
        }
        if (cursor != null){
            cursor.close();
        }
        if (db != null){
            db.close();
        }
        return list;
    }

    //sort product by quantitySold
    public List<Product> getAllByQuantitySold(String type){
        String sql = "SELECT * FROM " + DBHelper.TABLE_PRODUCT + " ORDER BY quantitySold " + type;
        List<Product> list = getData(sql);
        if (list.size() > 0){
            return list;
        }
        return null;
    }

    //tăng số lượng đã bán
    public boolean updateQuantitySold(Cart cart){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //lấy ra cart --> từ id của cart --> sản phẩm --> số lượng đã bán ht = sl cart + sl đã bán ban đầu
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_PRODUCT + " WHERE id = ?", new String[]{String.valueOf(cart.getIdProduct())});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            int quantitySold = cursor.getInt(4);
            quantitySold += cart.getQuantity();
            db.execSQL("UPDATE " + DBHelper.TABLE_PRODUCT + " SET quantitySold = ? WHERE id = ?", new String[]{String.valueOf(quantitySold), String.valueOf(cursor.getInt(0))});
        }
        if (cursor != null){
            cursor.close();
        }
        if (db != null){
            db.close();
        }
        return true;
    }
}
