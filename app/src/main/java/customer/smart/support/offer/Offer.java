package customer.smart.support.offer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user_1 on 12-07-2018.
 */

public class Offer implements Serializable {
    public String id;
    public String name;
    public String image;
    public String startDate;
    public String endDate;
    public String category;
    public String minQuantity;
    public String maxQuantity;
    ArrayList<String> image1;
public String price,productId;

    public Offer() {
    }

    public Offer(String name, String image, String startDate, String endDate,String category,String minQuantity,String maxQuantity) {
        this.name = name;
        this.image = image;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category=category;
        this.minQuantity=minQuantity;
        this.maxQuantity=maxQuantity;

    }

    public ArrayList<String> getImage1() {
        return image1;
    }

    public void setImage1(ArrayList<String> image1) {
        this.image1 = image1;
    }

    public Offer(String id, String name, String image, String startDate, String endDate,String category,String minQuantity,String maxQuantity) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category=category;
        this.minQuantity=minQuantity;
        this.maxQuantity=maxQuantity;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(String minQuantity) {
        this.minQuantity = minQuantity;
    }

    public String getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(String maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
