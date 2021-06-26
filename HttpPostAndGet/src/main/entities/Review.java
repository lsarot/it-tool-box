
package main.entities;

import java.io.Serializable;

public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    protected ReviewPK reviewPK;

    private long timestampReview;

    private String review;

    private short calification;

    private Account account;


    public Review() {
    }

    public Review(ReviewPK reviewPK) {
        this.reviewPK = reviewPK;
    }

    public Review(ReviewPK reviewPK, long timestampReview, String review, short calification) {
        this.reviewPK = reviewPK;
        this.timestampReview = timestampReview;
        this.review = review;
        this.calification = calification;
    }

    public Review(int idAccountMaker, int idAccountPub, long timestampPub) {
        this.reviewPK = new ReviewPK(idAccountMaker, idAccountPub, timestampPub);
    }

    public ReviewPK getReviewPK() {
        return reviewPK;
    }

    public void setReviewPK(ReviewPK reviewPK) {
        this.reviewPK = reviewPK;
    }

    public long getTimestampReview() {
        return timestampReview;
    }

    public void setTimestampReview(long timestampReview) {
        this.timestampReview = timestampReview;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public short getCalification() {
        return calification;
    }

    public void setCalification(short calification) {
        this.calification = calification;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reviewPK != null ? reviewPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Review)) {
            return false;
        }
        Review other = (Review) object;
        if ((this.reviewPK == null && other.reviewPK != null) || (this.reviewPK != null && !this.reviewPK.equals(other.reviewPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pruebitas.Review[ reviewPK=" + reviewPK + " ]";
    }
    
}
