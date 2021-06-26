/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Leo
 */
@Embeddable
public class HorarioPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "dia")
    private String dia;
    @Basic(optional = false)
    @Column(name = "cubiculo")
    private int cubiculo;

    public HorarioPK() {
    }

    public HorarioPK(String dia, int cubiculo) {
        this.dia = dia;
        this.cubiculo = cubiculo;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public int getCubiculo() {
        return cubiculo;
    }

    public void setCubiculo(int cubiculo) {
        this.cubiculo = cubiculo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dia != null ? dia.hashCode() : 0);
        hash += (int) cubiculo;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HorarioPK)) {
            return false;
        }
        HorarioPK other = (HorarioPK) object;
        if ((this.dia == null && other.dia != null) || (this.dia != null && !this.dia.equals(other.dia))) {
            return false;
        }
        if (this.cubiculo != other.cubiculo) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.HorarioPK[ dia=" + dia + ", cubiculo=" + cubiculo + " ]";
    }
    
}
