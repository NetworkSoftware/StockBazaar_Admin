package customer.smart.support.client.price;

import java.io.Serializable;

public class PercentagePriceBeen implements Serializable {

    String id;
    String price_percentage;
    String priceRange;
    String priceThree;
    String priceFive;

    public PercentagePriceBeen() {
    }

    public PercentagePriceBeen(String price_percentage, String priceRange) {
        this.price_percentage = price_percentage;
        this.priceRange = priceRange;

    }

    public PercentagePriceBeen(String price_percentage, String priceRange, String priceThree, String priceFive) {
        this.price_percentage = price_percentage;
        this.priceRange = priceRange;
        this.priceThree = priceThree;
        this.priceFive = priceFive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPriceThree() {
        return priceThree;
    }

    public void setPriceThree(String priceThree) {
        this.priceThree = priceThree;
    }

    public String getPriceFive() {
        return priceFive;
    }

    public void setPriceFive(String priceFive) {
        this.priceFive = priceFive;
    }
}
