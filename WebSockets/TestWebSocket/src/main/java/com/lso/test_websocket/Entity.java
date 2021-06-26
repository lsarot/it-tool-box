
package com.lso.test_websocket;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Leo
 */
public class Entity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String direction;
    private int phone;

    public Entity() {}
    
    public Entity(String name, String direction, int phone) {
        this.name = name;
        this.direction = direction;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Entity{" + "name=" + name + ", direction=" + direction + ", phone=" + phone + '}';
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.direction);
        hash = 53 * hash + this.phone;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Entity other = (Entity) obj;
        if (this.phone != other.phone) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.direction, other.direction)) {
            return false;
        }
        return true;
    }
    
}
