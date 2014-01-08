
package com.xifan.myaccount;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ShowRecord extends Fragment {
    private LayoutInflater mInflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        mInflater.inflate(R.layout.fragment_show_record, container);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
