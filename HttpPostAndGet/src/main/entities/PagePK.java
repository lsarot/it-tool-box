
package main.entities;

import java.io.Serializable;

public class PagePK implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private int idAccount;

    private long timestampPub;

    private short number;

    public PagePK() {
    }

    public PagePK(int idAccount, long timestampPub, short number) {
        this.idAccount = idAccount;
        this.timestampPub = timestampPub;
        this.number = number;
    }

    public int getIdAccount() {
        return idAccount;
    }

    public void setIdAccount(int idAccount) {
        this.idAccount = idAccount;
    }

    public long getTimestampPub() {
        return timestampPub;
    }

    public void setTimestampPub(long timestampPub) {
        this.timestampPub = timestampPub;
    }

    public short getNumber() {
        return number;
    }

    public void setNumber(short number) {
        this.number = number;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idAccount;
        hash += (int) timestampPub;
        hash += (int) number;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PagePK)) {
            return false;
        }
        PagePK other = (PagePK) object;
        if (this.idAccount != other.idAccount) {
            return false;
        }
        if (this.timestampPub != other.timestampPub) {
            return false;
        }
        if (this.number != other.number) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pulitsoft.coin.coinserver.model.data.entity.PagePK[ idAccount=" + idAccount + ", timestampPub=" + timestampPub + ", number=" + number + " ]";
    }
    
}
