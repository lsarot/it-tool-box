
package main;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Leo
 */
public class PruebaFileChooser {

    public Stage stage = new Stage();
    
    public PruebaFileChooser() {
        pruebaFileChooser1();
    }
    
    
    void pruebaFileChooser1() {
        final FileChooser fileChooser = new FileChooser();
 
        final Button openButton = new Button("Open a Picture...");
        final Button openMultipleButton = new Button("Open Pictures...");
 
        openButton.setOnAction((final ActionEvent e) -> {
            configureFileChooser(fileChooser);
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                openFile(file);
            }
        });
 
        openMultipleButton.setOnAction((final ActionEvent e) -> {
            configureFileChooser(fileChooser);
            List<File> list = fileChooser.showOpenMultipleDialog(stage);
            if (list != null) {
                for (File file : list) {
                    openFile(file);
                }
            }
        });
        
        final Button browseButton = new Button("Directory chooser");
        browseButton.setOnAction((final ActionEvent e) -> {
            final DirectoryChooser directoryChooser = new DirectoryChooser();
            final File selectedDirectory = directoryChooser.showDialog(stage);
            if (selectedDirectory != null) {
                selectedDirectory.getAbsolutePath();
            }
        });
 
        //save dialog es igual a directory chooser, pero debes colocar el nombre del archivo
        final Button saveButton = new Button("Save dialog");
        saveButton.setOnAction((final ActionEvent e) -> {
            fileChooser.setTitle("Save dialog");
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                System.out.println(file.getAbsoluteFile());
                //opero sobre el archivo
            }
        });
        
 
        final GridPane inputGridPane = new GridPane();
 
        GridPane.setConstraints(openButton, 0, 0);
        GridPane.setConstraints(openMultipleButton, 1, 0);
        GridPane.setConstraints(browseButton, 2, 0);
        GridPane.setConstraints(saveButton, 0, 1);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(openButton, openMultipleButton, browseButton, saveButton);
 
        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));
 
        stage.setScene(new Scene(rootGroup));
        stage.show();
    }
    
    private void openFile(File file) {
        try {
            if(Desktop.isDesktopSupported()) {//si el SO permite esta funcionalidad(obtener una instancia para abrir un URI)
                java.awt.Desktop.getDesktop().open(file);
            }           
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void configureFileChooser(final FileChooser fileChooser) {                           
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory( new File(System.getProperty("user.home")) );
        //muestra un selector para filtrar por tipos de extensión
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
            );
    }
    
}
