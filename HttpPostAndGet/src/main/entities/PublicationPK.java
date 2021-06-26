
package main.entities;

import java.io.Serializable;

public class PublicationPK implements Serializable {

    private static final long serialVersionUID = 1L;
	
    private int idAccount;

    private long timestamp;

    public PublicationPK() {
    }

    public PublicationPK(int idAccount, long timestamp) {
        this.idAccount = idAccount;
        this.timestamp = timestamp;
    }

    public int getIdAccount() {
        return idAccount;
    }

    public void setIdAccount(int idAccount) {
        this.idAccount = idAccount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idAccount;
        hash += (int) timestamp;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PublicationPK)) {
            return false;
        }
        PublicationPK other = (PublicationPK) object;
        if (this.idAccount != other.idAccount) {
            return false;
        }
        if (this.timestamp != other.timestamp) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pulitsoft.coin.coinserver.model.data.entity.PublicationPK[ idAccount=" + idAccount + ", timestamp=" + timestamp + " ]";
    }
    
}
