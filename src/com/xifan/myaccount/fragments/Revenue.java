
package com.xifan.myaccount.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.xifan.myaccount.R;
import com.xifan.myaccount.SearchTypeActivity;
import com.xifan.myaccount.data.Account;
import com.xifan.myaccount.data.AccountDetail;
import com.xifan.myaccount.util.DbHelper;
import com.xifan.myaccount.util.SmartType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class Revenue extends Fragment implements OnClickListener, OnCancelListener,
        android.content.DialogInterface.OnClickListener {
    // TODO 摇一摇

    private LayoutInflater mInflater;
    private Context mContext;

    private CheckBox mReimbursabledBox;
    private CheckBox mLocationBox;
    private Spinner mAccountSpinner;
    private Button mTypeButton;
    private TextView mDateTextView;
    private TextView mMoneyTextView;
    private EditText mInputText;
    private EditText mNoteText;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private Calendar mCalendar;

    private SimpleDateFormat mDateFomatter;

    private int clickItem;
    private boolean isExpend;
    private int mTypeId;
    private String mTypeName;

    private static final int ITEM_MONEY_VIEW = 1;
    private static final int ITEM_DATE_VIEW = 2;
    private static final int REQUEST_ACITIVITY_GET_TYPE_CODE = 0;

    public static final int REQUEST_EXPEND = 1;
    public static final int REQUEST_REVENUE = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        mContext = getActivity();
        View view = mInflater.inflate(R.layout.add_record_revenue, null);
        mAccountSpinner = (Spinner) view.findViewById(R.id.account_spinner);
        mTypeButton = (Button) view.findViewById(R.id.type);
        mDateTextView = (TextView) view.findViewById(R.id.date);
        mMoneyTextView = (TextView) view.findViewById(R.id.money);
        mNoteText = (EditText) view.findViewById(R.id.note);
        mReimbursabledBox = (CheckBox) view.findViewById(R.id.reimbursable);
        mLocationBox = (CheckBox) view.findViewById(R.id.location);

        mTypeButton.setOnClickListener(this);

        mCalendar = Calendar.getInstance();
        mDateFomatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        SmartType st = new SmartType(mContext);
        List<Account> account = st.getAccountList();
        Iterator<Account> accounts = st.getAccountList().iterator();
        List<String> types = st.getAccountTypeList();
        String[] list = new String[account.size()];
        int i = 0;
        while (accounts.hasNext()) {
            Account ac = accounts.next();
            boolean hasName = !TextUtils.isEmpty(ac.getAccountName());
            list[i] = hasName ? ac.getAccountName() : types.get(ac.getAccountType() - 1);
        }

        mAccountSpinner.setAdapter(new ArrayAdapter<String>(mContext,
                android.R.layout.simple_dropdown_item_1line, list));
        mDateTextView.setText(mDateFomatter.format(mCalendar.getTime()));
        mDateTextView.setOnClickListener(this);
        mMoneyTextView.setOnClickListener(this);
        mMoneyTextView.setTextColor(isExpend ? Color.RED : Color.GREEN);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.money:
                clickItem = ITEM_MONEY_VIEW;

                mInputText = new EditText(mContext);
                mInputText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                mInputText.setInputType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                mInputText.setWidth(mInputText.getMaxWidth());
                LinearLayout layout = new LinearLayout(mContext);
                layout.addView(mInputText);
                LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                param.setMargins(0, 10, 0, 0);
                param.gravity = Gravity.CENTER;
                mInputText.setLayoutParams(param);

                new AlertDialog.Builder(mContext)
                        .setTitle(getResources().getString(R.string.msg_input))
                        .setView(layout)
                        .setPositiveButton(getResources().getString(R.string.ok), this)
                        .setNegativeButton(getResources().getString(R.string.cancel), this).show();
                break;
            case R.id.date:
                clickItem = ITEM_DATE_VIEW;
                View view = mInflater.inflate(R.layout.datetime_picker_view, null);
                mDatePicker = (DatePicker) view.findViewById(R.id.datepicker);
                mTimePicker = (TimePicker) view.findViewById(R.id.timepicker);
                mDatePicker.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH),
                        new DatePicker.OnDateChangedListener() {

                            @Override
                            public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                    int dayOfMonth) {
                                mCalendar.set(year, monthOfYear, dayOfMonth);
                            }
                        });
                mTimePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        mCalendar.set(Calendar.HOUR, hourOfDay);
                        mCalendar.set(Calendar.MINUTE, minute);

                    }
                });
                new AlertDialog.Builder(mContext)
                        .setTitle(getResources().getString(R.string.msg_input_date))
                        .setView(view)
                        .setPositiveButton(getResources().getString(R.string.ok), this)
                        .setNegativeButton(getResources().getString(R.string.cancel), this)
                        .show();
                break;
            case R.id.type:
                Intent intent = new Intent(mContext, SearchTypeActivity.class);
                intent.putExtra("opType", isExpend ? 1 : 2);
                startActivityForResult(intent, REQUEST_ACITIVITY_GET_TYPE_CODE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            mTypeId = data.getIntExtra("typeId", 0);
            mTypeName = data.getStringExtra("typeName");
            mTypeButton.setText(mTypeName);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (clickItem) {
            case ITEM_MONEY_VIEW:
                if (which == Dialog.BUTTON_POSITIVE) {
                    mMoneyTextView.setText(mInputText.getText().toString());
                } else {
                    dialog.cancel();
                }
                break;
            case ITEM_DATE_VIEW:
                if (which == Dialog.BUTTON_POSITIVE) {
                    mDateTextView.setText(mDateFomatter.format(mCalendar.getTime()));
                } else {
                    dialog.cancel();
                }
                break;
        }

    }

    @Override
    public void onCancel(DialogInterface dialog) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int request = getArguments().getInt("opType", 1);
        isExpend = request == REQUEST_EXPEND;
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add("confirm").setIcon(R.drawable.ic_add_record_confirm)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("confirm")) {
            submit();
        } else if (item.getItemId() == android.R.id.home) {
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void submit() {

        AccountDetail detail = new AccountDetail();
        detail.setAccountId(Account.currentAccountId);
        detail.setDate(mDateFomatter.format(mCalendar.getTime()));
        detail.setMoney(Float.valueOf(mMoneyTextView.getText().toString().replace("￥", "")
                .replace(",", "")));
        detail.setNote(mNoteText.getText().toString());
        detail.setPicUri(""); // TODO pic
        detail.setRecordType(mTypeId);
        detail.setReimbursabled(mReimbursabledBox.isChecked() ? 1 : 0);
        if (mLocationBox.isChecked()) {
            detail.setLocation("地球"); // TODO location
        }

        DbHelper db = new DbHelper(mContext, DbHelper.DB_NAME, null,
                DbHelper.version);
        try {
            ContentValues cv = new ContentValues();
            cv.put("accountId", detail.getAccountId());
            cv.put("recordOp", getOpeateInt(isExpend));
            cv.put("recordType", detail.getRecordType());
            cv.put("moneyAmount", detail.getMoney());
            cv.put("picUri", detail.getPicUri());
            cv.put("recordDate", detail.getDate());
            cv.put("location", detail.getLocation());
            cv.put("note", detail.getNote());
            cv.put("isReimbursabled", detail.isReimbursabled());
            db.doInsert("detail", cv);
            db.syncAccount(detail.getMoney(), detail.getRecordType(), getOpeateInt(isExpend));
            db.close();
            Log.e("xifan", "done");
            getActivity().setResult(-1);
            getActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getOpeateInt(boolean val) {
        return isExpend ? 1 : 2;
    }

}
