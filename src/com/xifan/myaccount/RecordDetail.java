
package com.xifan.myaccount;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class RecordDetail extends SwipeBackActivity {

    private ActionBar mActionBar;

    public static final int REQUEST_ADD_RECORD = 1;
    public static final int REQUEST_SHOW_RECORD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);

        mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (getIntent().getExtras().getInt("type") == REQUEST_ADD_RECORD) {
            mActionBar.setTitle(R.string.add_record_title);
            ft.replace(R.id.fragment, new AddRecord());
        } else if (getIntent().getExtras().getInt("type") == REQUEST_SHOW_RECORD) {
            mActionBar.setTitle(R.string.show_record_title);
            ft.replace(R.id.fragment, new ShowRecord());
        }
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.record_detail, menu);
        return true;
    }

}
