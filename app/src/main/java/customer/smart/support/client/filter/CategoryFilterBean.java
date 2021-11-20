package customer.smart.support.client.filter;

public class CategoryFilterBean {
    String category;
    String id;

    public CategoryFilterBean(String category, String id) {
        this.category = category;
        this.id = id;
    }

    public CategoryFilterBean(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
