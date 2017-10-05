package com.moonearly.model;

/**
 * Created by Zun on 2016/12/16.
 */

public class GoodsModel extends BusinessBean {

    private String index;
    private String name;
    private String price;
    private String qty;
    private String total;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "GoodsModel{" +
                "index=" + index +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", qty=" + qty +
                ", total=" + total +
                '}';
    }
}
