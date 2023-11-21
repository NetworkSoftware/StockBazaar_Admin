package customer.smart.support.retail.stock;

import customer.smart.support.retail.category.CategoryModel;

public interface OnStockClick {
    void onDeleteClick(StockModel folderModel);
    void onEditClick(StockModel folderModel);
}
