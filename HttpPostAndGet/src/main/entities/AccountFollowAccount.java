
package main.entities;

import java.io.Serializable;

public class AccountFollowAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    protected AccountFollowAccountPK accountFollowAccountPK;

    private int status;

    public AccountFollowAccount() {
    }

    public AccountFollowAccount(AccountFollowAccountPK accountFollowAccountPK, int status) {
        this.accountFollowAccountPK = accountFollowAccountPK;
        this.status = status;
    }

    public AccountFollowAccountPK getAccountFollowAccountPK() {
        return accountFollowAccountPK;
    }

    public void setAccountFollowAccountPK(AccountFollowAccountPK accountFollowAccountPK) {
        this.accountFollowAccountPK = accountFollowAccountPK;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
