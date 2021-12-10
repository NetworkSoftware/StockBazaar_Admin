package customer.smart.support.client.bulkPrice;

import java.io.Serializable;

public class BulkPriceBeen implements Serializable {

    String  qty_price,quantity;


    public BulkPriceBeen() {
    }

    public BulkPriceBeen(String quantity,String price) {
        this.quantity = quantity;
        this.qty_price =price;
    }
    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getQty_price() {
        return qty_price;
    }

    public void setQty_price(String qty_price) {
        this.qty_price = qty_price;
    }


}
