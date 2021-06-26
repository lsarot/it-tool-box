package com.sample.mybatis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sample.mybatis.model.persistence.entity.Order;
import com.sample.mybatis.model.persistence.mappers_mybatis.OrderMapper;

import java.util.List;

@Service
public class OrderService implements IOrderService {


    @Autowired
    private OrderMapper orderMapper;


    @Override
    public void addOrder(final Order order) {
        orderMapper.insert(order);
    }

    @Override
    public Order getByOrderId(int orderId) {
        return orderMapper.getById(orderId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderMapper.getAll();
    }

    @Override
    public void updateOrder(Order order) {
        orderMapper.update(order);
    }

    @Override
    public void deleteOrder(int orderId) {
        orderMapper.deleteById(orderId);
    }

}
