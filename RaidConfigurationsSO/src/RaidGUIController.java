/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Leo
 */
public class RaidGUIController implements Initializable {
    
    @FXML
    private TextField tfqhdd;
    @FXML
    private TextField tfcapdisco;
    @FXML
    private ComboBox cbvelhdd;
    @FXML
    private ComboBox cbnivraid;
    @FXML
    private TextArea taventajas;
    @FXML
    private TextArea tadesventajas;
    @FXML 
    private TextField tfcapconjunto;
    @FXML
    private TextField tfvellectura;
    @FXML
    private TextField tfvelescritura;
    @FXML
    private TextField tfhamming;
    @FXML
    private CheckBox checkb1;
    @FXML
    private Button btborrar;
    @FXML
    private TextField tfsobra;
    @FXML
    private Label labelsobra;
    @FXML
    private TextField tfhamming2;
    @FXML
    private Label labelh1;
    @FXML
    private Label labelh2;
    @FXML
    private ImageView ivimagen;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    @FXML
    private void actionButtonComprobar(ActionEvent event){
            
        int nivel;
        int qhdd;
        int capHdd;
        int velHdd;
        
        qhdd = Integer.parseInt(tfqhdd.getText());
        capHdd = Integer.parseInt(tfcapdisco.getText());
        velHdd = Integer.parseInt(cbvelhdd.getValue().toString());
        nivel = Integer.parseInt(cbnivraid.getValue().toString());
        
        actionButtonBorrarcampos((ActionEvent) btborrar.getOnMouseClicked());
        ivimagen.setVisible(true);
        
        switch(nivel){
            case 0:
                Image img0 = new Image("Image/Raid0.png", true);
                ivimagen.setImage(img0);
                taventajas.setText("Rendimiento excelente en cuanto a lectura y escritura. Mientras más grandes sean las peticiones de información por parte del SO, mejor funcionará debido a que cumple con el paralelismo.");
                tadesventajas.setText("No hay redundancia. Rendimiento bajo con SO que solicita datos un sector a la vez debido al bajo paralelismo. Confiabilidad en cuanto a fallos es N veces inferior respecto a un SLED, siendo N el nro de HDD. Si falla alguno de los discos, se perderá toda la información.");
                if(qhdd>=2){
                checkb1.setSelected(true);
                tfcapconjunto.setText(Integer.toString(qhdd*capHdd));
                tfvellectura.setText(Integer.toString(qhdd*velHdd));
                tfvelescritura.setText(Integer.toString(qhdd*velHdd));
                }
                //
            break;
            case 1:
                Image img1 = new Image("Image/Raid1.png", true);
                ivimagen.setImage(img1);
                taventajas.setText("Rendimiento de lectura puede ser múltiplo lineal de la cantidad de discos. Tolerancia a fallas es excelente: si falla un HDD simplemente se utiliza el de respaldo. Se incrementa exponencialmente la fiabilidad respecto a un solo disco; es decir, la probabilidad de fallo del conjunto es igual al producto de las probabilidades de fallo de cada uno de los discos (pues para que el conjunto falle es necesario que lo hagan todos sus discos).");
                tadesventajas.setText("Rendimiento de escritura no es mejor que en un SLED. Aunque dependiendo de la configuración, pudiera ser múltiplo de N discos.");
                tfsobra.setText("0");
                
                if(qhdd>=2){
                checkb1.setSelected(true);
                
                if(qhdd%2 != 0){
                    qhdd--;
                    tfsobra.setText("1");
                }
      
                tfsobra.setVisible(true);
                labelsobra.setVisible(true);
                tfcapconjunto.setText(Integer.toString((qhdd/2)*capHdd));
                tfvellectura.setText(Integer.toString(qhdd*velHdd));
                tfvelescritura.setText(Integer.toString((qhdd/2)*velHdd));
                }
                //
            break;
            case 2:
                Image img2 = new Image("Image/Raid2.png", true);
                ivimagen.setImage(img2);
                taventajas.setText("Velocidades de transferencia altísimas, pero soporta la misma cantidad de peticiones E/S que un SLED. Si falla un HDD, este se repara a través del código Hamming, lo que se traduce en altísima confiabilidad.");
                tadesventajas.setText("Requiere de un gran número de HDD y estos deben ser sincronizados en términos de posición del brazo y rotacional, por ello incluso con 39 discos habría un retardo en cuanto a transferencia de datos. Adicionalmente debe hacerse una comprobación del código Hamming por cada tiempo de bit.");  
                if(qhdd>=7){
                    
                double h = qhdd/11;
                int p_int = (int) h;
                if(h - p_int > 0){
                    h++;
                }
                int m = (int) (qhdd - h);
                int ham = (int) h;
                
                checkb1.setSelected(true);
                tfhamming.setText(Integer.toString(ham));
                tfhamming2.setText(Integer.toString(m));              
                tfcapconjunto.setText(Integer.toString(capHdd*(qhdd-ham)));
                tfvellectura.setText(Integer.toString(velHdd));
                tfvelescritura.setText(Integer.toString(velHdd));
                }
                //
            break;
            case 3:
                Image img3 = new Image("Image/Raid3.png", true);
                ivimagen.setImage(img3);
                taventajas.setText("Utiliza sólo un disco de paridad (para detección y corrección de errores), el cual permite reconstruir un disco que falle en determinado momento.");
                tadesventajas.setText("Los HDD deben tener una sincronización exacta. Aunque ofrezca velocidades de transferencia muy altas ya que todos los discos trabajan a la vez, no soporta mayor número de peticiones de E/S que un SLED debido a que las palabras de datos están repartidas entre todos los HDD.");
                if(qhdd>=3){
                checkb1.setSelected(true);
                tfcapconjunto.setText(Integer.toString((qhdd-1)*capHdd));
                tfvellectura.setText(Integer.toString(velHdd));
                tfvelescritura.setText(Integer.toString(velHdd));
                }
                //
            break;
            case 4:
                Image img4 = new Image("Image/Raid4.png", true);
                ivimagen.setImage(img4);
                taventajas.setText("No requiere unidades sincronizadas debido a que trabaja por bloques y no bits de palabra, por ende los discos pueden trabajar de manera independiente. Diseño similar al Raid 0, pero utiliza una unidad adicional para la paridad, por lo que si una unidad falla se puede reconstruir a partir del resto de las unidades y la de paridad. Pudiera leer de varios discos a la vez.");
                tadesventajas.setText("Rendimiento pobre en múltiples actualizaciones (escritura) pequeñas. Esto se debe a que cualquier actualización por más pequeña que sea, requiere de dos lecturas y dos escrituras. Aunque pudiera escribir en varios discos al mismo tiempo, el cuello de botella está en la unidad de paridad ya que deberá esperar para múltiples actualizaciones.");
                if(qhdd>=3){
                checkb1.setSelected(true);
                tfcapconjunto.setText(Integer.toString((qhdd-1)*capHdd));
                tfvellectura.setText(Integer.toString((qhdd-1)*velHdd));
                tfvelescritura.setText(Integer.toString(velHdd));
                }
                //
            break;
            case 5:
                Image img5 = new Image("Image/Raid5.png", true);
                ivimagen.setImage(img5);
                taventajas.setText("Se enfoca en capacidad y tolerancia a fallos. Divide datos en bloques y no bits, lo que permite que los HDD trabajen independientemente, por lo que la lectura es más rápida que con una sóla unidad. Libera el cuello de botella de la unidad de paridad única del nivel Raid 4. Si ocurre un error CRC (control de redundancia cíclica), se utilizarán los sectores relativos en los otros bloques de la banda (incluyendo el de paridad) para reparar el sector que falló, a esto se le llama Modo Interino de Recuperación de Datos, el SO sabe que ocurrió, pero con el fin de notificar si hace falta cambiar una unidad defectuosa, esto pudiera afectar un tanto el rendimiento, pero las operaciones siguen iguales. Este modo de recuperación es más rápido en Raid 5 que en Raid 4 en el caso de que el CRC ocurra en el mismo HDD donde se encuentra el bloque de paridad.");
                tadesventajas.setText("Las escrituras en un RAID 5 son costosas en términos de operaciones de disco y tráfico entre los discos y la controladora, más aún si se somete a gran cantidad de actualizaciones más pequeñas que una banda de bloques. La perdida de dos discos provoca la pérdida de toda la data. Se recomienda no usar grupos de discos muy grandes debido a que estadísticamente la probabilidad de que falle un segundo disco donde ha fallado uno ya, antes de que se repare el que falló primero, aumenta con la cantidad de discos. Algunos vendedores de Raid recomiendan usar discos de diferentes lotes para reducir esta probabilidad de fallo entre dos discos.");
                if(qhdd>=3){
                checkb1.setSelected(true);
                tfcapconjunto.setText(Integer.toString((qhdd-1)*capHdd));
                tfvellectura.setText(Integer.toString((qhdd-1)*velHdd));
                tfvelescritura.setText(Integer.toString((qhdd-1)*velHdd));
                }
                //
            break;
            case 6:
                Image img6 = new Image("Image/Raid6.png", true);
                ivimagen.setImage(img6);
                taventajas.setText("Soporte para múltiples fallos incluso cuando se está en medio de una reparación de disco. La vel de lectura no se ve afectada y corresponde a la del más lento de ellos. El rendimiento aumenta a mayor número de discos, aunque la probabilidad de que fallen 2 antes de reparar el primero aumenta también.");
                tadesventajas.setText("La escritura se ve afectada ya que debe escribirse dos códigos de paridad.");
                if(qhdd>=3){
                checkb1.setSelected(true);
                tfcapconjunto.setText(Integer.toString((qhdd-2)*capHdd));
                tfvellectura.setText(Integer.toString((qhdd-2)*velHdd));
                tfvelescritura.setText(Integer.toString((qhdd-2)*velHdd));
                }
                //
            break;
        }        
    }
    
    @FXML
    private void actionButtonBorrarcampos(ActionEvent event){
    
        taventajas.clear();//.setText("Rendimiento excelente en cuanto a lectura y escritura. Mientras más grandes sean las peticiones de información por parte del SO, mejor funcionará debido a que cumple con el paralelismo.");
        tadesventajas.clear();//.setText("No hay redundancia. Rendimiento bajo con SO que solicita datos un sector a la vez debido al bajo paralelismo. Confiabilidad en cuanto a fallos es N veces inferior respecto a un SLED, siendo N el nro de HDD. Si falla alguno de los discos, se perderá toda la información.");
        checkb1.setSelected(false);
        tfcapconjunto.clear();
        tfvellectura.clear();
        tfvelescritura.clear();
        tfsobra.clear();
        ivimagen.setVisible(false);
        tfhamming.clear();
        tfhamming2.clear();
    //
    }
    
    @FXML
    private void actionNivelSelected(ActionEvent event){
        
        actionButtonBorrarcampos((ActionEvent) btborrar.getOnMouseClicked());
        
        if(Integer.parseInt(cbnivraid.getValue().toString())==2){           
            tfhamming.setVisible(true);
            tfhamming2.setVisible(true);
            labelh1.setVisible(true);
            labelh2.setVisible(true);
        }else{
            tfhamming.setVisible(false);
            tfhamming2.setVisible(false);
            labelh1.setVisible(false);
            labelh2.setVisible(false);
        }
        
        if(Integer.parseInt(cbnivraid.getValue().toString())==1){           
            tfsobra.setVisible(true);
            labelsobra.setVisible(true);
        }else{
            tfsobra.setVisible(false);
            labelsobra.setVisible(false);
        }
            
    }
}

