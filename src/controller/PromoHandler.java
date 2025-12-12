package controller;

import java.util.List;
import entitymodel.Promo;
import model.PromoModel;

public class PromoHandler {
    private PromoModel promoModel = new PromoModel();

    public Promo getPromoByCode(String code) {
        if (code == null || code.isEmpty()) return null;
        return promoModel.getPromoByCode(code);
    }

    public String createPromo(String code, String headline, double discount) {
        if (code.isEmpty()) return "Promo code cannot be empty";
        if (headline.isEmpty()) return "Headline cannot be empty";
        if (discount <= 0 || discount > 100) return "Discount must be between 1-100%";

        if (promoModel.getPromoByCode(code) != null) return "Promo code already exists";

        boolean success = promoModel.insertPromo(code, headline, discount);
        return success ? "Success" : "Failed to create promo";
    }

    public List<Promo> getAllPromos() {
        return promoModel.getAllPromos();
    }
}