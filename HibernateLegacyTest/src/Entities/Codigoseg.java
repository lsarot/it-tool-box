/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Leo
 */
@Entity
@Table(name = "codigoseg")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Codigoseg.findAll", query = "SELECT c FROM Codigoseg c")
    , @NamedQuery(name = "Codigoseg.findByCodigoSeg", query = "SELECT c FROM Codigoseg c WHERE c.codigoSeg = :codigoSeg")
    , @NamedQuery(name = "Codigoseg.findByVencCodSeg", query = "SELECT c FROM Codigoseg c WHERE c.vencCodSeg = :vencCodSeg")
    , @NamedQuery(name = "Codigoseg.findByUsuario", query = "SELECT c FROM Codigoseg c WHERE c.usuario = :usuario")})
public class Codigoseg implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @Column(name = "codigo_seg")
    private String codigoSeg;
    @Basic(optional = false)
    @Column(name = "venc_cod_seg")
    @Temporal(TemporalType.TIMESTAMP)
    private Date vencCodSeg;
    @Id
    @Basic(optional = false)
    @Column(name = "usuario")
    private Integer usuario;
    @JoinColumn(name = "usuario", referencedColumnName = "id_usuario", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Usuario usuario1;

    public Codigoseg() {
    }

    public Codigoseg(Integer usuario) {
        this.usuario = usuario;
    }

    public Codigoseg(Integer usuario, String codigoSeg, Date vencCodSeg) {
        this.usuario = usuario;
        this.codigoSeg = codigoSeg;
        this.vencCodSeg = vencCodSeg;
    }

    public String getCodigoSeg() {
        return codigoSeg;
    }

    public void setCodigoSeg(String codigoSeg) {
        this.codigoSeg = codigoSeg;
    }

    public Date getVencCodSeg() {
        return vencCodSeg;
    }

    public void setVencCodSeg(Date vencCodSeg) {
        this.vencCodSeg = vencCodSeg;
    }

    public Integer getUsuario() {
        return usuario;
    }

    public void setUsuario(Integer usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario1() {
        return usuario1;
    }

    public void setUsuario1(Usuario usuario1) {
        this.usuario1 = usuario1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (usuario != null ? usuario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Codigoseg)) {
            return false;
        }
        Codigoseg other = (Codigoseg) object;
        if ((this.usuario == null && other.usuario != null) || (this.usuario != null && !this.usuario.equals(other.usuario))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Codigoseg[ usuario=" + usuario + " ]";
    }
    
}
