
package main;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Leo
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        
        new PruebaFileChooser();
        //new PruebaMenus();
        
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
        
        //imageV1.setVisible(true);
        //Image img0 = new Image("util/espera_turno.png", true);
        //imageV1.setImage(img0);
        //imageV1.setOpacity(0.25);


        //DIALOG BOX CON TEXTO
        /*Button btn = new Button();
        btn.setText("Open Dialog");
        btn.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {                  
                    VBox dialogVbox = new VBox(20);
                    dialogVbox.getChildren().add(new Text("This is a Dialog"));
                    
                    Scene dialogScene = new Scene(dialogVbox, 300, 200);
                    
                    final Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    //stage.initOwner(stage);
                    stage.setScene(dialogScene);
                    stage.show();
                }
             });
        Scene sc = new Scene(btn,100,100);
        stage.setScene(sc);
        stage.show();*/
