package controller;

import java.util.List;
import entitymodel.CartItem;
import entitymodel.Product;
import model.CartItemModel;
import model.ProductModel;

public class CartItemHandler {
    private CartItemModel cartModel = new CartItemModel();
    private ProductModel productModel = new ProductModel();

    public List<CartItem> getUserCart(String idUser) {
        return cartModel.getUserCart(idUser);
    }

    public String addToCart(String idUser, String idProduct, int qty) {
        Product p = productModel.getProduct(idProduct);
        if (p == null) return "Product not found";
        if (qty < 1) return "Quantity must be at least 1";
        if (qty > p.getStock()) return "Insufficient stock";
        int currentQty = cartModel.checkItemInCart(idUser, idProduct);
        
        if (currentQty != -1) {
            int newQty = currentQty + qty;
            if (newQty > p.getStock()) return "Total quantity exceeds stock";
            cartModel.updateCartQty(idUser, idProduct, newQty);
        } else {
            cartModel.addToCart(idUser, idProduct, qty);
        }
        return "Success";
    }

    public String updateCartItem(String idUser, String idProduct, int newQty) {
        Product p = productModel.getProduct(idProduct);
        if (p == null) return "Product not found";
        if (newQty < 1) return "Quantity must be at least 1";
        if (newQty > p.getStock()) return "Insufficient stock (Stock: " + p.getStock() + ")";

        boolean success = cartModel.updateCartQty(idUser, idProduct, newQty);
        return success ? "Success" : "Item not found in cart";
    }

    public String deleteCartItem(String idUser, String idProduct) {
        boolean success = cartModel.deleteCartItem(idUser, idProduct);
        return success ? "Success" : "Failed to delete";
    }
    
    public void clearCart(String idUser) {
        cartModel.clearCart(idUser);
    }
}