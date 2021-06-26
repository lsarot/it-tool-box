
package maquinaremota;

import computeengine.Task;
import computeengine.Task;
import java.io.Serializable;

public class MiTarea implements Task<Float>, Serializable {

    private static final long serialVersionUID = 227L;
    
    public MiTarea() {}
    
    @Override
    public Float execute() {// Cualquier operación que desee realizar en la máquina remota
        
        float result = 2;
        for(int i=0;i<10;i++) {
            result += Math.pow(result, i);
        }
        
        return result;
    }
    
}