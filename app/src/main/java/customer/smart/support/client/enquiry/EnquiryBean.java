package customer.smart.support.client.enquiry;

import java.io.Serializable;

public class EnquiryBean implements Serializable {
    String product_name;
    String details;
    String qty;
    String whatsapp_number;
    String district;
    String id;

    public EnquiryBean(){

    }
    public EnquiryBean(String product_name, String details, String qty, String whatsapp_number, String district) {
        this.product_name = product_name;
        this.details = details;
        this.qty = qty;
        this.whatsapp_number = whatsapp_number;
        this.district = district;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getWhatsapp_number() {
        return whatsapp_number;
    }

    public void setWhatsapp_number(String whatsapp_number) {
        this.whatsapp_number = whatsapp_number;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
