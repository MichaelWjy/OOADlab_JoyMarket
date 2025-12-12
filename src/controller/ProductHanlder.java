package controller;

import java.util.List;
import entitymodel.Product;
import model.ProductModel;

public class ProductHanlder {
    private ProductModel productModel = new ProductModel();

    public List<Product> getAllProducts() {
        return productModel.getAllProducts();
    }

    public Product getProduct(String idProduct) {
        return productModel.getProduct(idProduct);
    }

    public String updateStock(String idProduct, int newStock) {
        if (newStock < 0) return "Stock cannot be negative";
        boolean success = productModel.updateProductStock(idProduct, newStock);
        return success ? "Success" : "Database Error";
    }
}