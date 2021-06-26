
package main.entities;

import java.io.Serializable;
import java.util.List;

public class Publication implements Serializable {

    private static final long serialVersionUID = 1L;

    protected PublicationPK publicationPK;

    private Short pages;

    private Integer likes;

    private Short comments;

    private Short reviews;

    private Integer acumCalifications;

    private String startDate;

    private String endDate;

    private String description;

    private String tags;

    private String place;

    private Account account;
   
    private List<Page> pageList;


    public Publication() {
    }

    public Publication(PublicationPK publicationPK) {
        this.publicationPK = publicationPK;
    }

    public Publication(int idAccount, long timestamp) {
        this.publicationPK = new PublicationPK(idAccount, timestamp);
    }

    public PublicationPK getPublicationPK() {
        return publicationPK;
    }

    public void setPublicationPK(PublicationPK publicationPK) {
        this.publicationPK = publicationPK;
    }

    public Short getPages() {
        return pages;
    }

    public void setPages(Short pages) {
        this.pages = pages;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Short getComments() {
        return comments;
    }

    public void setComments(Short comments) {
        this.comments = comments;
    }

    public Short getReviews() { return reviews; }

    public void setReviews(Short reviews) { this.reviews = reviews; }

    public Integer getAcumCalifications() { return acumCalifications; }

    public void setAcumCalifications(Integer acumCalifications) { this.acumCalifications = acumCalifications; }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() { return tags; }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }


    public List<Page> getPageList() {
        return pageList;
    }

    public void setPageList(List<Page> pageList) {
        this.pageList = pageList;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (publicationPK != null ? publicationPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Publication)) {
            return false;
        }
        Publication other = (Publication) object;
        if ((this.publicationPK == null && other.publicationPK != null) || (this.publicationPK != null && !this.publicationPK.equals(other.publicationPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pulitsoft.coin.coinserver.model.data.entity.Publication[ publicationPK=" + publicationPK + " ]";
    }
    
}
