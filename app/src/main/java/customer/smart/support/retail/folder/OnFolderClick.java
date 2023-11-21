package customer.smart.support.retail.folder;

public interface OnFolderClick {
    void onDeleteClick(FolderModel folderModel);
    void onEditClick(FolderModel folderModel);
    void onViewClick(FolderModel folderModel);
    void onMoveClick(FolderModel folderModel);
    void onShareClick(FolderModel folderModel);
}
