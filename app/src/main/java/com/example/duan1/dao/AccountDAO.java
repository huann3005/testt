package com.example.duan1.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.duan1.database.DBHelper;
import com.example.duan1.model.Account;
import com.example.duan1.model.Customer;

import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    DBHelper dbHelper;

    public AccountDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public Account getEmail(String email, String TABLE) {
        String sql = "SELECT * FROM " + TABLE + " WHERE email = ?";
        List<Account> list = getData(sql,TABLE, email);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public List<Account> getAll(String TABLE) {
        String sql = "SELECT * FROM " + TABLE;
        List<Account> list = getData(sql, TABLE);
        if (list.size() > 0) {
            return list;
        }
        return null;
    }


    public String getRole(String email, String TABLE) {
        String sql = "SELECT * FROM " + TABLE + " WHERE email = ?";
        List<Account> list = getData(sql, TABLE, email);
        if (list.size() > 0) {
            return list.get(0).getRole();
        }
        return null;
    }

    public boolean insert(Account object) {
        try{
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String sql = "INSERT INTO " + DBHelper.TABLE_ACCOUNT_CUSTOMER + " VALUES(?,?)";
            db.execSQL(sql, new String[]{object.getEmail(), object.getPassword()});
            if(db != null) {
                db.close();
            }
        }catch (Exception e){
            Log.d("Error", e.toString());
            return false;
        }
        return true;
    }

    public boolean insertEmployee(Account object) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try{
            String sql = "INSERT INTO " + DBHelper.TABLE_ACCOUNT_SHOP  + " (email, password, role) " + " VALUES(?,?,?)";
            db.execSQL(sql, new String[]{object.getEmail(), "chicken", object.getRole()});
            if(db != null) {
                db.close();
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public boolean updatee(Account object, String id, String TABLE) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String sql = "UPDATE " + TABLE + " SET password = ? WHERE email = ?";
        sqLiteDatabase.execSQL(sql, new String[]{object.getPassword(), id});
        if(sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
        return true;
    }

    public void delete(Object object) {

    }

    private List<Account> getData(String sql, String TABLE, String... selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<Account> list = new ArrayList<>();

        Cursor c = db.rawQuery(sql, selectionArgs);
        if (TABLE.equals(DBHelper.TABLE_ACCOUNT_CUSTOMER)) {
            while (c.moveToNext()) {
                list.add(new Account(c.getString(0), c.getString(1), null));
            }
        } else {
            while (c.moveToNext()) {
                list.add(new Account(c.getString(0), c.getString(1), c.getString(2)));
            }
        }

        return list;
    }

    public boolean checkLogin(String email, String password, String TABLE) {
        String sql = "SELECT * FROM " + TABLE + " WHERE email = ? AND password = ?";
        List<Account> list = getData(sql, TABLE, email, password);
        if (list.size() > 0) {
            return true;
        }
        return false;
    }
}
