
package com.xifan.myaccount.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xifan.myaccount.R;
import com.xifan.myaccount.data.Account;
import com.xifan.myaccount.util.DbHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AccountManage extends PreferenceFragment implements OnPreferenceChangeListener {

    private ListPreference mAccountPref;
    private Preference mAddAccount;
    private PreferenceCategory mEditCategory;

    private List<String> mAccountTypeList;

    private Context mContext;

    private int mCurrentAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_manage_account);
        mContext = getActivity();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    private void init() {
        mEditCategory = (PreferenceCategory) findPreference("edit_category");
        mAccountPref = (ListPreference) findPreference("current_account");
        mAddAccount = (Preference) findPreference("add_account");

        mCurrentAccount = Account.currentAccountId;
        mAccountTypeList = new ArrayList<String>();

        PreferenceScreen prefScreen = getPreferenceScreen();

        DbHelper helper = new DbHelper(mContext, DbHelper.DB_NAME, null, DbHelper.version);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from account_type", null);
        while (c.moveToNext()) {
            mAccountTypeList.add(c.getString(c.getColumnIndex("typename")));
        } // init accountTypeList

        c = db.rawQuery("select * from account", null);
        List<Account> accountList = new ArrayList<Account>();
        while (c.moveToNext()) {
            Account account = new Account();
            account.setAccountName(c.getString(c.getColumnIndex("accountName")));
            account.setAccountType(c.getInt(c.getColumnIndex("accountType")));
            account.setTotal(c.getInt(c.getColumnIndex("total")));
            account.setExpend(c.getInt(c.getColumnIndex("expend")));
            account.setId(c.getInt(c.getColumnIndex("id")));
            account.setRevenue(c.getFloat(c.getColumnIndex("revenue")));
            accountList.add(account);
        }

        String[] entries = new String[accountList.size()];
        String[] entryValues = new String[accountList.size()];

        int currentIndex = 0;
        for (int i = 0; i < accountList.size(); i++) {
            String tmpName = accountList.get(i).getAccountName();
            if (tmpName.equals("")) {
                entries[i] = mAccountTypeList.get(accountList.get(i).getAccountType() - 1);
            } else {
                entries[i] = tmpName;
            }
            if (accountList.get(i).getId() == mCurrentAccount)
                currentIndex = i;
            entryValues[i] = String.valueOf(i);
        }

        mAccountPref.setEntries(entries);
        mAccountPref.setEntryValues(entryValues);
        mAccountPref.setValueIndex(currentIndex);
        mAccountPref.setOnPreferenceChangeListener(this);

        int counter = 1;
        for (Account ac : accountList) {
            if (ac.getId() == 1) {
                // Default cash account
                Preference newPref = findPreference("default_account");
                newPref.setTitle(mAccountTypeList.get(0));
                newPref.setSummary(getResources().getString(R.string.floating_bar_total)
                        + " " + ac.getTotal());
                mEditCategory.addPreference(newPref);
            } else {
                CheckBoxPreference newPref = new CheckBoxPreference(mContext);
                newPref.setKey("extraAccount" + counter);
                newPref.setSummary(getResources().getString(R.string.floating_bar_total)
                        + ac.getTotal());
                newPref.setChecked(true);
                newPref.setTitle(ac.getAccountName().equals("") ? mAccountTypeList.get(ac
                        .getAccountType() - 1) : ac.getAccountName());
                mEditCategory.addPreference(newPref);
            }
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        if (key == mAddAccount.getKey()) {
            // TODO 新增账户
            Log.e("Tag", "add account");
        } else if (key.equals("default_account")) {
            Toast.makeText(mContext, R.string.pref_account_manage_default_account,
                    Toast.LENGTH_SHORT).show();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mAccountPref) {
            Account.currentAccountId = Integer.parseInt(newValue.toString()) + 1;
            SharedPreferences prefs = mContext.getSharedPreferences("pref", 0);
            mCurrentAccount = prefs.getInt("currentAccount", 1);
            if (mCurrentAccount != Account.currentAccountId) {
                mCurrentAccount = Account.currentAccountId;
                prefs.edit().putInt("currentAccount", mCurrentAccount).apply();
            }
        }
        return false;
    }

}
