
package com.xifan.myaccount;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xifan.myaccount.data.AccountDetail;
import com.xifan.myaccount.data.SmartType;
import com.xifan.myaccount.db.DbHelper;
import com.xifan.myaccount.widget.MoneyView;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ShowRecord extends Fragment implements OnClickListener,
        DialogInterface.OnClickListener {

    private LayoutInflater mInflater;
    private Context mContext;
    private Bitmap bmp;

    private MoneyView moneyView;
    private TextView dateText;
    private TextView typeText;
    private TextView locationText;
    private TextView noteText;
    private RelativeLayout bgBlank;
    private ImageView bgView;
    private EditText inputText;

    private AccountDetail mDetail;

    private int clickItem;
    private File picUri;
    private File picPath;

    private static final int INDEX_MONEY = 1;
    private static final int INDEX_DATE = 2;
    private static final int INDEX_TYPE = 3;
    private static final int INDEX_LOC = 4;
    private static final int INDEX_NOTE = 5;

    private static final int SELECT_CAMERA = 0;
    private static final int SELECT_PICTURE = 1;

    private static final String TAG = "MaiBen";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        View view = mInflater.inflate(R.layout.fragment_show_record, container);
        moneyView = (MoneyView) view.findViewById(R.id.details_money);
        dateText = (TextView) view.findViewById(R.id.details_date);
        typeText = (TextView) view.findViewById(R.id.details_type);
        locationText = (TextView) view.findViewById(R.id.details_location);
        noteText = (TextView) view.findViewById(R.id.details_note);
        bgView = (ImageView) view.findViewById(R.id.background);
        bgBlank = (RelativeLayout) view.findViewById(R.id.details_blank);

        moneyView.setText(mDetail.getMoney());
        dateText.setText(mDetail.getDate());
        typeText.setText(new SmartType(mContext).getTypeName().get(mDetail.getRecordType()));
        locationText.setText(mDetail.getLocation());
        noteText.setText(mDetail.getNote());

        picUri = new File(mDetail.getPicUri()); // get image
        if (picUri != null) {
            try {
                bgView.setImageBitmap(BitmapFactory.decodeStream(mContext.getContentResolver()
                        .openInputStream(Uri.fromFile(picUri)), null, null));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.w(TAG, "img is not from album,attempt to read from camera");
                bgView.setBackground(Drawable.createFromPath(mDetail.getPicUri()));
            }
        }

        moneyView.setOnClickListener(this);
        dateText.setOnClickListener(this);
        typeText.setOnClickListener(this);
        locationText.setOnClickListener(this);
        noteText.setOnClickListener(this);
        bgBlank.setOnClickListener(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setHasOptionsMenu(true);
        mDetail = getActivity().getIntent().getParcelableExtra("detail");
        // init storage path
        picPath = new File(Environment.getExternalStorageDirectory()
                + File.separator
                + "MaiBen");
        if (!picPath.exists()) {
            picPath.mkdir();
        }
        bmp = null;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof TextView) {
            if (v == moneyView) {
                clickItem = INDEX_MONEY;
            } else if (v == dateText) {
                clickItem = INDEX_DATE;
            } else if (v == typeText) {
                clickItem = INDEX_TYPE;
            } else if (v == locationText) {
                clickItem = INDEX_LOC;
            } else if (v == noteText) {
                clickItem = INDEX_NOTE;
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
                    .setTitle(getResources().getString(R.string.msg_input))
                    .setView(layout)
                    .setPositiveButton(getResources().getString(R.string.ok), this)
                    .setNegativeButton(getResources().getString(R.string.cancel), this).show();
        } else if (v instanceof RelativeLayout) {
            CharSequence[] items = {
                    "相机", "相册"
            };
            new AlertDialog.Builder(mContext)
                    .setTitle("选择图片来源")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == SELECT_PICTURE) {
                                Intent intent = new Intent(
                                        Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, SELECT_PICTURE);
                            } else {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale
                                        .getDefault());
                                picUri = new File(picPath + File.separator
                                        + sdf.format(Calendar.getInstance().getTime())
                                        + "-date.jpg");
                                Uri imageUri = Uri.fromFile(picUri);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(intent, SELECT_CAMERA);
                            }
                        }
                    }).show();
        }

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_POSITIVE) {
            switch (clickItem) {
                case 1:
                    moneyView.setText(inputText.getText());
                    break;
                case 2:
                    dateText.setText(inputText.getText());
                    break;
                case 3:
                    typeText.setText(inputText.getText());
                    break;
                case 4:
                    locationText.setText(inputText.getText());
                    break;
                case 5:
                    noteText.setText(inputText.getText());
                    break;
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
                picUri = new File(data.getData()+""); // TODO need fix
                bgView.setImageBitmap(bmp);
            } else if (requestCode == SELECT_CAMERA) {
                if (bmp != null)
                    bmp.recycle();
                Bitmap bitmap = BitmapFactory.decodeFile(picUri.getPath());
                bmp = Bitmap.createScaledBitmap(bitmap, metrics.widthPixels, metrics.heightPixels,
                        true);
                bgView.setImageBitmap(bmp);
            }
        } else {
            Toast.makeText(mContext, "请重新选择图片", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (bmp != null) {
                    DbHelper helper = new DbHelper(mContext, DbHelper.DB_NAME, null,
                            DbHelper.version);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put("picUri", picUri.getPath());
                    db.update("detail", cv, "id=?", new String[] {
                            String.valueOf(mDetail.getId())
                    });
                }
                getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
