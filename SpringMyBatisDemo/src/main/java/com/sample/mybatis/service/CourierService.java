package com.sample.mybatis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sample.mybatis.model.persistence.entity.Courier;
import com.sample.mybatis.model.persistence.mappers_mybatis.CourierMapper;

import java.util.List;

@Service
public class CourierService implements ICourierService {


    @Autowired
    private CourierMapper courierMapper;


    @Override
    public void addCourier(final Courier courier) {
        courierMapper.insert(courier);
    }

    @Override
    public Courier getByCourierId(int courierId) {
        return courierMapper.getById(courierId);
    }

    @Override
    public List<Courier> getAllCouriers() {
        return courierMapper.getAll();
    }

    @Override
    public void updateCourier(Courier courier) {
        courierMapper.update(courier);
    }

    @Override
    public void deleteCourier(int courierId) {
        courierMapper.deleteById(courierId);
    }

}
