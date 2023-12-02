package com.example.duan1.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.duan1.database.DBHelper;
import com.example.duan1.model.ProductDetail;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailDAO {
    private DBHelper dbHelper;
    public ProductDetailDAO(Context context){
        this.dbHelper = new DBHelper(context);
    }

    public List<ProductDetail> getAll(){
        String sql = "SELECT * FROM " + DBHelper.TABLE_PRODUCT_DETAIL;
        List<ProductDetail> list = getData(sql);
        if (list.size() > 0){
            return list;
        }
        return null;
    }

    public ProductDetail getID(int id){
        String sql = "SELECT * FROM " + DBHelper.TABLE_PRODUCT_DETAIL + " WHERE idProduct = ?";
        List<ProductDetail> list = getData(sql, String.valueOf(id));
        if (list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    public boolean insert(int id, String description, String image){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "INSERT INTO " + DBHelper.TABLE_PRODUCT_DETAIL+ "(idProduct, description, image)" + " VALUES(?,?,?)";
        db.execSQL(sql, new String[]{String.valueOf(id), String.valueOf(description), String.valueOf(image)});
        if (db != null){
            db.close();
        }
        return true;
    }

    public boolean update(ProductDetail product){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "UPDATE " + DBHelper.TABLE_PRODUCT_DETAIL + " SET description = ?" + " WHERE id = ?";
        db.execSQL(sql, new String[]{String.valueOf(product.getDescription()), String.valueOf(product.getId())});
        if (db != null){
            db.close();
        }
        return true;
    }

    public boolean delete(int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "DELETE FROM " + DBHelper.TABLE_PRODUCT_DETAIL + " WHERE id = ?";
        db.execSQL(sql, new String[]{String.valueOf(id)});
        if (db != null){
            db.close();
        }
        return true;
    }
    private List<ProductDetail> getData(String sql, String...selectionArgs){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<ProductDetail> list = new ArrayList<>();

        Cursor c = db.rawQuery(sql, selectionArgs);
        while (c.moveToNext()){
            list.add(new ProductDetail(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3)));
        }
        if (c != null){
            c.close();
        }
        if (db != null){
            db.close();
        }
        return list;
    }
}
