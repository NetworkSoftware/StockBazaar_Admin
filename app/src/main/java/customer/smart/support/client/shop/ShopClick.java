package customer.smart.support.client.shop;

public interface ShopClick {

    void onDeleteClick(int position);
    void onItemClick(int position);
    void onStockAdd(int position);
    void onCategoryAdd(int position);
}
