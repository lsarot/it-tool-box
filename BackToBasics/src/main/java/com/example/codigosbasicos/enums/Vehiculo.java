package com.example.codigosbasicos.enums;


/**Muestra el uso de tipo enumerado en Java, el cual sirve para restringir los posibles valores que puede tomar una variable.
 * Aquí el tipo enumerado está declarado dentro de la clase Vehiculo, pero pudiera ser independiente.
 * @version ApiLeo 1.0
 */
public class Vehiculo {
    
    public enum MarcaDeVehiculo{
    	FORD, 
    	TOYOTA,
    	SUZUKI,
    	RENAULT,
    	SEAT,
    	MERCEDES_BENZ
    }
    
    private String matricula;
    private MarcaDeVehiculo marca;

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public MarcaDeVehiculo getMarca() {
        return marca;
    }

    public void setMarca(MarcaDeVehiculo marca) {
        this.marca = marca;
    }

    @Override
    public String toString() {
        return "Vehiculo{" + "matricula=" + matricula + ", marca=" + marca + '}';
    }
}
