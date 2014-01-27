
package com.xifan.myaccount;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.xifan.myaccount.data.Account;
import com.xifan.myaccount.db.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class AccountManage extends PreferenceFragment implements OnPreferenceChangeListener {

    private ListPreference mAccountList;
    private CheckBoxPreference mDepositCardAccount;
    private CheckBoxPreference mCreditCardAccount;
    private CheckBoxPreference mAlipayAccount;
    private Preference mCashAccount;
    private Preference mAddAccount;

    private List<String> mAccountTypeList;

    private int mCurrentAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_manage_account);
        init();
    }

    private void init() {
        mAccountList = (ListPreference) findPreference("current_account");
        // mDepositCardAccount = (CheckBoxPreference)
        // findPreference("account2");
        // mCreditCardAccount = (CheckBoxPreference) findPreference("account3");
        // mAlipayAccount = (CheckBoxPreference) findPreference("account4");
        mCashAccount = (Preference) findPreference("defaultAccount");
        mAddAccount = (Preference) findPreference("add_account");

        mCurrentAccount = Account.currentAccountId;
        mAccountTypeList = new ArrayList<String>();

        DbHelper helper = new DbHelper(getActivity(), DbHelper.DB_NAME, null, DbHelper.version);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from account_type", null);
        while (c.moveToNext()) {
            mAccountTypeList.add(c.getString(c.getColumnIndex("typename")));
        } // init accountTypeList

        Cursor c1 = db.rawQuery("select id from account", null);
        String[] entries = new String[c1.getCount()];
        String[] entryValues = new String[c1.getCount()];
        c1.moveToNext();
        for (int i = 0; !c1.isAfterLast(); i++) {
            entries[i] = mAccountTypeList.get(c1.getInt(c1.getColumnIndex("accountType")));
            entryValues[i] = String.valueOf(i);
            c1.moveToNext();
        }

        mAccountList.setEntries(entries);
        mAccountList.setEntryValues(entryValues);
        mAccountList.setValue(entryValues[mCurrentAccount - 1]);
        mAccountList.setOnPreferenceChangeListener(this);

        mCashAccount.setTitle(mAccountTypeList.get(0));
        mCashAccount.setSummary(R.string.floating_bar_total);
        mCashAccount.setLayoutResource(R.layout.view_pref_default_account);
        View view = getActivity().getLayoutInflater().inflate(R.layout.view_pref_default_account,
                null);
        TextView stateText = (TextView) view.findViewById(R.id.state);
        stateText.setText(R.string.pref_account_manage_default_account);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mAddAccount) {
            // TODO 新增账户
            Log.e("Tag", "add account");
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mAccountList) {
            Account.currentAccountId = Integer.parseInt(newValue.toString());
            SharedPreferences prefs = getActivity().getSharedPreferences("pref", 0);
            mCurrentAccount = prefs.getInt("currentAccount", 1);
            if (mCurrentAccount != Account.currentAccountId) {
                mCurrentAccount = Account.currentAccountId;
                prefs.edit().putInt("currentAccount", mCurrentAccount).apply();
            }
        }
        return false;
    }

}
