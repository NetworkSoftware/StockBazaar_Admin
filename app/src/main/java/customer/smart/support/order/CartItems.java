package customer.smart.support.order;

import java.io.Serializable;

public class CartItems implements Serializable {

    public static final String TABLE_NAME = "SbCustomer";
    public static final String COLUMN_PRO_ID = "proid";
    public static final String COLUMN_ID = "id";
    public static final String USER_ID = "userId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_QTY = "quantity";
    public static final String COLUMN_MAX_QTY = "maxquantity";
    public static final String COLUMN_BULKPRICE = "bulkPrice";
    public static final String COLUMN_MIN_QTY = "minQuantity";
    public static final String COLUMN_WISH = "wish";


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_PRO_ID + " proid,"
                    + USER_ID + " userId,"
                    + COLUMN_NAME + " name,"
                    + COLUMN_PRICE + " price,"
                    + COLUMN_IMAGE + " image,"
                    + COLUMN_DESCRIPTION + " description,"
                    + COLUMN_MAX_QTY + " maxquantity,"
                    + COLUMN_QTY + " quantity,"
                    + COLUMN_BULKPRICE + " bulkPrice,"
                    + COLUMN_MIN_QTY + " minQuantity,"
                    + COLUMN_WISH + " wish"
                    + ")";


    public String id;
    public String name;
    public String description;
    public String image;
    public String quantity;
    public String price;
    public String userId;
    public String maxCount;
    public String shopName;
    public boolean isBulk;
    public String bulkPrice;
    public String minQuantity;
    public String wish;

    public CartItems(String id, String name, String description, String image, String quantity, String price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.quantity = quantity;
        this.price = price;
    }

    public CartItems() {

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(String maxCount) {
        this.maxCount = maxCount;
    }

    public boolean isBulk() {
        return isBulk;
    }

    public void setBulk(boolean bulk) {
        isBulk = bulk;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public static String getColumnBulkprice() {
        return COLUMN_BULKPRICE;
    }

    public String getBulkPrice() {
        return bulkPrice;
    }

    public void setBulkPrice(String bulkPrice) {
        this.bulkPrice = bulkPrice;
    }

    public static String getColumnMinQty() {
        return COLUMN_MIN_QTY;
    }

    public static String getColumnWish() {
        return COLUMN_WISH;
    }

    public String getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(String minQuantity) {
        this.minQuantity = minQuantity;
    }

    public String getWish() {
        return wish;
    }

    public void setWish(String wish) {
        this.wish = wish;
    }
}
