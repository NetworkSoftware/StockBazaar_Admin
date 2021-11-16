package customer.smart.support.shop;

import java.io.Serializable;

/**
 * Created by user_1 on 12-07-2018.
 */

public class Shop implements Serializable {
    public String id;
    public String name;
    public String contact;
    public String shopname;
    public String password;
    public String confirmPass;
    public String image;
    public String area;
    public String address;
    public String latlon;
    public String pincode;
    public String businesstype;
    public String type;
    public String statusTxt;

    public Shop() {
    }

    public Shop(String name, String contact, String shopname, String password, String confirmPass, String image, String area, String address, String latlon, String pincode, String businesstype, String type) {
        this.name = name;
        this.contact = contact;
        this.shopname = shopname;
        this.password = password;
        this.confirmPass = confirmPass;
        this.image = image;
        this.area = area;
        this.address = address;
        this.latlon = latlon;
        this.pincode = pincode;
        this.businesstype = businesstype;
        this.type = type;
    }

    public Shop(String id, String name, String contact, String shopname, String password, String confirmPass, String image, String area, String address, String latlon, String pincode, String businesstype, String type) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.shopname = shopname;
        this.password = password;
        this.confirmPass = confirmPass;
        this.image = image;
        this.area = area;
        this.address = address;
        this.latlon = latlon;
        this.pincode = pincode;
        this.businesstype = businesstype;
        this.type = type;

    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPass() {
        return confirmPass;
    }

    public void setConfirmPass(String confirmPass) {
        this.confirmPass = confirmPass;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatlon() {
        return latlon;
    }

    public void setLatlon(String latlon) {
        this.latlon = latlon;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getbusinesstype() {
        return businesstype;
    }

    public void setbusinesstype(String businesstype) {
        this.businesstype = businesstype;
    }

    public String getBusinesstype() {
        return businesstype;
    }

    public void setBusinesstype(String businesstype) {
        this.businesstype = businesstype;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatusTxt() {
        return statusTxt;
    }

    public void setStatusTxt(String statusTxt) {
        this.statusTxt = statusTxt;
    }
}
