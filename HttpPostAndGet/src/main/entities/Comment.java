
package main.entities;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    
    private Long id;

    private long timestampComment;

    private String comment;

    private Account account;

    private Integer idAccountPub;

    private long timestampPub;


    public Comment() {
    }

    public Comment(Long id) {
        this.id = id;
    }

    public Comment(Long id, long timestampComment, String comment) {
        this.id = id;
        this.timestampComment = timestampComment;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTimestampComment() {
        return timestampComment;
    }

    public void setTimestampComment(long timestampComment) {
        this.timestampComment = timestampComment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Account getAccount() { return account; }

    public void setAccount(Account account) { this.account = account; }

    public Integer getIdAccountPub() {
        return idAccountPub;
    }

    public void setIdAccountPub(Integer idAccountPub) {
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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Comment)) {
            return false;
        }
        Comment other = (Comment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pulitsoft.coin.coinserver.model.data.entity.Comment[ id=" + id + " ]";
    }
    
}
