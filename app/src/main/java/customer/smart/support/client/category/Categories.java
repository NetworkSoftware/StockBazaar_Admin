package customer.smart.support.client.category;

import java.io.Serializable;

/**
 * Created by ravi on 16/11/17.
 */

public class Categories implements Serializable {
    String id;
    String title;
    String image;
    String percentage;
    String brand;
    String tag;

    public Categories() {
    }

    public Categories(String title, String image,String percentage,String brand,String tag){
        this.title = title;
        this.image = image;
        this.percentage = percentage;
        this.brand = brand;
        this.tag = tag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}