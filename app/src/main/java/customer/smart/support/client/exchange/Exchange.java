package customer.smart.support.client.exchange;

import java.io.Serializable;


public class Exchange implements Serializable {
    String id;
    String model;
    String brand;
    String ram;
    String rom;
    String warranty;
    String box;
    String accessories;
    String mobileCondition;
    String hardwareProblem;
    String softwareChanged;
    String image;
    String price;
    String whatsapp;


    public Exchange() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getRom() {
        return rom;
    }

    public void setRom(String rom) {
        this.rom = rom;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public String getBox() {
        return box;
    }

    public void setBox(String box) {
        this.box = box;
    }

    public String getAccessories() {
        return accessories;
    }

    public void setAccessories(String accessories) {
        this.accessories = accessories;
    }

    public String getMobileCondition() {
        return mobileCondition;
    }

    public void setMobileCondition(String mobileCondition) {
        this.mobileCondition = mobileCondition;
    }

    public String getHardwareProblem() {
        return hardwareProblem;
    }

    public void setHardwareProblem(String hardwareProblem) {
        this.hardwareProblem = hardwareProblem;
    }

    public String getSoftwareChanged() {
        return softwareChanged;
    }

    public void setSoftwareChanged(String softwareChanged) {
        this.softwareChanged = softwareChanged;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }
}