package customer.smart.support.client.seller_as;

import java.io.Serializable;

public class Seller implements Serializable {
    String id;
    String shopname;
    String mobile;
    String district;
    String pincode;
    String shopInside;
    String shopOutside;
    boolean gst;
    String status;


    public Seller() {
    }

    public Seller(String shopname, String mobile, String district, String pincode,
                  String shopInside, String shopOutside,boolean gst,String status) {
        this.shopname = shopname;
        this.mobile = mobile;
        this.district = district;
        this.pincode = pincode;
        this.shopInside = shopInside;
        this.shopOutside = shopOutside;
        this.gst = gst;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getShopInside() {
        return shopInside;
    }

    public void setShopInside(String shopInside) {
        this.shopInside = shopInside;
    }

    public String getShopOutside() {
        return shopOutside;
    }

    public void setShopOutside(String shopOutside) {
        this.shopOutside = shopOutside;
    }

    public boolean isGst() {
        return gst;
    }

    public void setGst(boolean gst) {
        this.gst = gst;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}