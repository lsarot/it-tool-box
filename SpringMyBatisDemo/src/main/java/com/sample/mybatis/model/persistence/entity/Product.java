package com.sample.mybatis.model.persistence.entity;

import java.io.Serializable;

public class Product implements Serializable {

    private int id;
    private String name;
    private short package_qty;
    private double unit_price;
    private int on_stock_qty;
    private int on_orders_qty;
    private int to_reorder_qty;
    private short discontinued;
    private String category_name;
    private int provider_id;

    private Provider provider;
    private Category category;

    public Product() {}

    public Product(String name, short package_qty, double unit_price, int on_stock_qty, int on_orders_qty, int to_reorder_qty, short discontinued, String category_name, int provider_id) {
        this.name = name;
        this.package_qty = package_qty;
        this.unit_price = unit_price;
        this.on_stock_qty = on_stock_qty;
        this.on_orders_qty = on_orders_qty;
        this.to_reorder_qty = to_reorder_qty;
        this.discontinued = discontinued;
        this.category_name = category_name;
        this.provider_id = provider_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getPackage_qty() {
        return package_qty;
    }

    public void setPackage_qty(short package_qty) {
        this.package_qty = package_qty;
    }

    public double getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(double unit_price) {
        this.unit_price = unit_price;
    }

    public int getOn_stock_qty() {
        return on_stock_qty;
    }

    public void setOn_stock_qty(int on_stock_qty) {
        this.on_stock_qty = on_stock_qty;
    }

    public int getOn_orders_qty() {
        return on_orders_qty;
    }

    public void setOn_orders_qty(int on_orders_qty) {
        this.on_orders_qty = on_orders_qty;
    }

    public int getTo_reorder_qty() {
        return to_reorder_qty;
    }

    public void setTo_reorder_qty(int to_reorder_qty) {
        this.to_reorder_qty = to_reorder_qty;
    }

    public short getDiscontinued() {
        return discontinued;
    }

    public void setDiscontinued(short discontinued) {
        this.discontinued = discontinued;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public int getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(int provider_id) {
        this.provider_id = provider_id;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
