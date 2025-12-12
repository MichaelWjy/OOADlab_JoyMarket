package controller;

import java.util.List;
import entitymodel.OrderHeader;
import model.CourierModel;
import model.DeliveryModel;

public class DeliveryHandler {
    private CourierModel courierModel = new CourierModel();
    private DeliveryModel deliveryModel = new DeliveryModel();

    public String assignCourierToOrder(String idOrder, String idCourier) {
        if (idOrder == null || idCourier == null) return "Invalid selection";
        
        if (courierModel.getCourierById(idCourier) == null) return "Courier does not exist";

        boolean success = deliveryModel.assignCourier(Integer.parseInt(idOrder), Integer.parseInt(idCourier));
        return success ? "Success" : "Failed to assign courier";
    }
      
    public List<OrderHeader> getAssignedDeliveries(String idCourier) {
        return deliveryModel.getCourierDeliveries(idCourier);
    }
    
    public String editDeliveryStatus(String idOrder, String idCourier, String newStatus) {
        if (!newStatus.equals("Pending") && !newStatus.equals("In Progress") && !newStatus.equals("Delivered")) {
            return "Invalid status";
        }

        boolean authorized = deliveryModel.checkDeliveryOwnership(Integer.parseInt(idOrder), Integer.parseInt(idCourier));
        if (!authorized) return "Unauthorized";

        boolean success = deliveryModel.updateDeliveryStatus(Integer.parseInt(idOrder), newStatus);
        return success ? "Success" : "Database Error";
    }
    
    public List<OrderHeader> getAllDeliveries() {
        return deliveryModel.getAllDeliveries();
    }
}