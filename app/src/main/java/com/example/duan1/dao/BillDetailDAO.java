package com.example.duan1.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.duan1.database.DBHelper;
import com.example.duan1.model.BillDetail;

import java.util.ArrayList;
import java.util.List;

public class BillDetailDAO {
    private DBHelper dbHelper;

    public BillDetailDAO(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public List<BillDetail> getAll() {
        String sql = "SELECT * FROM " + DBHelper.TABLE_BILL_DETAIL;
        List<BillDetail> list = getData(sql);
        if (list.size() > 0) {
            return list;
        }
        return null;
    }

    public BillDetail getID(int id) {
        String sql = "SELECT * FROM " + DBHelper.TABLE_BILL_DETAIL + " WHERE id = ?";
        List<BillDetail> list = getData(sql, String.valueOf(id));
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public boolean insert(BillDetail object) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "INSERT INTO " + DBHelper.TABLE_BILL_DETAIL + "( idBill, idProduct, quantity, price, note) " + " VALUES(?,?,?,?,?)";
        db.execSQL(sql, new String[]{String.valueOf(object.getIdBill()), String.valueOf(object.getIdProduct()), String.valueOf(object.getQuantity()), String.valueOf(object.getPrice()), object.getNote()});
        Log.i("TAG", "Giá: " + object.getPrice());
        if (db != null) {
            db.close();
        }
        return true;
    }

    public int getTotalPrice(int idBill) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(price) FROM " + DBHelper.TABLE_BILL_DETAIL + " WHERE idBill = ?", new String[]{String.valueOf(idBill)});
        c.moveToFirst();
        return c.getInt(0);
    }

    public List<BillDetail> getAllByIdBill(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<BillDetail> list = new ArrayList<>();

        String sql = "SELECT * FROM " + DBHelper.TABLE_BILL_DETAIL + " WHERE idBill = ?";
        Cursor c = db.rawQuery(sql, new String[]{id});
        while (c.moveToNext()) {
            list.add(new BillDetail(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3), c.getInt(4), c.getString(5)));
        }
        if (c != null) {
            c.close();
        }
        if (db != null) {
            db.close();
        }
        return list;
    }

    private List<BillDetail> getData(String sql, String... selectionArgs) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<BillDetail> list = new ArrayList<>();

        Cursor c = db.rawQuery(sql, selectionArgs);
        while (c.moveToNext()) {
            list.add(new BillDetail(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3), c.getInt(4), c.getString(5)));
        }
        if (c != null) {
            c.close();
        }
        if (db != null) {
            db.close();
        }
        return list;
    }
}
