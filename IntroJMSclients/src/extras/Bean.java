package extras;

import java.io.Serializable;

/**
 * 
 * @author Leo
 * OBJETO BEAN PARA PROBAR EL PASO DE OBJETOS CON JMS
 */
public class Bean implements Serializable {

	private static final long serialVersionUID = -8963196389686352100L;
	private String name;
	private String location;
	private int edad;
	private Bean hijo;
	
	public Bean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Bean(String name, String location, int edad, Bean hijo) {
		this.name = name;
		this.location = location;
		this.edad = edad;
		this.hijo = hijo;
	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	public int getEdad() {
		return edad;
	}

	public Bean getHijo() {
		return hijo;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setEdad(int edad) {
		this.edad = edad;
	}

	public void setHijo(Bean hijo) {
		this.hijo = hijo;
	}

	//hashCode y equals los puse por probar!
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + edad;
		result = prime * result + ((hijo == null) ? 0 : hijo.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Bean)) {
			return false;
		}
		Bean other = (Bean) obj;
		if (edad != other.edad) {
			return false;
		}
		if (hijo == null) {
			if (other.hijo != null) {
				return false;
			}
		} else if (!hijo.equals(other.hijo)) {
			return false;
		}
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Bean [name=" + name + ", location=" + location + ", edad=" + edad + ", hijo=" + hijo + "]";
	}
}
