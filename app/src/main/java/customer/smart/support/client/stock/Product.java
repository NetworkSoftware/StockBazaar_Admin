package customer.smart.support.client.stock;

import java.io.Serializable;

public class Product implements Serializable {
    String id;
    String brand;
    String model;
    String price;
    String description;
    String selectshop;
    String shopName;
    String selectcategories;
    String stock_update;
    String image;
    String qty;
    String categoryName;
    String bulkPrice;
    String category;
    String  categoryTag;
    String incategory;


    public Product() {
    }

    public Product(String brand, String model, String price, String description,
                   String selectshop,String stock_update, String image) {
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.description = description;
        this.selectshop = selectshop;
        this.stock_update = stock_update;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSelectshop() {
        return selectshop;
    }

    public void setSelectshop(String selectshop) {
        this.selectshop = selectshop;
    }

    public String getSelectcategories() {
        return selectcategories;
    }

    public void setSelectcategories(String selectcategories) {
        this.selectcategories = selectcategories;
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

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getBulkPrice() {
        return bulkPrice;
    }

    public void setBulkPrice(String bulkPrice) {
        this.bulkPrice = bulkPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryTag() {
        return categoryTag;
    }

    public void setCategoryTag(String categoryTag) {
        this.categoryTag = categoryTag;
    }

    public String getIncategory() {
        return incategory;
    }

    public void setIncategory(String incategory) {
        this.incategory = incategory;
    }
}