
package testappsencilla;

import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

//APLICACIÓN JAVAFX MUY SIMPLE, MUESTRA EL MANEJO DE IMÁGEN SPLASH

public class Testappsencilla extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        
        StackPane root = new StackPane();
        Text label = new Text("HOLA USUARIO!");
        
        Button btn = new Button();
        btn.setText("Say 'Hello User'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello User!");
                root.getChildren().set(0, label);
            }
        });
        
        root.getChildren().add(btn);
        
        Scene scene = new Scene(root, 250, 200);
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        
        //----------------------- MANEJO DEL SPLASH SCREEN
        manejoSplashScreen();
        //-----------------------
        
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public void manejoSplashScreen() {
        //debo incluirlo en manifest con SplashScreen-Image: testappsencilla/splash.jpg
        final SplashScreen splash = SplashScreen.getSplashScreen();
        /*if (splash == null) {
            System.out.println("SplashScreen.getSplashScreen() returned null");
            return;
        }
        Graphics2D g = splash.createGraphics();
        if (g == null) {
            System.out.println("g is null");
            return;
        }
        for(int i=0; i<100; i++) {
            renderSplashFrame(g, i);//crea un método que modifica la superficie, ej de oracle docs
            splash.update();
            try {
                Thread.sleep(90);
            }
            catch(InterruptedException e) {
            }
        }*/
        try {
            sleep(1000);
        } catch (InterruptedException ex) {}
        splash.close();
    }
    
}
