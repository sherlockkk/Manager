package com.sherlockkk.manager.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

/**
 * @author SongJian
 * @created 16/3/23
 * @e-mail 1129574214@qq.com
 */

@AVClassName("Goods")
public class Goods extends AVObject {
    public static final Creator CREATOR = AVObjectCreator.instance;

    private String name;
    private String timeIn;
    private String timeOut;
    private String productTime;//生产日期
    private String shelfLife;//保质期
    private int residue;//剩余库存
    private int stock;//库存

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

       public String getTimeIn() {
        return getString("timeIn");
    }

    public void setTimeIn(String timeIn) {
        put("timeIn", timeIn);
    }

    public String getProductTime() {
        return getString("productTime");
    }

    public void setProductTime(String productTime) {
        put("productTime", productTime);
    }


    public String getTimeOut() {
        return getString("timeOut");
    }

    public void setTimeOut(String timeOut) {
        put("timeOut", timeOut);
    }

    public String getShelfLife() {
        return getString("shelfLife");
    }

    public void setShelfLife(String shelfLife) {
        put("shelfLife", shelfLife);
    }

    public int getResidue() {
        return getInt("residue");
    }

    public void setResidue(int residue) {
        put("residue", residue);
    }

    public int getStock() {
        return getInt("stock");
    }

    public void setStock(int stock) {
        put("stock", stock);
    }

    @Override
    public String toString() {
        return "Goods{" +
                "name='" + name + '\'' +
                ", timeIn='" + timeIn + '\'' +
                ", timeOut='" + timeOut + '\'' +
                ", shelfLife='" + shelfLife + '\'' +
                ", residue=" + residue +
                ", stock=" + stock +
                '}';
    }
}
