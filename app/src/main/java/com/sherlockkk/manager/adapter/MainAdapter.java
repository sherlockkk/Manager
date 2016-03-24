package com.sherlockkk.manager.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sherlockkk.manager.R;
import com.sherlockkk.manager.Util;
import com.sherlockkk.manager.dialog.ReduceDialogFragment;
import com.sherlockkk.manager.model.Goods;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author SongJian
 * @created 16/3/23
 * @e-mail 1129574214@qq.com
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.GoodsViewHolder> {
    private static final String TAG = "MainAdapter";

    private Context context;
    private List<Goods> goodsList = new ArrayList<>();
    private LayoutInflater inflater;
    ReduceOnClickListener reduceOnClickListener;
    OnLongClickListener onLongClickListener;

    public MainAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void addList(List<Goods> goodsList) {
        this.goodsList = goodsList;
        notifyDataSetChanged();
    }

    @Override
    public GoodsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_recyclerview, null, false);
        GoodsViewHolder goodsViewHolder = new GoodsViewHolder(view);
        goodsViewHolder.setIsRecyclable(true);
        return goodsViewHolder;
    }

    int D_S;

    @Override
    public void onBindViewHolder(GoodsViewHolder holder, final int position) {

        final Goods goods = goodsList.get(position);

        final String objectId = goods.getObjectId();
        Log.i(TAG, "onBindViewHolder: goodsList:" + goodsList);

        Log.i(TAG, "onBindViewHolder: goods:" + goods);


        if (onLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onLongClickListener.OnLongClick(goods);
                    return true;
                }
            });
        }

        holder.tv_name.setText(goods.getString("name"));
        holder.tv_timein.setText("入库时间：" + goods.getString("timeIn"));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            D_S = Util.daysBetween(simpleDateFormat.parse(goods.getString("productTime")), simpleDateFormat.parse(getCurDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int num = Integer.parseInt(goods.getString("shelfLife")) - D_S;
        if (num < 0) {
            holder.tv_beleft.setText("该商品已过期" + (-num) + "天");
        } else if (num == 0) {
            holder.tv_beleft.setText("该商品今天过期！！！");
        } else {
            holder.tv_beleft.setText("该商品距离过期还有" + num + "天");
        }

        final int stock = goods.getInt("stock");
        holder.tv_stock.setText("现有库存量：" + stock);

        holder.btn_reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int type = 1;
                reduceOnClickListener.OnReduceClick(type, objectId, stock);
            }
        });
        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int type = 2;
                reduceOnClickListener.OnAddClick(type, objectId, stock);
            }
        });
    }

    @Override
    public int getItemCount() {

        return goodsList.size();
    }

    public interface OnLongClickListener {
        void OnLongClick(Goods goods);
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public interface ReduceOnClickListener {
//        void OnReduceClick(List<Goods> goodsList, int position);

        void OnReduceClick(int type, String objectId, int stock);

        void OnAddClick(int type, String objectId, int stock);
    }

    public void setOnReduceOnClickListener(ReduceOnClickListener reduceOnClickListener) {
        this.reduceOnClickListener = reduceOnClickListener;
    }

    public String getCurDate() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String da = dateFormat.format(date);
        return da;
    }

    class GoodsViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_timein, tv_beleft, tv_stock;
        private Button btn_reduce, btn_add;

        public GoodsViewHolder(View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_timein = (TextView) itemView.findViewById(R.id.tv_timein);
            tv_beleft = (TextView) itemView.findViewById(R.id.tv_beleft);
            tv_stock = (TextView) itemView.findViewById(R.id.tv_stock);
            btn_reduce = (Button) itemView.findViewById(R.id.btn_reduce);
            btn_add = (Button) itemView.findViewById(R.id.btn_add);
        }
    }

}
