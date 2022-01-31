package customer.smart.support.client.shop;

public interface ShopClick {

    void onDeleteClick(int position);
    void onItemClick(Shop shop);
    void onStockAdd(int position);
    void onCategoryAdd(int position);
}
