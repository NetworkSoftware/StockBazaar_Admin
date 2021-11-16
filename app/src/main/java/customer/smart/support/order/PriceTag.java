package customer.smart.support.order;

public class PriceTag {
    boolean isSelected;
    String tag;
    String price;


    public PriceTag(boolean isSelected, String tag, String price) {
        this.isSelected = isSelected;
        this.tag = tag;
        this.price = price;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
