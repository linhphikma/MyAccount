
package com.xifan.myaccount;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class RecordDetail extends SwipeBackActivity implements OnClickListener {

    private Context mContext;
    private Bitmap mBitmap;
    private DisplayMetrics metrics;

    private ActionBar mActionBar;
    private ImageView mBackground;

    private static final int RESULT_CROP_IMAGES = 1;

    public static final int REQUEST_ADD_RECORD = 1;
    public static final int REQUEST_SHOW_RECORD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);

        mContext = this;
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

        // metrics = new DisplayMetrics();
        // getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //
        // RelativeLayout blank = (RelativeLayout)
        // findViewById(R.id.details_blank);
        // mBackground = (ImageView) findViewById(R.id.background);
        // // background.setBackground(new BitmapDrawable(mBitmap)); TODO 读取
        // blank.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.record_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                // TODO 保存数据到数据库
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof TextView) {
            Toast.makeText(mContext, "you have clicked the text", Toast.LENGTH_SHORT).show();
            // TODO 修改数据
        } else {
            Toast.makeText(mContext, "you have clicked the background", Toast.LENGTH_SHORT).show();
            // TODO background is ready to go
            setPicture(null);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        if (requestCode == RESULT_CROP_IMAGES && resultCode == RESULT_OK) {
            if (data.getParcelableExtra("data") != null) {
                mBitmap = data.getParcelableExtra("data");
                mBackground.setImageDrawable(new BitmapDrawable(getResources(), mBitmap));
                mActionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
            }
        }
    }

    private void setPicture(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setData(uri);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", metrics.widthPixels);
        intent.putExtra("aspectY", metrics.heightPixels / metrics.widthPixels);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_CROP_IMAGES);
    }

}
