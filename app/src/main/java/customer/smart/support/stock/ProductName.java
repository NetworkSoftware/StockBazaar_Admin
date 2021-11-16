package customer.smart.support.stock;

import java.io.Serializable;
import java.util.List;

public class ProductName implements Serializable {
    public ProductName(List<String> names) {
        this.names = names;
    }

    public List<String> names;

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }
}
