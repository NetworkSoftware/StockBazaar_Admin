package customer.smart.support.client.wallet;

import java.io.Serializable;

/**
 * Created by ravi on 16/11/17.
 */

public class AllWalletBean implements Serializable {
    String id;
    String userId;
    String operation;
    String amt;
    String description;
    String createdon;
    String userName;
    String status;

    public AllWalletBean() {
    }

    public AllWalletBean(String userId, String operation, String amt,
                         String description, String createdon, String userName,String status) {
        this.userId = userId;
        this.operation = operation;
        this.amt = amt;
        this.description = description;
        this.createdon = createdon;
        this.userName = userName;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedon() {
        return createdon;
    }

    public void setCreatedon(String createdon) {
        this.createdon = createdon;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}