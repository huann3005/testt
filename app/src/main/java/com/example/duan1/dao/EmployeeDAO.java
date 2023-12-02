package com.example.duan1.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.duan1.database.DBHelper;
import com.example.duan1.model.Account;
import com.example.duan1.model.Customer;
import com.example.duan1.model.Employee;

import java.net.PortUnreachableException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private DBHelper dbHelper;
    public EmployeeDAO(Context context) {
        this.dbHelper = new DBHelper(context);
    }
    public List<Employee> getAll() {
        String sql = "SELECT * FROM " + DBHelper.TABLE_EMPLOYEE;
        List<Employee> list = getData(sql);
        if (list.size() > 0) {
            return list;
        }
        return null;
    }

    public List<Employee> getAllByStatus(String status) {
        String sql = "SELECT * FROM " + DBHelper.TABLE_EMPLOYEE + " WHERE status = ?";
        List<Employee> list = getData(sql, status);
        if (list.size() > 0) {
            return list;
        }
        return null;
    }
    public Employee getID(String id) {
        String sql = "SELECT * FROM " + DBHelper.TABLE_EMPLOYEE + " WHERE id = ?";
        List<Employee> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, new String[]{id});
        while (c.moveToNext()) {
            list.add(new Employee(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8)));
        }
        if (c != null) {
            c.close();
        }
        if (db != null) {
            db.close();
        }
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public Employee getByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_EMPLOYEE + " WHERE email = ?", new String[]{email});
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            Employee employee = new Employee(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));
            return employee;
        }
        return null;
    }

    public boolean insert(Employee object) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "INSERT INTO " + DBHelper.TABLE_EMPLOYEE + " (email, name, phone, address, citizenshipID, status, date, image) " + " VALUES(?,?,?,?,?,?,?,?)";
        db.execSQL(sql, new String[]{ object.getEmail(), object.getName(), object.getPhone(), object.getAddress(), object.getCitizenshipID(), "active", object.getDate(), object.getImage()});
        if (db != null) {
            db.close();
        }
        return true;
    }

    public boolean updateStatus(Employee object, String id) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String sql = "UPDATE " + DBHelper.TABLE_EMPLOYEE + " SET status = ? WHERE id = ?";
        sqLiteDatabase.execSQL(sql, new String[]{object.getStatus(), id});
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
        return true;
    }
    public boolean updatee(Employee object, int email) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String sql = "UPDATE " + DBHelper.TABLE_EMPLOYEE + " SET status = ? WHERE email = ?";
        sqLiteDatabase.execSQL(sql, new String[]{object.getStatus(), String.valueOf(email)});
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
        return true;
    }

    public boolean updateInfo(Employee employee, String email) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String sql = "UPDATE " + DBHelper.TABLE_EMPLOYEE + " SET name = ?, phone = ?, address = ?, image = ? WHERE email = ?";
        sqLiteDatabase.execSQL(sql, new String[]{employee.getName(), employee.getPhone(), employee.getAddress(), employee.getImage(), email});
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
        return true;
    }
    public boolean updateInfoNewAccount(Employee employee, String email) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String sql = "UPDATE " + DBHelper.TABLE_EMPLOYEE + " SET name = ?, phone = ?, address = ?, image = ?, citizenshipID = ? WHERE email = ?";
        sqLiteDatabase.execSQL(sql, new String[]{employee.getName(), employee.getPhone(), employee.getAddress(), employee.getImage(), employee.getCitizenshipID(), email});
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
        return true;
    }
    public String getStatus(String email){
        String sql = "SELECT * FROM " + DBHelper.TABLE_EMPLOYEE + " WHERE email = ?";
        List<Employee> list = getData(sql, email);
        if (list.size() > 0) {
            return list.get(0).getStatus();
        }
        return null;
    }
    private List<Employee> getData(String sql, String... selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<Employee> list = new ArrayList<>();

        Cursor c = db.rawQuery(sql, selectionArgs);
        while (c.moveToNext()) {
            list.add(new Employee(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8)));
        }
        return list;
    }
}
