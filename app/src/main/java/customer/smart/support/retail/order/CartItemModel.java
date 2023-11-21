package customer.smart.support.retail.order;

import java.io.Serializable;
import java.util.ArrayList;

public class CartItemModel implements Serializable {
    String id;
    String folderId;
    String categoryId;
    String name;
    String price;
    ArrayList<String> image;
    String createdOn;
    String cartVal;


    public CartItemModel() {
    }

    public String getCartVal() {
        return cartVal;
    }

    public void setCartVal(String cartVal) {
        this.cartVal = cartVal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }
}