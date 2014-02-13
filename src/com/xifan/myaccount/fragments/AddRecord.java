
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
import java.util.Locale;

public class AddRecord extends Fragment implements OnClickListener, OnCancelListener,
        android.content.DialogInterface.OnClickListener {
    // TODO 摇一摇

    private LayoutInflater mInflater;
    private Context mContext;

    private CheckBox reimbursabledBox;
    private CheckBox locationBox;
    private Spinner accountSpinner;
    private TextView typeTextView;
    private TextView dateTextView;
    private TextView moneyTextView;
    private EditText inputText;
    private EditText noteText;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Calendar mCalendar;

    private SimpleDateFormat dateFomatter;

    private int clickItem;

    private static final int ITEM_MONEY_VIEW = 1;
    private static final int ITEM_DATE_VIEW = 2;
    private static final int REQUEST_ACITIVITY_GET_TYPE_CODE = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        mContext = getActivity();
        View view = mInflater.inflate(R.layout.fragment_add_record, container);
        accountSpinner = (Spinner) view.findViewById(R.id.account_spinner);
        typeTextView = (TextView) view.findViewById(R.id.type);
        dateTextView = (TextView) view.findViewById(R.id.date);
        moneyTextView = (TextView) view.findViewById(R.id.money);
        noteText = (EditText) view.findViewById(R.id.note);
        reimbursabledBox = (CheckBox) view.findViewById(R.id.reimbursable);
        locationBox = (CheckBox) view.findViewById(R.id.location);

        typeTextView.setOnClickListener(this);

        mCalendar = Calendar
                .getInstance();
        dateFomatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        dateTextView.setText(dateFomatter.format(mCalendar.getTime()));
        dateTextView.setOnClickListener(this);
        moneyTextView.setOnClickListener(this);
        setMoneyColor();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void setMoneyColor() {
        switch (accountSpinner.getSelectedItemPosition()) {
            case 0:
                moneyTextView.setTextColor(Color.RED);
                break;
            case 1:
                moneyTextView.setTextColor(Color.GREEN);
                break;
            default:
                moneyTextView.setTextColor(Color.BLACK);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.money:
                clickItem = ITEM_MONEY_VIEW;

                inputText = new EditText(mContext);
                inputText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                inputText.setInputType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                inputText.setWidth(inputText.getMaxWidth());
                LinearLayout layout = new LinearLayout(mContext);
                layout.addView(inputText);
                LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                param.setMargins(0, 10, 0, 0);
                param.gravity = Gravity.CENTER;
                inputText.setLayoutParams(param);

                new AlertDialog.Builder(mContext)
                        .setTitle(getResources().getString(R.string.msg_input))
                        .setView(layout)
                        .setPositiveButton(getResources().getString(R.string.ok), this)
                        .setNegativeButton(getResources().getString(R.string.cancel), this).show();
                break;
            case R.id.date:
                clickItem = ITEM_DATE_VIEW;
                View view = mInflater.inflate(R.layout.datetime_picker_view, null);
                datePicker = (DatePicker) view.findViewById(R.id.datepicker);
                timePicker = (TimePicker) view.findViewById(R.id.timepicker);
                datePicker.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH),
                        new DatePicker.OnDateChangedListener() {

                            @Override
                            public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                    int dayOfMonth) {
                                mCalendar.set(year, monthOfYear, dayOfMonth);
                            }
                        });
                timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

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
                startActivityForResult(new Intent(mContext, SearchTypeActivity.class),
                        REQUEST_ACITIVITY_GET_TYPE_CODE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (clickItem) {
            case ITEM_MONEY_VIEW:
                if (which == Dialog.BUTTON_POSITIVE) {
                    moneyTextView.setText(inputText.getText().toString());
                } else {
                    dialog.cancel();
                }
                break;
            case ITEM_DATE_VIEW:
                if (which == Dialog.BUTTON_POSITIVE) {
                    dateTextView.setText(dateFomatter.format(mCalendar.getTime()));
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
        SmartType type = new SmartType(mContext);
        detail.setAccountId(Account.currentAccountId);
        detail.setDate(dateFomatter.format(mCalendar.getTime()));
        detail.setMoney(Float.valueOf(moneyTextView.getText().toString().replace("￥", "")
                .replace(",", "")));
        detail.setNote(noteText.getText().toString());
        detail.setPicUri(""); // TODO pic
        detail.setRecordType(type.getTypeIndex(typeTextView.getText().toString()));
        detail.setReimbursabled(reimbursabledBox.isChecked() ? 1 : 0);
        if (locationBox.isChecked()) {
            detail.setLocation("重庆文理学院"); // TODO location
        }

        DbHelper db = new DbHelper(mContext, DbHelper.DB_NAME, null,
                DbHelper.version);
        try {
            ContentValues cv = new ContentValues();
            cv.put("accountId", detail.getAccountId());
            cv.put("recordOp", accountSpinner.getSelectedItemPosition());
            cv.put("recordType", detail.getRecordType());
            cv.put("moneyAmount", detail.getMoney());
            cv.put("picUri", detail.getPicUri());
            cv.put("recordDate", detail.getDate());
            cv.put("location", detail.getLocation());
            cv.put("note", detail.getNote());
            cv.put("isReimbursabled", detail.isReimbursabled());
            db.doInsert("detail", cv);
            db.syncAccount(detail.getMoney(), accountSpinner.getSelectedItemPosition());
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("xifan", "done");
        getActivity().finish();
    }

}
