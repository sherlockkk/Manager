package com.sherlockkk.manager.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sherlockkk.manager.R;

/**
 * @author SongJian
 * @created 16/3/23
 * @e-mail 1129574214@qq.com
 */
public class ReduceDialogFragment extends AppCompatDialogFragment {
    private Context context;
    private EditText et_quantity;
    private String objectId;
    private int stock;
    private int type;

    public ReduceDialogFragment() {
    }

    public ReduceDialogFragment(Context context, String objectId, int stock, int type) {
        this.context = context;
        this.objectId = objectId;
        this.stock = stock;
        this.type = type;
    }

    public interface OkActionListener {
//        void onOkAction(String quantity, List<Goods> goodsList, int postion);

        void onOkAction(String quantity, String objectId, int stock, int type);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_reduce, null, false);
        et_quantity = (EditText) view.findViewById(R.id.et_reduce);
        builder.setView(view).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String quantity = et_quantity.getText().toString().trim();

                if (isNotEmpty(quantity)) {
                    OkActionListener okActionListener = (OkActionListener) context;
                    okActionListener.onOkAction(quantity, objectId, stock, type);
                }
            }
        }).setNegativeButton("取消", null);
        return builder.create();
    }

    private boolean isNotEmpty(String quantity) {
        if (TextUtils.isEmpty(quantity)) {
            return false;
        }
        return true;
    }
}
