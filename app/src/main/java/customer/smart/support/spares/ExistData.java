package customer.smart.support.spares;

import java.io.Serializable;
import java.util.ArrayList;

public class ExistData implements Serializable {
    ArrayList<String> data;

    public ExistData(ArrayList<String> data) {
        this.data = data;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }
}
