package com.sample.mybatis.service;

import java.util.List;

import com.sample.mybatis.model.persistence.entity.Order;

public interface IOrderService {

    Order getByOrderId(final int orderId);
    List<Order> getAllOrders();
    void addOrder(final Order order);
    void updateOrder(final Order order);
    void deleteOrder(final int orderId);

}
