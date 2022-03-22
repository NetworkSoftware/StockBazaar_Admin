package customer.smart.support.client.seller_as;


public interface SellerClick {
    void onDetailClick(Seller seller);
    void onDeleteClick(int position);
    void onImageClick(int position);
    void onStatus(Seller seller,String status);
    void onCallClick(String call);
}
