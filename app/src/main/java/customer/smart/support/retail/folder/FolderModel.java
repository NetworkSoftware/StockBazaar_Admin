package customer.smart.support.retail.folder;

import java.io.Serializable;

public class FolderModel implements Serializable {
    String id;
    String name;
    String count;
    String createdOn;


    public FolderModel() {
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public FolderModel(String name, String createdOn) {
        this.name = name;
        this.createdOn = createdOn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}