/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Leo
 */
@Entity
@Table(name = "prestamo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Prestamo.findAll", query = "SELECT p FROM Prestamo p")
    , @NamedQuery(name = "Prestamo.findByPrestaMarcador", query = "SELECT p FROM Prestamo p WHERE p.prestaMarcador = :prestaMarcador")
    , @NamedQuery(name = "Prestamo.findByPrestaBorrador", query = "SELECT p FROM Prestamo p WHERE p.prestaBorrador = :prestaBorrador")
    , @NamedQuery(name = "Prestamo.findByRetMarcador", query = "SELECT p FROM Prestamo p WHERE p.retMarcador = :retMarcador")
    , @NamedQuery(name = "Prestamo.findByRetBorrador", query = "SELECT p FROM Prestamo p WHERE p.retBorrador = :retBorrador")
    , @NamedQuery(name = "Prestamo.findByReserva", query = "SELECT p FROM Prestamo p WHERE p.reserva = :reserva")})
public class Prestamo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @Column(name = "presta_marcador")
    private boolean prestaMarcador;
    @Basic(optional = false)
    @Column(name = "presta_borrador")
    private boolean prestaBorrador;
    @Basic(optional = false)
    @Column(name = "ret_marcador")
    private boolean retMarcador;
    @Basic(optional = false)
    @Column(name = "ret_borrador")
    private boolean retBorrador;
    @Id
    @Basic(optional = false)
    @Column(name = "reserva")
    private Integer reserva;
    @JoinColumn(name = "reserva", referencedColumnName = "id_reserva", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Reserva reserva1;

    public Prestamo() {
    }

    public Prestamo(Integer reserva) {
        this.reserva = reserva;
    }

    public Prestamo(Integer reserva, boolean prestaMarcador, boolean prestaBorrador, boolean retMarcador, boolean retBorrador) {
        this.reserva = reserva;
        this.prestaMarcador = prestaMarcador;
        this.prestaBorrador = prestaBorrador;
        this.retMarcador = retMarcador;
        this.retBorrador = retBorrador;
    }

    public boolean getPrestaMarcador() {
        return prestaMarcador;
    }

    public void setPrestaMarcador(boolean prestaMarcador) {
        this.prestaMarcador = prestaMarcador;
    }

    public boolean getPrestaBorrador() {
        return prestaBorrador;
    }

    public void setPrestaBorrador(boolean prestaBorrador) {
        this.prestaBorrador = prestaBorrador;
    }

    public boolean getRetMarcador() {
        return retMarcador;
    }

    public void setRetMarcador(boolean retMarcador) {
        this.retMarcador = retMarcador;
    }

    public boolean getRetBorrador() {
        return retBorrador;
    }

    public void setRetBorrador(boolean retBorrador) {
        this.retBorrador = retBorrador;
    }

    public Integer getReserva() {
        return reserva;
    }

    public void setReserva(Integer reserva) {
        this.reserva = reserva;
    }

    public Reserva getReserva1() {
        return reserva1;
    }

    public void setReserva1(Reserva reserva1) {
        this.reserva1 = reserva1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reserva != null ? reserva.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Prestamo)) {
            return false;
        }
        Prestamo other = (Prestamo) object;
        if ((this.reserva == null && other.reserva != null) || (this.reserva != null && !this.reserva.equals(other.reserva))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Prestamo[ reserva=" + reserva + " ]";
    }
    
}
