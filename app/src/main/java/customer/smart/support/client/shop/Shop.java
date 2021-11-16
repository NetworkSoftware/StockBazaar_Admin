package customer.smart.support.client.shop;

import java.io.Serializable;
import java.util.ArrayList;


public class Shop implements Serializable {
    String id;
    String shop_name;
    String phone;
    String stock_update;
    String image;
    String category;
    String password;

    public Shop() {
    }

    public Shop(String shop_name, String phone, String stock_update, String image,String category,
    String password) {
        this.shop_name = shop_name;
        this.phone = phone;
        this.stock_update = stock_update;
        this.image = image;
        this.category = category;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStock_update() {
        return stock_update;
    }

    public void setStock_update(String stock_update) {
        this.stock_update = stock_update;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}