package customer.smart.support.order;

public interface StatusListener {

    void onDeliveredClick(String id);
    void onWhatsAppClick(String phone);
    void onCallClick(String phone);
    void onCancelClick(String id);
    void onItemClick(Order order);
    void onWallet(Order order);
}
