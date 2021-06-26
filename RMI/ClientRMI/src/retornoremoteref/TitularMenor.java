
package retornoremoteref;

import remoterefreturn.TitularCuenta;

public class TitularMenor extends TitularCuenta {
    
    private String nombreTutor;
    
    public TitularMenor(String n, String i, String t) {
        super(n, i);
        nombreTutor = t;
    }
    
    @Override
    public String toString() {
         return super.toString() + " | Tutor: " + nombreTutor;
     }
}