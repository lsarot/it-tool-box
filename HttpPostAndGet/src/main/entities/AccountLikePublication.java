package main.entities;


import java.io.Serializable;

public class AccountLikePublication implements Serializable {

    private static final long serialVersionUID = 1L;

    protected AccountLikePublicationPK accountLikePublicationPK;

    
    private long timestampLiked;

    public AccountLikePublication() {
    }

    public AccountLikePublication(AccountLikePublicationPK accountLikePublicationPK, long timestampLiked) {
        this.accountLikePublicationPK = accountLikePublicationPK;
        this.timestampLiked = timestampLiked;
    }

    public AccountLikePublicationPK getAccountLikePublicationPK() {
        return accountLikePublicationPK;
    }

    public void setAccountLikePublicationPK(AccountLikePublicationPK accountLikePublicationPK) {
        this.accountLikePublicationPK = accountLikePublicationPK;
    }

    public long getTimestampLiked() {
        return timestampLiked;
    }

    public void setTimestampLiked(long timestampLiked) {
        this.timestampLiked = timestampLiked;
    }


}
