package controller;

import java.util.List;
import entitymodel.Courier;
import model.CourierModel;

public class CourierHandler {
    private CourierModel courierModel = new CourierModel();
    
    public Courier getCourier(String idCourier) {
        return courierModel.getCourierById(idCourier);
    }
    
    public List<Courier> getAllCouriers() {
        return courierModel.getAllCouriers();
    }
}