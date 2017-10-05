package com.moonearly.model;

import java.util.List;

/**
 * Created by Alex on 16/12/17.
 */

public class OrderModel extends BusinessBean {
    private String restaurantName;
    private String subTotal;
    private String discount;
    private String taxes;
    private String grandTotal;
    private GoodsModel goodsModel;
    private List<GoodsModel> goodsModelList;

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getTaxes() {
        return taxes;
    }

    public void setTaxes(String taxes) {
        this.taxes = taxes;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public GoodsModel getGoodsModel() {
        return goodsModel;
    }

    public void setGoodsModel(GoodsModel goodsModel) {
        this.goodsModel = goodsModel;
    }

    public List<GoodsModel> getGoodsModelList() {
        return goodsModelList;
    }

    public void setGoodsModelList(List<GoodsModel> goodsModelList) {
        this.goodsModelList = goodsModelList;
    }

    @Override
    public String toString() {
        return "OrderModel{" +
                "restaurantName='" + restaurantName + '\'' +
                ", subTotal='" + subTotal + '\'' +
                ", discount='" + discount + '\'' +
                ", taxes='" + taxes + '\'' +
                ", grandTotal='" + grandTotal + '\'' +
                ", goodsModel=" + goodsModel +
                ", goodsModelList=" + goodsModelList +
                '}';
    }
}
