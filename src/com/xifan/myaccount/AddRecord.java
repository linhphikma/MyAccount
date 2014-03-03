
package com.xifan.myaccount;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.xifan.myaccount.fragments.Revenue;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import java.util.ArrayList;
import java.util.List;

public class AddRecord extends SwipeBackActivity {

    private List<Fragment> mFragmentList;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        FragmentManager fm = getFragmentManager();
        mFragmentList = new ArrayList<Fragment>();

        Revenue expend = new Revenue();
        Bundle b = new Bundle();
        b.putInt("opType", 1);
        expend.setArguments(b);

        Revenue rev = new Revenue();
        Bundle b1 = new Bundle();
        b1.putInt("opType", 2);
        rev.setArguments(b1);

        mFragmentList.add(expend);
        mFragmentList.add(rev);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new OpeateTypeAdapter(fm));
    }

    private class OpeateTypeAdapter extends FragmentPagerAdapter {

        public OpeateTypeAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int location) {
            return mFragmentList.get(location);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

    }
}
