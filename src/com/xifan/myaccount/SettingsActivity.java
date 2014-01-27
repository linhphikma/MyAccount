
package com.xifan.myaccount;

import android.app.FragmentTransaction;
import android.os.Bundle;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class SettingsActivity extends SwipeBackActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {
        boolean isAccountManage = getIntent().getStringExtra("entry").equals(
                AccountManage.class.getSimpleName());
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content,
                isAccountManage ? new AccountManage() : new Settings()).commit();
        if (isAccountManage) {
            getActionBar().setTitle(R.string.title_activity_account_manage);
        }
    }
}
