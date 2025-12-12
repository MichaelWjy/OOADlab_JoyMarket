package controller;

import java.util.List;
import entitymodel.CartItem;
import entitymodel.Customer;
import entitymodel.OrderHeader;
import entitymodel.Product;
import entitymodel.Promo;
import entitymodel.User;
import model.CartItemModel;
import model.OrderModel;
import model.ProductModel;
import model.PromoModel;

public class OrderHandler {
    private CartItemModel cartModel = new CartItemModel();
    private ProductModel productModel = new ProductModel();
    private PromoModel promoModel = new PromoModel();
    private OrderModel orderModel = new OrderModel();

    public String checkout(User user, String promoCode) {
        if (!(user instanceof Customer)) return "Only customers can order";
        Customer customer = (Customer) user;

        List<CartItem> cart = cartModel.getUserCart(customer.getId());
        if (cart.isEmpty()) return "Cart is empty";

        double totalPrice = 0;
        for (CartItem item : cart) {
            Product p = productModel.getProduct(item.getIdProduct());
            if (p.getStock() < item.getCount()) {
                return "Stock changed for product: " + p.getName();
            }
            totalPrice += (p.getPrice() * item.getCount());
        }

        String idPromo = null; 
        if (promoCode != null && !promoCode.isEmpty()) {
            Promo promo = promoModel.getPromoByCode(promoCode);
            if (promo == null) return "Invalid Promo Code";
            
            double discount = totalPrice * (promo.getDiscountPercentage() / 100);
            totalPrice -= discount;
            idPromo = promo.getIdPromo();
        }

        if (customer.getBalance() < totalPrice) {
            return "Insufficient Balance. Total: " + totalPrice;
        }

        boolean success = orderModel.createOrder(customer.getId(), idPromo, totalPrice, cart);
        
        if (success) {
            customer.setBalance(customer.getBalance() - totalPrice);
            cartModel.clearCart(customer.getId());
            return "Success";
        } else {
            return "Transaction Failed";
        }
    }

    public List<OrderHeader> getPendingOrders() {
        return orderModel.getOrdersByStatus("Pending");
    }
    
    public List<OrderHeader> getOrderHistory(String idCustomer) {
        return orderModel.getOrderHistory(idCustomer);
    }
    
    public List<OrderHeader> getAllOrders() {
        return orderModel.getAllOrders();
    }
}