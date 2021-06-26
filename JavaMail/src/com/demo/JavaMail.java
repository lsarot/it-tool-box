package com.demo;


// <editor-fold defaultstate="collapsed" desc="imports">
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Store;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;
import javax.mail.event.MessageChangedEvent;
import javax.mail.event.MessageChangedListener;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.jsoup.Jsoup;
// </editor-fold>

public class JavaMail {
    
    public static void main(String[] args) throws AddressException, MessagingException, NoSuchProviderException, IOException {
        enviarMensaje();
        //revisarPOP3_sin_SSL();
        //revisarIMAP_sin_SSL();
    }
    
    private static void enviarMensaje() throws AddressException {
        // <editor-fold defaultstate="collapsed" desc="clic para abrir">
        
        final String user="help.recovery.xpens@gmail.com"; //USER ES FROM TAMBIÉN 
        final String password="Pipeweno120690";
        String to="ldo.sto126@gmail.com";
        //InternetAddress[] toList = {new InternetAddress(to), new InternetAddress("ldo_sto126@hotmail.com")}; //lista de correos

        //Get the session object
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");    
        props.put("mail.smtp.socketFactory.port", "465");    
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.quitwait", "false");
        
        
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {  
                return new PasswordAuthentication(user,password);  
            }  
        });
        
        //UNA VEZ OBTENIDA LA SESSION
        
        try{  
            //Compose the message
            MimeMessage message = new MimeMessage(session);  
            message.setFrom(new InternetAddress(user)); //.. usar otra dir no cambiará nada, se coloca el de la cuenta que se usó.. "help.recovery.xpens@xpens.com"
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress(to) );  
            message.setSubject("Asunto");  
            message.setText("Hello, this is an example of sending email.");  //AL ENVIAR MULTIPART, ESTE NO TIENE EFECTO
            
                if (to.indexOf(',') > 0) {
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                } else {
                    message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));//message.addRecipient(...);
                }

            
                // La estructura sería 
                //MimeMessage recibe MimeMultipart recibe varios MimeBodyPart/BodyPart       
                //create MimeBodyPart object and set your message text     
                BodyPart messageBodyPart1 = new MimeBodyPart();  
                messageBodyPart1.setText("This is message body"); //EL BODY SE ENVÍA AHORA EN EL MULTIPART
                //AHORA UN ADJUNTO
                //create new MimeBodyPart object and set DataHandler object to this object      
                MimeBodyPart messageBodyPart2 = new MimeBodyPart();  
                String file = "/Users/Leo/Desktop/Diccionario personal.xlsx";
                String filename = "Diccionario personal.xlsx"; 
                DataSource source = new FileDataSource(file);  
                messageBodyPart2.setDataHandler(new DataHandler(source));  
                messageBodyPart2.setFileName(filename);
                //otro adjunto
                MimeBodyPart messageBodyPart3 = new MimeBodyPart();
                messageBodyPart3.setDataHandler(new DataHandler(new FileDataSource("/Users/Leo/Desktop/ruta.jpg")));  
                messageBodyPart3.setFileName("foto.jpg");           
                //ahora HTML en lugar de texto plano (podemos obtenerlo de un stream GET para mostrar publicidad como ya hemos visto, de un archivo del sistema o cualquier parte)             
                String HTMLmsg = "<!DOCTYPE html>\n" +
                                "<html>\n" +
                                "<body>\n" +
                                "\n" +
                                "<p>This is some text.</p>\n" +
                                "\n" +
                                "<div style=\"background-color:lightblue\">\n" +
                                "  <h3>This is a heading in a div element.</h3>\n" +
                                "  <p>This is some text in a div element.</p>\n" +
                                "</div>\n" +
                                "\n" +
                                "<p>This is more text.</p>\n" +
                                "\n" +
                                "</body>\n" +
                                "</html>";
                MimeBodyPart htmlBodyPart = new MimeBodyPart();
                htmlBodyPart.setContent(HTMLmsg, "text/html");          
                
                //AHORA AL CONTENEDOR PRINCIPAL LE AGREGAMOS CADA PARTE
                //create Multipart object and add MimeBodyPart objects to this object      
                Multipart multipart = new MimeMultipart();  
                multipart.addBodyPart(messageBodyPart1);  
                multipart.addBodyPart(messageBodyPart2);
                multipart.addBodyPart(messageBodyPart3);             
                multipart.addBodyPart(htmlBodyPart);
                
                //set the multiplart object to the message object  
                message.setContent( multipart );
                    
                
            //Send message  
            Transport.send(message);  
            System.out.println("message successfully sent!");  
        }catch (MessagingException mex) {mex.printStackTrace();}
        // </editor-fold>
    }
    
    
    private static void revisarPOP3_sin_SSL() {
        // <editor-fold defaultstate="collapsed" desc="clic para abrir">
        
        //CON YAHOO POP3 SE DESCARGA LA BANDEJA DE ENTRADA
        //CON GMAIL POP3 SE DESCARGAN CORREOS DE HACE SIGLOS EN CARPETAS PERDIDAS QUE SÓLO POP3 ENCUENTRA
        
        String host="pop.mail.yahoo.com";  
        final String user="ldo_sto126@yahoo.com";  
        final String password="xxxx"; 

        Properties props = System.getProperties();  
            props.put("mail.pop3.host", host);
            //props.put("mail.store.protocol", "pop3"); ya seteado abajo
            //props.put("mail.pop3s.port", "995");
            //props.put("mail.pop3s.starttls.enable", "true");
            //props.put("mail.pop3s.ssl.socketFactory.port", "995");    
            //props.put("mail.pop3s.ssl.socketFactory.class", "javax.net.ssl.SSLSocketFactory");    
            props.put("mail.pop3.auth", "true");
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {  
                return new PasswordAuthentication(user,password);  
            }  
        });
        
        try {
            //Create the POP3 store object and connect with the pop server
            POP3Store store = (POP3Store) session.getStore("pop3");   //session.getStore("pop3://user:passw@host:port/_Folder") creo
            store.connect();
            System.out.println("Is store SSL?: "+store.isSSL());

            //Create the finbox object and open it
            //SOBRE FOLDER PUEDO HACER MUCHAS COSAS, RECUPERAR POR CRITERIO, DE NÚMERO A NRO, CANTIDAD TOTAL Y MÁS...
            Folder folder = store.getFolder("INBOX");  
            folder.open(Folder.READ_ONLY); //READ_WRITE 

            //Retrieve the messages from the finbox in an array and print it 
            Message[] messages = folder.getMessages();
            for (int i = 0; i < messages.length; i++) {  
                Message message = messages[i];  
                System.out.println("---------------------------------");  
                System.out.println("Email Number " + (i + 1));  
                System.out.println("Subject: " + message.getSubject());  
                System.out.println("From: " + message.getFrom()[0]);  
                //System.out.println("Text: " + message.getContent().toString());
                
                    //LEER ADJUNTOS, PERO CREO QUE ALGO CON EL ANTIVIRUS NO ME LO PERMITE PQ DICE AVAST LA EXCEPTION, Y SI LO DESACTIVO NI LA EXCEPTION ARROJA
                    /*System.out.println(message.getSentDate());  
                    MimeMessage mimem = (MimeMessage) message;
                    Multipart multip = (Multipart) mimem.getContent();
                        System.out.println(multip.getCount());        
                    for (int i = 0; i < multip.getCount(); i++) {  
                        BodyPart bodyPart = multip.getBodyPart(i); //o MimeBodyPart  
                        InputStream stream = bodyPart.getInputStream();  
                        BufferedReader br = new BufferedReader(new InputStreamReader(stream));  

                        while (br.ready()) {  
                            System.out.println(br.readLine());  
                        }  
                        System.out.println();  
                    }*/
                    
            }  

            //Close the store and finbox objects  
            folder.close(false);  
            store.close();
        } catch (NoSuchProviderException e) {e.printStackTrace();
        } catch (MessagingException e) {e.printStackTrace();
        } //catch (IOException e) {e.printStackTrace();}
        // </editor-fold>
    }
    
    
    private static void revisarIMAP_sin_SSL() throws NoSuchProviderException, MessagingException, IOException {
        // <editor-fold defaultstate="collapsed" desc="clic para abrir">
        final String user="ldo.sto126@gmail.com";  
        final String password="xxxx"; 

        Properties props = System.getProperties();  
            props.put("mail.imap.host", "imap.gmail.com");
            //props.put("mail.store.protocol", "imaps");
            //props.put("mail.imap.port", "993"); //sobre SSL
            props.put("mail.imap.auth", "true");  
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {  
                return new PasswordAuthentication(user,password);  
            }  
        });
        
        IMAPStore store = (IMAPStore) session.getStore("imap"); 
        store.connect();
        
        //------
            System.out.println("isSSL: "+store.isSSL());
            System.out.println("Store: "+store);
        //------
            Folder[] deff = store.getDefaultFolder().list(); System.out.println("EN EL DEFAULT FOLDER:");
            for(Folder fd:deff)
                System.out.println("folder>> "+fd.getName());    
        //------
            Folder[] f2 = store.getFolder("[Gmail]").list(); System.out.println("EN EL FOLDER [Gmail]:");
            for(Folder fd:f2)
                System.out.println("folder>> "+fd.getName());    
        //------             
            Folder finbox = store.getFolder("INBOX"); System.out.println("DE FOLDER INBOX:");
            finbox.open(Folder.READ_WRITE);  //o READ_ONLY
            System.out.println("_msgQty:"+finbox.getMessageCount()+
                    "_fullName:"+finbox.getFullName()+
                    "_newMsgs:"+finbox.getNewMessageCount()+
                    "_unreadMsgs:"+finbox.getUnreadMessageCount()+
                    "_parentFullName:"+finbox.getParent().getFullName()+
                    "_urlName:"+finbox.getURLName());
            
            //MODIFICAR FLAGS (ELIMINADO, LEÍDO, BORRADOR, RESPONDIDO, ETC.) A UN CORREO PARTICULAR
                //Message msg0 = finbox.getMessage(0);
                //msg0.setFlag(Flags.Flag.SEEN, true);
            //LISTENERS A UNA CARPETA
                // <editor-fold defaultstate="collapsed" desc="listeners">
                finbox.addFolderListener(new FolderListener(){
                    @Override
                    public void folderCreated(FolderEvent fe) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void folderDeleted(FolderEvent fe) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void folderRenamed(FolderEvent fe) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                });
                finbox.addMessageChangedListener(new MessageChangedListener(){
                    @Override
                    public void messageChanged(MessageChangedEvent mce) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                });
                finbox.addMessageCountListener(new MessageCountListener(){
                    @Override
                    public void messagesAdded(MessageCountEvent mce) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void messagesRemoved(MessageCountEvent mce) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                });
                // </editor-fold>
            
        //--- PARTE SERIA (usando el folder INBOX de arriba)
        //BUSCAREMOS SI UN CORREO DE LA CAPERA INBOX TIENE ADJUNTOS, SI LOS TIENE LOS GUARDAMOS EN UNA CARPETA Y TAMBIÉN EL TEXTO DEL CORREO
            //el correo con adjuntos es mime multipart/mixed, igualmente en los métodos de busqueda de texto se revisará el mime type...
                //los multipart mixed se dividen en parts, recorreremos el correo 2 veces (1 por texto y otra por adjuntos), pero deberíamos mejorar esa parte.
        Message[] messages = finbox.getMessages();      
        for (int a = 0; a < messages.length; a++) {         //RECORREMOS LOS MENSAJES EN INBOX
            System.out.println("-------------" + (a + 1) + "-------------");  
            Message message = messages[a];
                
                System.out.println("messageID: "+message.getHeader("Message-ID")[0]);
                System.out.println("from: "+InternetAddress.toString(message.getFrom()));
                System.out.println("replyTo: "+message.getReplyTo()[0]);
                System.out.println("recipientsTO: "+InternetAddress.toString(message.getRecipients(Message.RecipientType.TO)));
                System.out.println("sentDate: "+message.getSentDate());
                System.out.println("subject: "+message.getSubject());
                System.out.println("flags: "+message.getFlags().toString());
                System.out.println("contentType: "+message.getContentType());
                //System.out.println("Message size: "+message.getSize());
                
            if( message.getContentType().contains("multipart/MIXED") ) {  // SI TIENE ADJUNTOS BÁSICAMENTE
                
                    String mailBody = getTextFromMessage(message); //haremos un recorrido al correo en busca de texto del cuerpo del msj.
                    boolean guardarBody = false; //para uso posterior si mi correo tiene adjuntos pq así lo quiero
                    
                MimeMessage mimem = (MimeMessage) message;
                MimeMultipart multip = (MimeMultipart) mimem.getContent();
                
                    String carpetaAdjuntos = new SimpleDateFormat("YYYY.MM.dd").format(message.getReceivedDate()) +" - "+ message.getSubject();        
                    File dir = new File("/Users/Leo/Desktop/Adjuntos/"+carpetaAdjuntos);
                
                for (int i = 0; i < multip.getCount(); i++) { //recorremos el correo, de tipo más complejo que simple texto, en busca de archivos adjuntos  
                    BodyPart bodyPart = multip.getBodyPart(i);
                    
                    if(bodyPart instanceof MimeBodyPart) {
                        MimeBodyPart mbp = (MimeBodyPart) bodyPart;
                        String filename = bodyPart.getFileName();
                        if (filename != null) { // SI ENCUENTRA UN ADJUNTO... o bodyPart.getDisposition().containts("ATTACHMENT")                        
                            guardarAdjuntoEnCarpeta(filename, dir, mbp);                          
                            guardarBody = true;//aunque se seteará tantas veces cm adjuntos haya, es una solución rápida...
                        }
                    }  
                }
                
                if (guardarBody) {
                    guardarBodyEnArchivoTxt(mailBody,dir);
                    guardarBodyEnArhivoHtml(mailBody, dir);
                }
            }
        }
       
        finbox.close(true);  
        store.close();
        // </editor-fold>
    }
    
        //PAR DE MÉTODOS QUE BUSCAN EL CUERPO CON TEXTO DEL CORREO (el de abajo sirve al de arriba)
        //se prefiere separarlos del código de arriba pq el segundo es recursivo, y así son más reutilizables
        private static String getTextFromMessage(Message message) throws MessagingException, IOException {
            // <editor-fold defaultstate="collapsed" desc="clic para abrir">
            String result = "";
            if (message.isMimeType("text/plain")) { //SI ES UN CORREO CON SIMPLE TEXTO PLANO
                result = message.getContent().toString();
            } else if (message.isMimeType("multipart/*")) { //SI ES UN CORREO CON ELEMENTOS MÁS COMPLEJOS
                MimeMultipart multip = (MimeMultipart) message.getContent();
                result = getTextFromMimeMultipart(multip);
            }
            return result;
            // </editor-fold>
        }
        
        /* NOTA: 
                - cuando creo un correo desde un gestor como la página de GMAIL por ej, el cuerpo del correo (no los adjuntos) es un multipart, 
                en el cual habrá una versión text/plain(respeta lineas) y una versión test/html(todo en una línea).
                - cuando creo un correo insertando un bodypart de texto o de html, este no es multipart, cada uno se trata individualmente
        SOLUCIÓN: 
                .si los correos creados en gestor (interfaz de gmail, yahoo, hotmail por ejemplo) tienen versión texto y versión html, entonces elijo una en el extractor del texto, y guardo el archivo en .txt o .html dependiendo de lo que quiera.
                .en un archivo .txt el html se muestra con tags y si lo parseo con Jsoup a texto entonces mete todo en una sóla línea
                .en un .html el texto plano se guarda todo en una sola línea
                .por otra parte, al crear correo desde mi aplicación java, o usamos puro html o puro texto, para mantener la consistencia en el formato y poder guardarlo como debe ser.
        */
        private static String getTextFromMimeMultipart(MimeMultipart multip)  throws MessagingException, IOException {
            // <editor-fold defaultstate="collapsed" desc="clic para abrir">
            String result = "";
            for (int i = 0; i < multip.getCount(); i++) {
                //SÓLO DEBO USAR text/plain o text/html como se explicó arriba
                BodyPart bodyPart = multip.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    System.out.println("text/plain");
                    result = result + "\n" + bodyPart.getContent();
                    //break; //saca del for pq en correos creados en editor de correo (ie. gmail) se crea un bodypart text/plain y otro igual text/html.. entonces se repetiría al guardarlo
                } else if (bodyPart.isMimeType("text/html")) {
                    System.out.println("text/html");
                    String html = (String) bodyPart.getContent();
                    result = result + "\n" + html;//Jsoup.parse(html).text(); si quiero guardar en .txt sin los html tags
                } else if (bodyPart.getContent() instanceof MimeMultipart) {//a veces el bodypart es a la vez multipart, tenemos que usar recursión
                    System.out.println("multipart");
                    result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
                }
            }
            
            return result;
            // </editor-fold>
        }
        
        //PAR DE MÉTODOS QUE GUARDAN ADJUNTOS Y BODY EN ARHIVOS
        private static void guardarAdjuntoEnCarpeta(String filename, File dir, MimeBodyPart mbp) throws FileNotFoundException, IOException, MessagingException {
            // <editor-fold defaultstate="collapsed" desc="clic para abrir">
            dir.mkdir();                         
            //System.out.println("mbp size: "+mbp.getSize());                                                      
            FileOutputStream fos = new FileOutputStream(dir.getAbsolutePath()+"/"+filename);
            InputStream is = mbp.getInputStream();
            byte[] b = new byte[mbp.getSize()];
            int leidos = is.read(b);       
            if ( leidos != -1 ) {
                fos.write(b,0,leidos); 
                System.out.println("Bytes leídos: "+leidos);
            }
            fos.close(); is.close();
            // </editor-fold>
        }
        
        private static void guardarBodyEnArchivoTxt(String cuerpo, File dir) throws FileNotFoundException, IOException {
            // <editor-fold defaultstate="collapsed" desc="clic para abrir">
            dir.mkdir();
            byte[] b = cuerpo.getBytes(Charset.forName("UTF-8"));
            FileOutputStream fos = new FileOutputStream(dir.getAbsolutePath()+"/email_body.txt");
            fos.write(b); fos.close();
            // </editor-fold>
        }

        private static void guardarBodyEnArhivoHtml(String cuerpo, File dir) throws FileNotFoundException, IOException {
            // <editor-fold defaultstate="collapsed" desc="clic para abrir">
            dir.mkdir();
            byte[] b = cuerpo.getBytes(Charset.forName("UTF-8"));
            FileOutputStream fos = new FileOutputStream(dir.getAbsolutePath()+"/email_body.html");
            fos.write(b); fos.close();
            // </editor-fold>
        }
        
    public static void reenviarMensaje() {
        // <editor-fold defaultstate="collapsed" desc="clic para abrir">
        //... recupero los datos de un Message
        //.. originalMsg supongamos
        
        // compose the message to forward  
            //Message newMsg = new MimeMessage(session);  
            //newMsg.setSubject("Fwd: " + originalMsg.getSubject());  
            //newMsg.setFrom(new InternetAddress( from ));  
            //newMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));  

        // Create your new message part  
            //BodyPart mbp = new MimeBodyPart();  
            //mbp.setText("Original message:\n\n");  

        // Create a multi-part to combine the parts  
            //Multipart multipart = new MimeMultipart();  
            //multipart.addBodyPart( mbp );  

        // Create and fill part for the forwarded content  
            //mbp = new MimeBodyPart();  
            //mbp.setDataHandler( originalMsg.getDataHandler() );  

        // Add part to multi part  
            //multipart.addBodyPart( mbp );  

        // Associate multi-part with message  
            //newMsg.setContent( multipart );  

        // Send message  
            //Transport.send( newMsg );
        // </editor-fold>
    }    

}