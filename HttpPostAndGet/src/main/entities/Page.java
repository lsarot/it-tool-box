
package main.entities;

import java.io.Serializable;

public class Page implements Serializable {

    private static final long serialVersionUID = 1L;

    protected PagePK pagePK;

    private String url;

    private String type;

    private short size;

    private short width;

    private short height;

    private String multimediaBytesB64;//BASE64 Encoded


    public Page() {
    }

    public Page(PagePK pagePK) {
        this.pagePK = pagePK;
    }

    public Page(PagePK pagePK, String url, String type, short size, short width, short height) {
        this.pagePK = pagePK;
        this.url = url;
        this.type = type;
        this.size = size;
        this.width = width;
        this.height = height;
    }

    public Page(int idAccount, long timestampPub, short number) {
        this.pagePK = new PagePK(idAccount, timestampPub, number);
    }

    public PagePK getPagePK() {
        return pagePK;
    }

    public void setPagePK(PagePK pagePK) {
        this.pagePK = pagePK;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public short getSize() {
        return size;
    }

    public void setSize(short size) {
        this.size = size;
    }

    public short getWidth() {
        return width;
    }

    public void setWidth(short width) {
        this.width = width;
    }

    public short getHeight() {
        return height;
    }

    public void setHeight(short height) {
        this.height = height;
    }

    public String getMultimediaBytesB64() { return multimediaBytesB64; }

    public void setMultimediaBytesB64(String multimediaBytesB64) {
        this.multimediaBytesB64 = multimediaBytesB64;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pagePK != null ? pagePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Page)) {
            return false;
        }
        Page other = (Page) object;
        if ((this.pagePK == null && other.pagePK != null) || (this.pagePK != null && !this.pagePK.equals(other.pagePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pulitsoft.coin.coinserver.model.data.entity.Page[ pagePK=" + pagePK + " ]";
    }
    
}
