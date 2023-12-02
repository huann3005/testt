package com.example.duan1.admin.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.duan1.R;

public abstract class BaseFragment<B extends ViewDataBinding> extends Fragment {
    protected B binding;
    protected abstract int getLayoutId();
    protected abstract void initEvent();
    protected abstract void initData();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        initData();
        initEvent();
        return binding.getRoot();
    }
    public static void add(AppCompatActivity appCompatActivity, Fragment fragment) {
        if (appCompatActivity.getSupportFragmentManager().findFragmentByTag(fragment.getClass().getName()) == null) {
            addFragmentStartToEnd(appCompatActivity, fragment, fragment.getClass().getName());
        }
    }

    private static void addFragmentStartToEnd(AppCompatActivity appCompatActivity, Fragment fragment, String str) {
        appCompatActivity
                .getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.main, fragment, str).commit();
    }
    public abstract String getTAG();
}
