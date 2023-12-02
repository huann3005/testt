package com.example.duan1.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.duan1.database.DBHelper;
import com.example.duan1.model.Customer;
import com.example.duan1.model.Employee;

import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private DBHelper dbHelper;
    public CustomerDAO(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public List<Customer> getAll() {
        String sql = "SELECT * FROM " + DBHelper.TABLE_CUSTOMER;
        List<Customer> list = getData(sql);
        if (list.size() > 0) {
            return list;
        }
        return null;
    }

    public List<Customer> getAllByStatus(String status) {
        String sql = "SELECT * FROM " + DBHelper.TABLE_CUSTOMER + " WHERE status = ?";
        List<Customer> list = getData(sql, status);
        if (list.size() > 0) {
            return list;
        }
        return null;
    }
    public Customer getID(String id) {
        String sql = "SELECT * FROM " + DBHelper.TABLE_CUSTOMER + " WHERE id = ?";
        List<Customer> list = getData(sql, id);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public String getStatus(String email){
        String sql = "SELECT * FROM " + DBHelper.TABLE_CUSTOMER + " WHERE email = ?";
        List<Customer> list = getData(sql, email);
        if (list.size() > 0) {
            return list.get(0).getStatus();
        }
        return null;
    }
    public Customer getByEmail(String email) {
        String sql = "SELECT * FROM " + DBHelper.TABLE_CUSTOMER + " WHERE email = ?";
        List<Customer> list = getData(sql, email);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public boolean insert(Customer object) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "INSERT INTO " + DBHelper.TABLE_CUSTOMER + "(email, name, phone, address, birthday, status, image)" +  "VALUES(?,?,?,?,?,?,?)";
        db.execSQL(sql, new String[]{object.getEmail(), object.getName(), object.getPhone(), object.getAddress(), object.getBirthday(), object.getStatus(), object.getImage()});
        if (db != null) {
            db.close();
        }
        return true;
    }
    public boolean updateStatus(Customer object, String id) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String sql = "UPDATE " + DBHelper.TABLE_CUSTOMER + " SET status = ? WHERE id = ?";
        sqLiteDatabase.execSQL(sql, new String[]{object.getStatus(), id});
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
        return true;
    }
    public boolean updateInfo(Customer object, String email) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String sql = "UPDATE " + DBHelper.TABLE_CUSTOMER + " SET name = ?, phone = ?, address = ?, birthday = ?, image = ? WHERE email = ?";
        sqLiteDatabase.execSQL(sql, new String[]{object.getName(), object.getPhone(), object.getAddress(), object.getBirthday(), object.getImage(), email});
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
        return true;
    }

    private List<Customer> getData(String sql, String... selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<Customer> list = new ArrayList<>();

        Cursor c = db.rawQuery(sql, selectionArgs);
        while (c.moveToNext()) {
            list.add(new Customer(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7)));
            Log.e("TAGG", c.getString(6));
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
