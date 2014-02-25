
package com.xifan.myaccount;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xifan.myaccount.R;
import com.xifan.myaccount.data.AccountDetail;
import com.xifan.myaccount.util.DbHelper;
import com.xifan.myaccount.util.SmartType;
import com.xifan.myaccount.widget.MoneyView;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ShowRecord extends SwipeBackActivity implements OnClickListener,
        DialogInterface.OnClickListener {

    private Context mContext;
    private Bitmap bmp;

    private MoneyView moneyView;
    private TextView dateText;
    private Spinner typeSpinner;
    private TextView locationText;
    private TextView noteText;
    private RelativeLayout bgBlank;
    private ImageView bgView;
    private EditText inputText;
    private TextView tipText;
    private MenuItem confirmButton;

    private AccountDetail mDetail;

    private int clickItem;
    private File picUri;
    private File picPath;
    private Uri albumPicUri;

    private static final int INDEX_MONEY = 1;
    private static final int INDEX_DATE = 2;
    private static final int INDEX_LOC = 4;
    private static final int INDEX_NOTE = 5;

    private static final int SELECT_CAMERA = 0;
    private static final int SELECT_PICTURE = 1;

    private static final String TAG = "MaiBen";

    private boolean hasChanges = false;

    private boolean firstRun = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_record);
        mContext = this;
        mDetail = getIntent().getParcelableExtra("detail");

        // init storage path
        picPath = new File(Environment.getExternalStorageDirectory()
                + File.separator
                + "MaiBen");
        if (!picPath.exists()) {
            picPath.mkdir();
        }
        bmp = null;

        initView();
    }

    private void initView() {
        moneyView = (MoneyView) findViewById(R.id.details_money);
        dateText = (TextView) findViewById(R.id.details_date);
        typeSpinner = (Spinner) findViewById(R.id.details_type);
        locationText = (TextView) findViewById(R.id.details_location);
        noteText = (TextView) findViewById(R.id.details_note);
        bgView = (ImageView) findViewById(R.id.background);
        bgBlank = (RelativeLayout) findViewById(R.id.details_blank);
        tipText = (TextView) findViewById(R.id.details_add_tip);

        moneyView.setText(mDetail.getMoney());
        dateText.setText(mDetail.getDate());
        typeSpinner.setAdapter(new ArrayAdapter<String>(mContext, R.layout.type_spinner_view,
                new SmartType(mContext).getTypeName()));
        locationText.setText(mDetail.getLocation());
        noteText.setText(mDetail.getNote());

        if (mDetail.getPicUri() != null) {
            try {
                bmp = BitmapFactory.decodeStream(mContext.getContentResolver()
                        .openInputStream(Uri.parse(mDetail.getPicUri())));
                bgView.setImageBitmap(bmp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.w(TAG, "img is not from album,attempt to read from camera");
                // gView.setBackground(Drawable.createFromPath(mDetail.getPicUri()));
            }
        }

        moneyView.setOnClickListener(this);
        dateText.setOnClickListener(this);
        locationText.setOnClickListener(this);
        noteText.setOnClickListener(this);
        bgBlank.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle(getResources().getString(R.string.show_record_choose_pic_source))
                        .setItems(getResources().getStringArray(R.array.choose_pic_source),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == SELECT_PICTURE) {
                                            Intent intent = new Intent(
                                                    Intent.ACTION_PICK,
                                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                            startActivityForResult(intent, SELECT_PICTURE);
                                        } else {
                                            Intent intent = new Intent(
                                                    MediaStore.ACTION_IMAGE_CAPTURE);
                                            SimpleDateFormat sdf = new SimpleDateFormat(
                                                    "yyyyMMddHHmm",
                                                    Locale
                                                            .getDefault());
                                            // Preset the uri for storage.
                                            picUri = new File(picPath + File.separator
                                                    + sdf.format(Calendar.getInstance().getTime())
                                                    + "-date.jpg");
                                            Uri imageUri = Uri.fromFile(picUri);
                                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                            startActivityForResult(intent, SELECT_CAMERA);
                                        }
                                    }
                                }).show();
                return true;
            }
        });
        typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int
                    position, long id) {
                if (!firstRun) {
                    notifyRecordChanges(true);
                } else {
                    firstRun = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        typeSpinner.setSelection(mDetail.getRecordType(), true);

        tipText.setVisibility(bgView.getDrawable() == null ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof TextView) {
            String title = getResources().getString(R.string.msg_edit);
            if (v == moneyView) {
                clickItem = INDEX_MONEY;
                title += getResources().getString(R.string.money);
            } else if (v == dateText) {
                clickItem = INDEX_DATE;
                title += getResources().getString(R.string.date);
            } else if (v == locationText) {
                clickItem = INDEX_LOC;
                title += getResources().getString(R.string.location);
            } else if (v == noteText) {
                clickItem = INDEX_NOTE;
                title += getResources().getString(R.string.note);
            }

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
                    .setTitle(title)
                    .setView(layout)
                    .setPositiveButton(getResources().getString(R.string.ok), this)
                    .setNegativeButton(getResources().getString(R.string.cancel), this).show();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_POSITIVE) {
            switch (clickItem) {
                case 0:
                    writeToDb();
                    setResult(RESULT_OK);
                    finish();
                case 1:
                    moneyView.setText(inputText.getText());
                    break;
                case 2:
                    dateText.setText(inputText.getText());
                    break;
                case 4:
                    locationText.setText(inputText.getText());
                    break;
                case 5:
                    noteText.setText(inputText.getText());
                    break;
            }
            notifyRecordChanges(true);
        } else if (which == Dialog.BUTTON_POSITIVE) {
            if (clickItem == 0) {
                setResult(RESULT_OK);
                finish();
            }
        }

    }

    protected void notifyRecordChanges(boolean haschanges) {
        hasChanges = haschanges;
        invalidateOptionsMenu();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            if (requestCode == SELECT_PICTURE) {
                // 选择图片
                ContentResolver cr = mContext.getContentResolver();
                try {
                    if (bmp != null)
                        bmp.recycle();
                    bmp = BitmapFactory.decodeStream(cr.openInputStream(data.getData()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                albumPicUri = data.getData();
                bgView.setImageBitmap(bmp);
            } else if (requestCode == SELECT_CAMERA) {
                if (bmp != null)
                    bmp.recycle();
                Bitmap bitmap = BitmapFactory.decodeFile(picUri.getPath());
                bmp = Bitmap.createScaledBitmap(bitmap, metrics.widthPixels, metrics.heightPixels,
                        true);
                bgView.setImageBitmap(bmp);
            }
            notifyRecordChanges(true);
            tipText.setVisibility(bgView.getDrawable() == null ? View.VISIBLE : View.GONE);
        } else {
            Toast.makeText(mContext,
                    getResources().getString(R.string.show_record_choose_pic_failed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        confirmButton = menu.add("confirm");
        confirmButton.setIcon(R.drawable.ic_add_record_confirm)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        if (!hasChanges) {
            menu.removeItem(confirmButton.getItemId());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (hasChanges) {
                TextView tv = new TextView(mContext);
                tv.setText(getResources().getString(R.string.show_record_changes_alert));
                new AlertDialog.Builder(mContext)
                        .setTitle(getResources().getString(R.string.attention))
                        .setView(tv)
                        .setPositiveButton(getResources().getString(R.string.ok), this)
                        .setNegativeButton(getResources().getString(R.string.cancel), this).show();
                clickItem = 0;
            }
        } else if (item.getItemId() == confirmButton.getItemId()) {
            writeToDb();
            notifyRecordChanges(false);
        }
        return super.onOptionsItemSelected(item);
    }

    private void writeToDb() {
        DbHelper db = new DbHelper(mContext, DbHelper.DB_NAME, null,
                DbHelper.version);
        ContentValues cv = new ContentValues();
        if (bmp != null)
            cv.put("picUri",
                    albumPicUri == null ? picUri.getPath() : albumPicUri.toString());
        cv.put("recordType", typeSpinner.getSelectedItemPosition());
        cv.put("moneyAmount", Float.valueOf(moneyView.getText().toString().replace("￥", "")
                .replace(",", "")));
        cv.put("recordDate", dateText.getText().toString());
        cv.put("note", noteText.getText().toString());

        db.doUpdate("detail", cv, "id=?", new String[] {
                String.valueOf(mDetail.getId())
        });
    }

}
