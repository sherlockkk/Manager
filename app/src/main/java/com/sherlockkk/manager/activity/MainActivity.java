package com.sherlockkk.manager.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.sherlockkk.manager.R;
import com.sherlockkk.manager.adapter.MainAdapter;
import com.sherlockkk.manager.dialog.ReduceDialogFragment;
import com.sherlockkk.manager.model.Goods;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ReduceDialogFragment.OkActionListener {
    private static final String TAG = "MainActivity";

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSwipeRefreshLayout();
        initRecyclerView();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PubGoodsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_main);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                loadDatas();
            }
        });
    }

    private void loadDatas() {
        AVQuery query = AVQuery.getQuery(Goods.class);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback() {

            @Override
            public void done(List list, AVException e) {
                swipeRefreshLayout.setRefreshing(false);
                if (e == null) {
                    adapter.addList(list);
                }
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.rcv_main);
        adapter = new MainAdapter(this);
        loadDatas();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        discussAdapter();
    }

    ReduceDialogFragment dialogFragment;

    private void discussAdapter() {
        adapter.setOnReduceOnClickListener(new MainAdapter.ReduceOnClickListener() {
            @Override
            public void OnReduceClick(int type, final String objectId, final int stock) {
                Log.i(TAG, "OnReduceClick: stock:" + stock);
                if (stock <= 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setCancelable(false);
                    builder.setTitle("该商品库存已为 0 ，添加商品？");
                    builder.setPositiveButton("现在添加", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialogFragment = new ReduceDialogFragment(MainActivity.this, objectId, stock, 2);
                            dialogFragment.show(getSupportFragmentManager(), "ReduceDialog");
                        }
                    });
                    builder.setNegativeButton("稍后添加", null);
                    builder.create();
                    builder.show();
                } else {
                    dialogFragment = new ReduceDialogFragment(MainActivity.this, objectId, stock, type);
                    dialogFragment.show(getSupportFragmentManager(), "ReduceDialog");
                }
            }

            @Override
            public void OnAddClick(int type, String objectId, int stock) {
                dialogFragment = new ReduceDialogFragment(MainActivity.this, objectId, stock, type);
                dialogFragment.show(getSupportFragmentManager(), "ReduceDialog");
//                updateStock(getQuantity(),objectId,stock);
            }
        });

        adapter.setOnLongClickListener(new MainAdapter.OnLongClickListener() {
            @Override
            public void OnLongClick(final Goods goods) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("删除该商品？？？");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goods.deleteInBackground();
                        loadDatas();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create();
                builder.show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onOkAction(String quantity, String objectId, int stock, int type) {
        try {
            if (type == 1) {
                updateReduceStock(quantity, objectId, stock);
            } else {
                updateAddStock(quantity, objectId, stock);
            }
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

    private void updateAddStock(String quantity, String objectId, int stock) throws AVException {
//        if (quantity == null) {
//            quantity = "0";
//        }
        int stock2 = stock + Integer.parseInt(quantity);
        Goods goods = Goods.createWithoutData(Goods.class, objectId);
        goods.put("stock", stock2);
        goods.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                loadDatas();
                if (e == null) {
                    Log.i(TAG, "done: update success");
                } else {
                    Log.i(TAG, "done: e:" + e.getMessage());
                }
            }
        });
    }


    public void updateReduceStock(String quantity, final String objectId, final int stock) throws AVException {
        Log.i(TAG, "updateStock: quantity:" + quantity);

        int stock1 = stock - Integer.parseInt(quantity);

        if (stock1 < 1) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setCancelable(false);
            builder.setTitle("该商品库存将为 0 ，确认出库？");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Goods goods = null;
                    try {
                        goods = Goods.createWithoutData(Goods.class, objectId);
                        goods.put("stock", 0);
                        goods.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                loadDatas();
                                if (e == null) {
                                    Log.i(TAG, "done: update success");
                                } else {
                                    Log.i(TAG, "done: e:" + e.getMessage());
                                }
                            }
                        });
                    } catch (AVException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            });
            builder.create();
            builder.show();
        } else {
            Goods goods = Goods.createWithoutData(Goods.class, objectId);
            goods.put("stock", stock1);
            goods.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    loadDatas();
                    if (e == null) {
                        Log.i(TAG, "done: update success");
                    } else {
                        Log.i(TAG, "done: e:" + e.getMessage());
                    }
                }
            });
        }
    }
}
