package main.entities;

import java.io.Serializable;

public class AccountLikePublicationPK implements Serializable {

    private static final long serialVersionUID = 1L;

    
    private int idAccountLiker;

    
    private int idAccountPub;

    
    private long timestampPub;

    public AccountLikePublicationPK() {
    }

    public AccountLikePublicationPK( int idAccountLiker, int idAccountPub, long timestampPub) {
        this.idAccountLiker = idAccountLiker;
        this.idAccountPub = idAccountPub;
        this.timestampPub = timestampPub;
    }

    public int getIdAccountLiker() {
        return idAccountLiker;
    }

    public void setIdAccountLiker(int idAccountLiker) {
        this.idAccountLiker = idAccountLiker;
    }

    public int getIdAccountPub() {
        return idAccountPub;
    }

    public void setIdAccountPub(int idAccountPub) {
        this.idAccountPub = idAccountPub;
    }

    public long getTimestampPub() {
        return timestampPub;
    }

    public void setTimestampPub(long timestampPub) {
        this.timestampPub = timestampPub;
    }
}
