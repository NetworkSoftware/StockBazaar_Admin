package customer.smart.support.retail.stock;

import java.io.Serializable;

public class StockModel implements Serializable {
    String id;
    String folderId;
    String categoryId;
    String name;
    String price;
    String image;
    String createdOn;
    String cartVal;


    public StockModel() {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}