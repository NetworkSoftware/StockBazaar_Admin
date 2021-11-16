package customer.smart.support.contact;

import java.io.Serializable;

/**
 * Created by ravi on 16/11/17.
 */

public class Contact implements Serializable {
    String id;
    String name;
    String pincode;
    String address;
    String phone;
    String area;


    public Contact() {
    }

    public Contact(String name, String pincode, String address, String phone, String area) {
        this.name = name;
        this.pincode = pincode;
        this.address = address;
        this.phone = phone;
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

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}