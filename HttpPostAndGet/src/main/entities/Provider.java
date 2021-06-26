
package main.entities;

import java.io.Serializable;

public class Provider implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idAccount;

    private String location;

    private String phone;

    private String emailBussiness;

    private String web;

    private Long mapLat;

    private Long mapLon;

    /* recupera el account asociado, pero siempre navegaremos de account a provider y no de provider a account
    @JoinColumn(name = "id_account", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Account account;
    */

    public Provider() {
    }

    public Provider(Integer idAccount) {
        this.idAccount = idAccount;
    }

    public Integer getIdAccount() {
        return idAccount;
    }

    public void setIdAccount(Integer idAccount) {
        this.idAccount = idAccount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmailBussiness() {
        return emailBussiness;
    }

    public void setEmailBussiness(String emailBussiness) {
        this.emailBussiness = emailBussiness;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public Long getMapLat() {
        return mapLat;
    }

    public void setMapLat(Long mapLat) {
        this.mapLat = mapLat;
    }

    public Long getMapLon() {
        return mapLon;
    }

    public void setMapLon(Long mapLon) {
        this.mapLon = mapLon;
    }

    /*
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
    */


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idAccount != null ? idAccount.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Provider)) {
            return false;
        }
        Provider other = (Provider) object;
        if ((this.idAccount == null && other.idAccount != null) || (this.idAccount != null && !this.idAccount.equals(other.idAccount))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pulitsoft.coin.coinserver.model.data.entity.Provider[ idAccount=" + idAccount + " ]";
    }
    
}
