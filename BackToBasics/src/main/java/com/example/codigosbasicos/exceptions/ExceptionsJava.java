package com.example.codigosbasicos.exceptions;

/* http://www.davidmarco.es/articulo/tratamiento-de-excepciones-en-java

Jerarquía de clases:
    .Throwable
        .Error               //errores de una magnitud tal que una aplicación nunca debería intentar realizar nada con ellos (como errores de la JVM, desbordamientos de buffer, etc.) No las utilizamos!!!.
        .Exception  (Exception como tal es checked)
            //Hay N tipo checked (extienden de Exception)   // tales como ClassNotFoundException, IOException, ParseException, SQLException y muchas más.. (NO SON CULPA DEL PROGRAMADOR)
            .RuntimeException  (unchecked) // tales como ArrayIndexOutOfBounds (ERRORES DEL PROGRAMADOR) (pq tuvo que haber checkeado previamente, se pudo evitar!) (SI SE ARROJA UNA RUNTIME EXCEPTION EN UN MÉTODO, NO TE PEDIRÁ QUE USES TRY-CATCH , NI TE ENTERAS!)


    Checked: las capturamos o las relanzamos o ambas
        try/catch, throws(en firma del método) o try/catch con throw en el catch.

    Unchecked no se capturan.. en el ejemplo las evitaban comprobando con ifs, pero si por ej usas una api de terceros(ie. un webservice), quizás no es posible comprobar algún dato antes de arriesgar a que arroje la exception.
        cualquier situación excepcional que deje la aplicación en un estado irrecuperable y/o no sea inherente al proposito del código que la produce debe ser declarada como una excepción de tipo unchecked (igualmente no sirve de nada capturarlas ya que el estado es irrecuperable).


Generally RuntimeExceptions are exceptions that can be prevented programmatically.
E.g NullPointerException, ArrayIndexOutOfBoundException.
If you check for null before calling any method, NullPointerException would never occur.
Similarly ArrayIndexOutOfBoundException would never occur if you check the index first. 

RuntimeException are not checked by the compiler, so it is clean code.

These days people favor RuntimeException because the clean code it produces. It is totally a personal choice.

-----------------------------

    Al diseñar exceptions debemos pensar si usar checked (Exception) o unchecked (RuntimeException).

        Este ejemplo usa una checked. Quizá NO podremos diseñarla como unchecked pq quizás no podemos revisar el saldo disponible antes de realizar la operación de pago (si es una api de terceros).
        
        public void pagarCompraConTarjeta() { 
            try { 
                TarjetaDeCredito tarjetaPreferida = cliente.getTarjetaDeCreditoPreferida(); 
                tarjetaPreferida.realizarPago( getImporteCompra() );
            } catch(CreditoInsuficienteException ciex) {   // Informamos al usuario de crédito insuficiente   } 
        } 
        
        public class CreditoInsuficienteException extends Exception { // checked
            public CreditoInsuficienteExcepion(String ex) {  super(ex);  } //se pueden enviar otras cosas
        }

            public class TarjetaDeCredito {
                realizarPago(double monto){
                    if( monto > disponible ){
                        throw new CreditoInsuficienteException( "Credito insuficiente!" );
                    } else {...}
                }
            }
    -----
    mala práctica:
        usar catch Exception ex, ya que captura todo como general, sin diferir de un tipo u otro que es la idea del uso de exceptions.
        crear tu propia exception si ya hay una que maneja el caso.


*/

public class ExceptionsJava {
    
    public void testExceptionsClass() {
        
        try {
            this.testMyCheckedException();
        } catch (MyExceptionChecked ex) {
            //LAS CHECKED EXIGEN SER CAPTURADAS
        }
        
        this.testMyUncheckedException();
        //LAS UNCHECKED NI AVISA EL COMPILADOR
        //por eso ni nos enteramos si arroja una exception el método llamado
        //la única forma de capturarla, sin revisar la librería, es un bloque try-catch que atrape Exception (todas)
        //pero disque no debería hacerse porque son evitables si revisamos lo que enviamos o hacemos previamente!
        
    }
    
    private void testMyCheckedException() throws MyExceptionChecked { // REQUIERE USAR TRY-CATCH O THROWS EN FIRMA DEL MÉTODO!!!
        //CHECKED (extends Exception)
        throw new MyExceptionChecked("CHECKED throwed!");
    }
    
    private void testMyUncheckedException() {
        //UNCHECKED (extendes RuntimeException)
        throw new MyExceptionUnchecked("UNCHECKED throwed!"); //NÓTESE QUE NO EXIGE USAR TRY-CATCH O THROWS EN FIRMA DEL MÉTODO!!!
    }
    
    
    
    public static class MyExceptionChecked extends Exception {
        public MyExceptionChecked(String message) {
            super(message);
        }
    }
    
    public static class MyExceptionUnchecked extends RuntimeException {
        public MyExceptionUnchecked(String message) {
            super(message);
        }
    }
    
}
