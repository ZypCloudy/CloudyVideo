package com.example.cloudy.cloudyvideo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.cloudy.cloudyvideo.base.BasePager;

public class ReplaceFragment extends Fragment {
    private BasePager currPager;

    public ReplaceFragment(){

    }

    public ReplaceFragment(BasePager pager) {
        this.currPager = pager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return currPager.rootView;
    }
}
