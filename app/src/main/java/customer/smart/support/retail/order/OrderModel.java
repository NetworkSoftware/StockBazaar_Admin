package customer.smart.support.retail.order;

import java.io.Serializable;
import java.util.ArrayList;

import customer.smart.support.retail.stock.StockModel;

public class OrderModel implements Serializable {
    String id;
    String name;
    String phone;
    String totalAmt;
    String items;
    String status;
    String createdOn;
    ArrayList<CartItemModel> productBeans;


    public OrderModel() {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(String totalAmt) {
        this.totalAmt = totalAmt;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public ArrayList<CartItemModel> getProductBeans() {
        return productBeans;
    }

    public void setProductBeans(ArrayList<CartItemModel> productBeans) {
        this.productBeans = productBeans;
    }
}