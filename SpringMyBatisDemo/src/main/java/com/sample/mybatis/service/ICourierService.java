package com.sample.mybatis.service;

import java.util.List;

import com.sample.mybatis.model.persistence.entity.Contact;
import com.sample.mybatis.model.persistence.entity.Courier;

public interface ICourierService {

    Courier getByCourierId(final int courierId);
    List<Courier> getAllCouriers();
    void addCourier(final Courier courier);
    void updateCourier(final Courier courier);
    void deleteCourier(final int courierId);

}
