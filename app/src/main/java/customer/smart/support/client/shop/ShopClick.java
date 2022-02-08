package customer.smart.support.client.shop;

public interface ShopClick {

    void onDeleteClick(Shop shop);
    void onItemClick(Shop shop);
    void onStockAdd(Shop shop);
    void onCategoryAdd(int position);
}
