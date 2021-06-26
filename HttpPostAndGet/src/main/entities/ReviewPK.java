
package main.entities;

import java.io.Serializable;

public class ReviewPK implements Serializable {

    private int idAccountMaker;

    private int idAccountPub;

    private long timestampPub;

    public ReviewPK() {
    }

    public ReviewPK(int idAccountMaker, int idAccountPub, long timestampPub) {
        this.idAccountMaker = idAccountMaker;
        this.idAccountPub = idAccountPub;
        this.timestampPub = timestampPub;
    }

    public int getIdAccountMaker() {
        return idAccountMaker;
    }

    public void setIdAccountMaker(int idAccountMaker) {
        this.idAccountMaker = idAccountMaker;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idAccountMaker;
        hash += (int) idAccountPub;
        hash += (int) timestampPub;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ReviewPK)) {
            return false;
        }
        ReviewPK other = (ReviewPK) object;
        if (this.idAccountMaker != other.idAccountMaker) {
            return false;
        }
        if (this.idAccountPub != other.idAccountPub) {
            return false;
        }
        if (this.timestampPub != other.timestampPub) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pruebitas.ReviewPK[ idAccountMaker=" + idAccountMaker + ", idAccountPub=" + idAccountPub + ", timestampPub=" + timestampPub + " ]";
    }
    
}
