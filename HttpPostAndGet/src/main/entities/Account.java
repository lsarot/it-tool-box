
package main.entities;

import java.io.Serializable;
import java.util.List;

public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String user;

    private String password;//no sirve el size ni NotNull

    private String email;

    private String description;

    private String type;

    private String urlPhoto;

    private short photoShape;

    private long timestampCreation;
    
    private Provider provider;

    private String newPassword;

    private int followed;

    private int followers;


    public Account() {
    }

    public Account(Integer id) {
        this.id = id;
    }

    public Account(Integer id, String user, String password, String email, String type, long timestampCreation) {
        this.id = id;
        this.user = user;
        this.password = password;
        this.email = email;
        this.type = type;
        this.timestampCreation = timestampCreation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrlPhoto() { return urlPhoto; }

    public void setUrlPhoto(String urlPhoto) { this.urlPhoto = urlPhoto; }

    public long getTimestampCreation() {
        return timestampCreation;
    }

    public void setTimestampCreation(long timestampCreation) {
        this.timestampCreation = timestampCreation;
    }

    public short getPhotoShape() { return photoShape; }

    public void setPhotoShape(short photoShape) { this.photoShape = photoShape; }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public String getNewPassword() { return newPassword; }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public int getFollowed() { return followed; }

    public void setFollowed(int followed) { this.followed = followed; }

    public int getFollowers() { return followers; }

    public void setFollowers(int followers) { this.followers = followers; }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Account)) {
            return false;
        }
        Account other = (Account) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pulitsoft.coin.coinserver.model.data.entity.Account[ id=" + id + " ]";
    }
    
}
