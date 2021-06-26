
package pdfandexcel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class PdfAndExcel extends JFrame {

    public static void main(String[] args) {
        
        PdfAndExcel p = new PdfAndExcel();
        
        //p.miFileChooser();
        //GENERAR PDF ... USAMOS LA LIBRERÍA iText (tiene licencia de pago para producción)
        //p.generarPDFtexto();
        //p.generarPDF("HEADER", "Cuerpo del documento.\nOtra línea.", "FOOTER");
        //GENERAR EXCEL .XLS ... USAMOS LA LIBRERÍA jxl (no soporta .xlsx)
        //p.generarExcel();
        
    }
    
    private void miFileChooser() {
        //EXISTE LA VERSIÓN DE JAVAFX QUE FUNCIONA MEJOR
        JFileChooser fc = new JFileChooser();

        //fc.approveSelection();  fc.cancelSelection();
        //fc.ensureFileIsVisible(File);
        //fc.getSelectedFiles();
        //fc.getIcon(f);
        //fc.setCurrentDirectory(File);
        //fc.rescanCurrentDirectory();
        //fc.setControlButtonsAreShown(false); hacer doble clic para elegir archivo
        //fc.setDialogTitle("Titulo");
        //fc.setDialogType(JFileChooser.CUSTOM_DIALOG);
        //fc.setDragEnabled(true);
        //fc.setMultiSelectionEnabled(true);
        //fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);//FILES_ONLY, DIRECTORIES_ONLY, FILES_AND_DIRECTORIES
        //int option = fc.showOpenDialog(this); //cualquiera guarda en fc un contexto asociado a un File, lo que cambia es el diálogo y las etiquetas de los botones
        //int option = fc.showSaveDialog(this);
        int option = fc.showDialog(this, "Aprobar");

        if (option == JFileChooser.APPROVE_OPTION) { //CANCEL_OPTION, ERROR_OPTION
            File f = fc.getSelectedFile();
            String path = f.getName();
            System.out.println(path);
        }
        System.exit(0);
    }

    //PDF básico con texto
    private void generarPDFtexto() {
        //podemos setear que sólo acepte carpetas y no archivos, así no se sobreescribe uno existente, o arrojar un mensaje si ya existe uno con el mismo nombre!
        JFileChooser fc = new JFileChooser();
        int option = fc.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                FileOutputStream fos = new FileOutputStream(fc.getSelectedFile().getAbsoluteFile());//no puedo poner la extensión en el fileChooser y otra vez aquí pq no lo guarda!
                Document doc = new Document();
                PdfWriter.getInstance(doc, fos);
                doc.open();
                doc.add(new Paragraph("Contenido del pdf."));
                doc.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        System.exit(0);
    }

    //PDF con imágen y texto
    private void generarPDF(String header, String info, String footer) {
        //elijo imágen
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Elija una imágen");
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int option = fc.showDialog(this, "Elegir imagen");
        if (option == JFileChooser.APPROVE_OPTION) {
            String imgPath = fc.getSelectedFile().getAbsolutePath() + ".jpg"; //no tiene la extensión el archivo y no lo encontraba

            //elijo ruta de guardado
            fc.setDialogTitle("Elija ruta destino");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            option = fc.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                try {
                    Document d = new Document(PageSize.A4, 36, 36, 10, 10);
                    PdfWriter.getInstance(d, new FileOutputStream(fc.getSelectedFile().getAbsolutePath())); //al nombre en el filechooser le coloco .pdf al final
                    d.open();
                    d.add(getHeader(header));
                    Image img = Image.getInstance(imgPath);
                    img.scaleAbsolute(300, 200);
                    img.setAlignment(Element.ALIGN_CENTER);
                    d.add(img);
                    d.add(getBody(info));
                    d.add(getBody(" "));
                    d.add(getBody(" "));
                    d.add(getBody(" ")); //insertamos lineas vacías con el método propio
                    d.add(getFooter(footer));
                    d.close();

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
        System.exit(0);
    }

    private Font fuenteBold = new Font(Font.FontFamily.COURIER, 10, Font.BOLD);
    private Font fuenteNormal = new Font(Font.FontFamily.COURIER, 8, Font.NORMAL);
    private Font fuenteItalic = new Font(Font.FontFamily.COURIER, 8, Font.ITALIC);

    private Paragraph getHeader(String texto) {
        Paragraph p = new Paragraph();
        Chunk c = new Chunk();
        p.setAlignment(Element.ALIGN_CENTER);
        c.append(texto);
        c.setFont(fuenteBold);
        p.add(c);
        return p;
    }

    private Paragraph getBody(String texto) {
        Paragraph p = new Paragraph();
        Chunk c = new Chunk();
        p.setAlignment(Element.ALIGN_JUSTIFIED);
        c.append(texto);
        c.setFont(fuenteNormal);
        p.add(c);
        return p;
    }

    private Paragraph getFooter(String texto) {
        Paragraph p = new Paragraph();
        Chunk c = new Chunk();
        p.setAlignment(Element.ALIGN_RIGHT);
        c.append(texto);
        c.setFont(fuenteItalic);
        p.add(c);
        return p;
    }

    //---------------------------------------------------------------
    
    private void leerExcel() {
        try {
            JFileChooser fc = new JFileChooser();
            int option = fc.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                
                Workbook wb = jxl.read.biff.WorkbookParser.getWorkbook(file);
                Sheet sheet = wb.getSheet(0);
                System.out.println(sheet.getRows());
                System.out.println(sheet.getColumns());
                System.out.println(sheet.getRow(0)[5].getContents());//row 0, column 5, contenido (pq retorna un Cell)
                
                
                
                
            }
        } catch (Exception e) {
        }
    }
    
    
    private void generarExcel() {
        String[][] entrada = tomarDatos();
        try {

            //elijo ruta de guardado
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Guardar como... (usar .xls)");
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int option = fc.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                //configuración de documento
                WorkbookSettings conf = new WorkbookSettings();
                conf.setEncoding("ISO-8859-1");
                //creo documento
                WritableWorkbook wb = Workbook.createWorkbook(fc.getSelectedFile(), conf);
                //creo hoja
                WritableSheet ws = wb.createSheet("Resultado", 0);
                //creo formato de celda
                WritableFont h = new WritableFont(WritableFont.COURIER, 16, WritableFont.NO_BOLD);
                WritableCellFormat hformat = new WritableCellFormat(h);

                //recorro la matriz
                for (int i = 0; i < entrada.length; i++) {          //filas
                    for (int j = 0; j < entrada[i].length; j++) {   //columnas

                        //agrego celda
                        ws.addCell(new jxl.write.Label(j, i, entrada[i][j], hformat));

                    }
                }
                wb.write();
                wb.close();
            }

        } catch (IOException | WriteException e) {
        }
        System.exit(0);
    }

    private String[][] tomarDatos() {
        String[][] m = new String[4][3];

        m[0][0] = "NOMBRE";
        m[0][1] = "APELLIDO";
        m[0][2] = "EDAD";
        m[1][0] = "Jhon";
        m[1][1] = "Doe";
        m[1][2] = "20";
        m[2][0] = "Mary";
        m[2][1] = "Moe";
        m[2][2] = "23";
        m[3][0] = "Cindy";
        m[3][1] = "Lindy";
        m[3][2] = "18";

        return m;
    }
    
    
}
