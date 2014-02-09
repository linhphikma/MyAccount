
package com.xifan.myaccount.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import com.xifan.myaccount.R;
import com.xifan.myaccount.data.Account;
import com.xifan.myaccount.util.DbHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccountManage extends PreferenceFragment implements OnPreferenceChangeListener {

    private ListPreference mAccountPref;
    private Preference mAddAccount;
    private PreferenceCategory mEditCategory;

    private List<String> mAccountTypeList;
    private List<Account> mAccountList;

    private Context mContext;
    private LoadTask mTask;

    private int mCurrentAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_manage_account);

        mContext = getActivity();
        mTask = new LoadTask();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    private void init() {
        mEditCategory = (PreferenceCategory) findPreference("edit_category");
        mAccountPref = (ListPreference) findPreference("current_account");
        mAddAccount = (Preference) findPreference("add_account");
        mCurrentAccount = Account.currentAccountId;
        mTask.execute();
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

    public class LoadTask extends AsyncTask<Void, Void, HashMap<String, Object>> {

        @Override
        protected HashMap<String, Object> doInBackground(Void... params) {
            mAccountTypeList = new ArrayList<String>();
            mAccountList = new ArrayList<Account>();
            DbHelper db = new DbHelper(mContext, DbHelper.DB_NAME, null, DbHelper.version);
            Cursor c = db.doQuery("select * from account_type", null);
            while (c.moveToNext()) {
                mAccountTypeList.add(c.getString(c.getColumnIndex("typename")));
            } // init accountTypeList

            c = db.doQuery("select * from account", null);
            while (c.moveToNext()) {
                Account account = new Account();
                account.setAccountName(c.getString(c.getColumnIndex("accountName")));
                account.setAccountType(c.getInt(c.getColumnIndex("accountType")));
                account.setTotal(c.getInt(c.getColumnIndex("total")));
                account.setExpend(c.getInt(c.getColumnIndex("expend")));
                account.setId(c.getInt(c.getColumnIndex("id")));
                account.setRevenue(c.getFloat(c.getColumnIndex("revenue")));
                mAccountList.add(account);
            }

            String[] entries = new String[mAccountList.size()];
            String[] entryValues = new String[mAccountList.size()];

            int currentIndex = 0;
            for (int i = 0; i < mAccountList.size(); i++) {
                String tmpName = mAccountList.get(i).getAccountName();
                if (tmpName.equals("")) {
                    entries[i] = mAccountTypeList.get(mAccountList.get(i).getAccountType() - 1);
                } else {
                    entries[i] = tmpName;
                }
                if (mAccountList.get(i).getId() == mCurrentAccount)
                    currentIndex = i;
                entryValues[i] = String.valueOf(i);
            }
            db.closeAll(c);

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("Entries", entries);
            map.put("Values", entryValues);
            map.put("Index", currentIndex);
            return map;
        }

        @Override
        protected void onPostExecute(HashMap<String, Object> result) {
            super.onPostExecute(result);
            mAccountPref.setEntries((String[]) result.get("Entries"));
            mAccountPref.setEntryValues((String[]) result.get("Values"));
            mAccountPref.setValueIndex((Integer) result.get("Index"));
            mAccountPref.setOnPreferenceChangeListener(AccountManage.this);

            int counter = 1;
            for (Account ac : mAccountList) {
                if (ac.getId() == 1) {
                    // Default cash account
                    Preference newPref = new Preference(mContext);
                    newPref.setTitle(mAccountTypeList.get(0));
                    newPref.setSummary(getResources().getString(
                            R.string.pref_account_manage_detail_balance)
                            + " " + ac.getTotal());
                    mEditCategory.addPreference(newPref);
                    newPref.setOrder(0);
                } else {
                    CheckBoxPreference newPref = new CheckBoxPreference(mContext);
                    newPref.setKey("extraAccount" + counter);
                    newPref.setSummary(getResources().getString(
                            R.string.pref_account_manage_detail_balance)
                            + ac.getTotal());
                    newPref.setChecked(true);
                    newPref.setTitle(ac.getAccountName().equals("") ? mAccountTypeList.get(ac
                            .getAccountType() - 1) : ac.getAccountName());
                    mEditCategory.addPreference(newPref);
                }
            }
            mAddAccount.setOrder(99);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTask.cancel(true);
    }

}
