
package main;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author Leo
 */
public class PruebaMenus {

    public Stage stage = new Stage();
    
    public PruebaMenus() {
        pruebaMenus1();
    }
    
    final PageData[] pages = new PageData[] {
        new PageData("flor1",
            "The apple is the pomaceous fruit of the apple tree, species Malus "
            + "domestica in the rose family (Rosaceae). It is one of the most "
            + "widely cultivated tree fruits, and the most widely known of "
            + "the many members of genus Malus that are used by humans. "
            + "The tree originated in Western Asia, where its wild ancestor, "
            + "the Alma, is still found today.",
            "Malus domestica"),
        new PageData("flor2",
            "The hawthorn is a large genus of shrubs and trees in the rose "
            + "family, Rosaceae, native to temperate regions of the Northern "
            + "Hemisphere in Europe, Asia and North America. "
            + "The name hawthorn was "
            + "originally applied to the species native to northern Europe, "
            + "especially the Common Hawthorn C. monogyna, and the unmodified "
            + "name is often so used in Britain and Ireland.",
            "Crataegus monogyna"),
        new PageData("flor3",
            "The ivy is a flowering plant in the grape family (Vitaceae) native to"
            + " eastern Asia in Japan, Korea, and northern and eastern China. "
            + "It is a deciduous woody vine growing to 30 m tall or more given "
            + "suitable support,  attaching itself by means of numerous small "
            + "branched tendrils tipped with sticky disks.",
            "Parthenocissus tricuspidata"),
        new PageData("flor4",
            "The quince is the sole member of the genus Cydonia and is native to "
            + "warm-temperate southwest Asia in the Caucasus region. The "
            + "immature fruit is green with dense grey-white pubescence, most "
            + "of which rubs off before maturity in late autumn when the fruit "
            + "changes color to yellow with hard, strongly perfumed flesh.",
            "Cydonia oblonga")
    };
 
    final String[] viewOptions = new String[] {
        "Title", 
        "Binomial name", 
        "Picture", 
        "Description"
    };
 
    final Entry<String, Effect>[] effects = new Entry[] {
        new SimpleEntry<String, Effect>("Sepia Tone", new SepiaTone()),
        new SimpleEntry<String, Effect>("Glow", new Glow()),
        new SimpleEntry<String, Effect>("Shadow", new DropShadow())
    };
 
    final ImageView pic = new ImageView();
    final Label name = new Label();
    final Label binName = new Label();
    final Label description = new Label();
    private int currentIndex = -1;
    
    private class PageData {
        public String name;
        public String description;
        public String binNames;
        public Image image;
        public PageData(String name, String description, String binNames) {
            this.name = name;
            this.description = description;
            this.binNames = binNames;
            image = new Image(getClass().getResourceAsStream("/main/resources/raw/images/" + name + ".jpg"));
        }
    }
    private void shuffle() {
        int i = currentIndex;
        while (i == currentIndex) {
            i = (int) (Math.random() * pages.length);
        }
        pic.setImage(pages[i].image);
        name.setText(pages[i].name);
        binName.setText("(" + pages[i].binNames + ")");
        description.setText(pages[i].description);
        currentIndex = i;
    }
    
    // The createMenuItem method for 
    private static CheckMenuItem createMenuItem (String title, final Node node){
        CheckMenuItem cmi = new CheckMenuItem(title);
        cmi.setSelected(true);
        cmi.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue ov, Boolean old_val, Boolean new_val) {
                node.setVisible(new_val);
            }
        });
        return cmi;
    }

    
    public void pruebaMenus1() {
        stage.setTitle("Menu Sample");
        
        Scene scene = new Scene(new VBox(), 400, 350);
        scene.setFill(Color.OLDLACE);
        
        name.setFont(new Font("Verdana Bold", 22));
        binName.setFont(new Font("Arial Italic", 10));
        pic.setFitHeight(150);
        pic.setPreserveRatio(true);
        description.setWrapText(true);
        description.setTextAlignment(TextAlignment.JUSTIFY);
 
        shuffle();
        
        
        MenuBar menuBar = new MenuBar();
        
        
        final VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(0, 10, 0, 10));
        vbox.getChildren().addAll(name, binName, pic, description);
 
        
        // --- Menu File
        Menu menuFile = new Menu("File");
        MenuItem shuffle = new MenuItem("Shuffle", new ImageView(new Image("/main/resources/raw/images/shuffle.png")));
        shuffle.setOnAction((ActionEvent t) -> {
            shuffle();
            vbox.setVisible(true);
        });
        
        MenuItem clear = new MenuItem("Clear");
        clear.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
        clear.setOnAction((ActionEvent t) -> {
            vbox.setVisible(false);
        });
        
        MenuItem save = new MenuItem("Save as...");
        save.setOnAction((ActionEvent t) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image");
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(pic.getImage(), null), "png", file);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction((ActionEvent t) -> {
            System.exit(0);
        });
 
        menuFile.getItems().addAll(shuffle, clear, new SeparatorMenuItem(), exit);
        
        
        // --- Menu View (checked menu items to toggle if elements are visible or not)
        Menu menuView = new Menu("View");
        // --- Creating four check menu items within the start method
        CheckMenuItem titleView = createMenuItem ("Title", name);                                                       
        CheckMenuItem binNameView = createMenuItem ("Binomial name", binName);        
        CheckMenuItem picView = createMenuItem ("Picture", pic);        
        CheckMenuItem descriptionView = createMenuItem ("Description", description);     
        menuView.getItems().addAll(titleView, binNameView, picView, descriptionView);

        
        // --- Menu Edit (edit the image)
        Menu menuEdit = new Menu("Edit");
            //FALTA LEER ESTA PARTE DEL TUTORIAL DE MENUS Y CONTEXT MENUS
        
 
        menuBar.getMenus().addAll(menuFile, menuView, menuEdit);
        ((VBox) scene.getRoot()).getChildren().addAll(menuBar, vbox); //al vbox del root le añadimos la barra y otro vbox con los elementos
        stage.setScene(scene);
        stage.show();
        
    }
    
}
