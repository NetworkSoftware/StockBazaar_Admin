package customer.smart.support.shop;

public interface OnShopClick {

    void onDeleteClick(Shop position);

    void onEditClick(Shop position);

    void onCallClick(String phone);
    void onStatusClick(Shop position);

}
