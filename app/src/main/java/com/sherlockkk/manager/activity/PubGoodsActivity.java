package com.sherlockkk.manager.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sherlockkk.manager.R;
import com.sherlockkk.manager.model.Goods;
import com.sherlockkk.manager.ui.WheelView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * @author SongJian
 * @created 16/3/23
 * @e-mail 1129574214@qq.com
 */
public class PubGoodsActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    private static final String TAG = "PubGoodsActivity";

    public static final String DATEPICKER_TAG = "datepicker";

    private EditText et_name, et_shelflife, et_stock;
    private Button btn_save;
    private TextView tv_product_date, tv_shelflife;
    private LinearLayout ll_shelflife;

    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pubgoods);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("添加商品");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final Calendar calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), true);
        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }
        }
        et_name = (EditText) findViewById(R.id.et_name);
        ll_shelflife = (LinearLayout) findViewById(R.id.ll_shelflife);
        ll_shelflife.setOnClickListener(this);
        tv_shelflife = (TextView) findViewById(R.id.tv_shelflife);
        et_stock = (EditText) findViewById(R.id.et_stock);
        tv_product_date = (TextView) findViewById(R.id.tv_product_date);
        tv_product_date.setOnClickListener(this);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
    }

    String shelflife;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_shelflife:
                View view = LayoutInflater.from(this).inflate(R.layout.layout_select_shelflife, null);
                WheelView wheelView = (WheelView) view.findViewById(R.id.wv_select_shelflife);
                wheelView.setItems(Arrays.asList(getResources().getStringArray(R.array.select_shelflife)));
                wheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                    @Override
                    public void onSelected(int selectedIndex, String item) {
                        tv_shelflife.setText("保质期：" + item);
                        shelflife = item.substring(0, item.length() - 1);
                        Log.i(TAG, "onSelected: shelflife:" + shelflife);
                    }
                });
                new AlertDialog.Builder(this).setTitle("设置保质期").setView(view).setPositiveButton("OK", null).show();
                break;
            case R.id.tv_product_date:
                datePickerDialog.setVibrate(true);
                datePickerDialog.setYearRange(1980, 2028);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
                break;
            case R.id.btn_save:
                String name = et_name.getText().toString().trim();
                String stockString = et_stock.getText().toString();
                String timeIn = getCurTime();

                //判断是否为空
                if (isNotEmpty(name, shelflife, stockString, productDate)) {
                    beginShowDialog();
                    int stock = Integer.parseInt(stockString);
                    Goods goods = new Goods();
                    goods.setName(name);
                    goods.setShelfLife(shelflife);
                    goods.setProductTime(productDate);
                    goods.setTimeIn(timeIn);
                    goods.setStock(stock);

                    goods.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            dialog.dismiss();
                            if (e == null) {
                                Toast.makeText(PubGoodsActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(PubGoodsActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(PubGoodsActivity.this, "提交失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
        }


    }

    private boolean isNotEmpty(String name, String shelflife, String stock, String productDate) {
        if (TextUtils.isEmpty(name)) {
            Snackbar.make(btn_save, "商品名不能为空", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(shelflife)) {
            Snackbar.make(btn_save, "请设置保质期", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(String.valueOf(stock))) {
            Snackbar.make(btn_save, "入库数量不能为空", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(productDate)) {
            Snackbar.make(btn_save, "请选择生产日期", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getCurTime() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String time = dateFormat.format(date);
        return time;
    }

    ProgressDialog dialog;

    private void beginShowDialog() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("提交中。。。");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.show();
    }

    String productDate;

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        int m = month + 1;//月份要加一
        productDate = year + "-" + m + "-" + day;
        tv_product_date.setText("生产日期为：" + productDate);
        Log.i(TAG, "onDateSet: productDate:" + productDate);
    }
}
