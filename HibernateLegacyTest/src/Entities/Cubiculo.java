/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Leo
 */
@Entity
@Table(name = "cubiculo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cubiculo.findAll", query = "SELECT c FROM Cubiculo c")
    , @NamedQuery(name = "Cubiculo.findByIdCubiculo", query = "SELECT c FROM Cubiculo c WHERE c.idCubiculo = :idCubiculo")
    , @NamedQuery(name = "Cubiculo.findByUbicacion", query = "SELECT c FROM Cubiculo c WHERE c.ubicacion = :ubicacion")
    , @NamedQuery(name = "Cubiculo.findByCapMinReq", query = "SELECT c FROM Cubiculo c WHERE c.capMinReq = :capMinReq")
    , @NamedQuery(name = "Cubiculo.findByCapMaxPer", query = "SELECT c FROM Cubiculo c WHERE c.capMaxPer = :capMaxPer")
    , @NamedQuery(name = "Cubiculo.findByReservable", query = "SELECT c FROM Cubiculo c WHERE c.reservable = :reservable")})
public class Cubiculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id_cubiculo")
    private Integer idCubiculo;
    @Basic(optional = false)
    @Column(name = "ubicacion")
    private String ubicacion;
    @Basic(optional = false)
    @Column(name = "cap_min_req")
    private int capMinReq;
    @Basic(optional = false)
    @Column(name = "cap_max_per")
    private int capMaxPer;
    @Basic(optional = false)
    @Column(name = "reservable")
    private boolean reservable;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cubiculo1")
    private List<Horario> horarioList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cubiculo")
    private List<Reserva> reservaList;

    public Cubiculo() {
    }

    public Cubiculo(Integer idCubiculo) {
        this.idCubiculo = idCubiculo;
    }

    public Cubiculo(Integer idCubiculo, String ubicacion, int capMinReq, int capMaxPer, boolean reservable) {
        this.idCubiculo = idCubiculo;
        this.ubicacion = ubicacion;
        this.capMinReq = capMinReq;
        this.capMaxPer = capMaxPer;
        this.reservable = reservable;
    }

    public Integer getIdCubiculo() {
        return idCubiculo;
    }

    public void setIdCubiculo(Integer idCubiculo) {
        this.idCubiculo = idCubiculo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public int getCapMinReq() {
        return capMinReq;
    }

    public void setCapMinReq(int capMinReq) {
        this.capMinReq = capMinReq;
    }

    public int getCapMaxPer() {
        return capMaxPer;
    }

    public void setCapMaxPer(int capMaxPer) {
        this.capMaxPer = capMaxPer;
    }

    public boolean getReservable() {
        return reservable;
    }

    public void setReservable(boolean reservable) {
        this.reservable = reservable;
    }

    @XmlTransient
    public List<Horario> getHorarioList() {
        return horarioList;
    }

    public void setHorarioList(List<Horario> horarioList) {
        this.horarioList = horarioList;
    }

    @XmlTransient
    public List<Reserva> getReservaList() {
        return reservaList;
    }

    public void setReservaList(List<Reserva> reservaList) {
        this.reservaList = reservaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCubiculo != null ? idCubiculo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cubiculo)) {
            return false;
        }
        Cubiculo other = (Cubiculo) object;
        if ((this.idCubiculo == null && other.idCubiculo != null) || (this.idCubiculo != null && !this.idCubiculo.equals(other.idCubiculo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Cubiculo[ idCubiculo=" + idCubiculo + " ]";
    }
    
}
