package customer.smart.support.order;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by ravi on 16/11/17.
 */

public class Order implements Serializable {

    String quantity, id, amount, status, items, reason,
            payment, grandCost, shipCost, address;
    String paymentId;
    String comments;
    String createdOn;
    String name;
    String phone;
    String pincode;
    String specialCost;
    String user;
    String cashback;
    String wallet;
    String tokenValue;

    ArrayList<CartItems> productBeans;

    public Order() {
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getGrandCost() {
        return grandCost;
    }

    public void setGrandCost(String grandCost) {
        this.grandCost = grandCost;
    }

    public String getShipCost() {
        return shipCost;
    }

    public void setShipCost(String shipCost) {
        this.shipCost = shipCost;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
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

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public ArrayList<CartItems> getProductBeans() {
        return productBeans;
    }

    public void setProductBeans(ArrayList<CartItems> productBeans) {
        this.productBeans = productBeans;
    }

    public String getSpecialCost() {
        return specialCost;
    }

    public void setSpecialCost(String specialCost) {
        this.specialCost = specialCost;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCashback() {
        return cashback;
    }

    public void setCashback(String cashback) {
        this.cashback = cashback;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }
}