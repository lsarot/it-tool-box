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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Leo
 */
@Entity
@Table(name = "horario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Horario.findAll", query = "SELECT h FROM Horario h")
    , @NamedQuery(name = "Horario.findByDia", query = "SELECT h FROM Horario h WHERE h.horarioPK.dia = :dia")
    , @NamedQuery(name = "Horario.findByHoraApertura", query = "SELECT h FROM Horario h WHERE h.horaApertura = :horaApertura")
    , @NamedQuery(name = "Horario.findByHoraCierre", query = "SELECT h FROM Horario h WHERE h.horaCierre = :horaCierre")
    , @NamedQuery(name = "Horario.findByCubiculo", query = "SELECT h FROM Horario h WHERE h.horarioPK.cubiculo = :cubiculo")})
public class Horario implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected HorarioPK horarioPK;
    @Basic(optional = false)
    @Column(name = "hora_apertura")
    @Temporal(TemporalType.TIME)
    private Date horaApertura;
    @Basic(optional = false)
    @Column(name = "hora_cierre")
    @Temporal(TemporalType.TIME)
    private Date horaCierre;
    @JoinColumn(name = "cubiculo", referencedColumnName = "id_cubiculo", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Cubiculo cubiculo1;

    public Horario() {
    }

    public Horario(HorarioPK horarioPK) {
        this.horarioPK = horarioPK;
    }

    public Horario(HorarioPK horarioPK, Date horaApertura, Date horaCierre) {
        this.horarioPK = horarioPK;
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
    }

    public Horario(String dia, int cubiculo) {
        this.horarioPK = new HorarioPK(dia, cubiculo);
    }

    public HorarioPK getHorarioPK() {
        return horarioPK;
    }

    public void setHorarioPK(HorarioPK horarioPK) {
        this.horarioPK = horarioPK;
    }

    public Date getHoraApertura() {
        return horaApertura;
    }

    public void setHoraApertura(Date horaApertura) {
        this.horaApertura = horaApertura;
    }

    public Date getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(Date horaCierre) {
        this.horaCierre = horaCierre;
    }

    public Cubiculo getCubiculo1() {
        return cubiculo1;
    }

    public void setCubiculo1(Cubiculo cubiculo1) {
        this.cubiculo1 = cubiculo1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (horarioPK != null ? horarioPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Horario)) {
            return false;
        }
        Horario other = (Horario) object;
        if ((this.horarioPK == null && other.horarioPK != null) || (this.horarioPK != null && !this.horarioPK.equals(other.horarioPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Horario[ horarioPK=" + horarioPK + " ]";
    }
    
}
