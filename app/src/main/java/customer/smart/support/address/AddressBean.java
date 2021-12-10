package customer.smart.support.address;

import java.io.Serializable;


public class AddressBean implements Serializable {
    String id;
    String selleraddress;
    String buyeraddress;
    String idtext;
    String cod;
    String payment;

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public AddressBean() {
    }

    public AddressBean(String selleraddress, String buyeraddress) {
        this.selleraddress = selleraddress;
        this.buyeraddress = buyeraddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSelleraddress() {
        return selleraddress;
    }

    public void setSelleraddress(String selleraddress) {
        this.selleraddress = selleraddress;
    }

    public String getBuyeraddress() {
        return buyeraddress;
    }

    public void setBuyeraddress(String buyeraddress) {
        this.buyeraddress = buyeraddress;
    }

    public String getIdtext() {
        return idtext;
    }

    public void setIdtext(String idtext) {
        this.idtext = idtext;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }
}