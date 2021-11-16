package customer.smart.support.client.price;

import java.io.Serializable;

public class PercentagePriceBeen implements Serializable {

    String price_percentage;
    String priceRange;

    public PercentagePriceBeen() {
    }

    public PercentagePriceBeen(String price_percentage, String priceRange) {
        this.price_percentage = price_percentage;
        this.priceRange = priceRange;
    }

    public String getPrice_percentage() {
        return price_percentage;
    }

    public void setPrice_percentage(String price_percentage) {
        this.price_percentage = price_percentage;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }
}
