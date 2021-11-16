package customer.smart.support.client.stock;

public interface OnStockClick {

    void onDeleteClick(int position);
    void onImageClick(int position);
    void onEditClick(Product position);
}
