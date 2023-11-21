package customer.smart.support.retail.order;

public interface OnOrderClick {
    void onCallClick(OrderModel mOrder);

    void onWhatsappClick(OrderModel mOrder);

    void onStatusClick(OrderModel mOrder);
}
