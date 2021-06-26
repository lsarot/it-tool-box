package com.example.codigosbasicos.enums;


/**Clase tipo enum que muestra cómo inicializar posibles valores con un constructor
 * @since ApiLeo 1.0
 */
public enum TipoDePiedra {
    CALIZA("gris", 1200),   //   al declarar de esta forma debe existir un constructor con dichos parámetros
    MARMOL("blanca", 1423.55),
    PIZARRA("verde", 1325),
    CUARZO("negro", 1466.22);
    
    private final String color;
    private final double peso;
    
    //Notar que el constructor no lleva public/private ya que no se puede llamar con new
    TipoDePiedra(String color, double peso) {
        this.color = color;
        this.peso = peso;
    }
    
    public String getColor() { return this.color; }
    
    public double getPeso() { return this.peso; }
    
    
    /** Otra forma
	 * así o al revés (dado un enum devuelve un valor) le asignamos un valor a cada enum.
	 * */
	TipoDePiedra getValue(int i) {
		switch (i) {
		case 0:
			return CALIZA;
		case 1:
			return MARMOL;
		case 2:
			return PIZARRA;
		default:
			return CUARZO;
		}
	}
	
}
