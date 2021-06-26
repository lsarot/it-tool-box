
package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import main.entities.Account;
import main.entities.Page;
import main.entities.Publication;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;

public class Main {
    
    public static void main (String[] args) {
    
        Main ref = new Main();
        
        try {
            //tomaremos un archivo del computador y obtenemos un array de bytes para enviar
            InputStream initialStream = new FileInputStream(new File("/Users/Leo/Desktop/ruta.jpg"));
            byte[] buffer = new byte[initialStream.available()];
            initialStream.read(buffer);
            initialStream.close();
            
            //Ahora enviamos Publication al RESTful WS por método POST (conel archivo codificado base64)         
            //ref.sendPostLowLevel(buffer);
            //ref.sendPostApacheHttpClient(buffer);
            //ref.sendPostApacheHttpClient_ProgressListener(buffer);
            
            
              
        } catch (Exception e) {
            System.out.println(e);
        }
    
    }

    //por alguna razón no llega al destino el request.. con get no tuvimos problema
    private void sendPostLowLevel(byte[] byteArray) {    
        try {
            URL url = new URL( "http://192.168.0.180:8000/api/v1.0/coin/publications/new" );

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            conn.setRequestProperty("Content-type", "application/json;charset=UTF-8");
            //conn.setRequestProperty("transfer-encoding", "chunked");
            
                        //para enviar binario (bytes)
                        //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                        //conn.connect();
                        //OutputStream os = conn.getOutputStream();
                        //os.write(bytes);
            
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            
            
            OutputStream os = conn.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            
            
                String json = getPublicationJson(byteArray);
            
            
            osw.write(json);
            //os.write(json.getBytes());
            
            osw.flush();
            osw.close();
            os.close();  //don't forget to close the OutputStream

            
            //conn.setConnectTimeout( 1000 );
            //conn.setReadTimeout( this.timeout );
            
            conn.connect();
            
        } catch (IOException ex) {
        }       
    }
    
    
    /** @Description: USANDO EL PROYECTO APACHE HTTP CLIENT (varios métodos)
     */
    private void sendPostApacheHttpClient(byte[] byteArray) {
        
        try {
            //1. BASIC POST
            /*CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://192.168.0.180:8000/...");

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", "John"));
            params.add(new BasicNameValuePair("password", "pass"));
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            CloseableHttpResponse response = client.execute(httpPost);
            client.close();*/
            
            
            
                        //2. USING BASIC AUTHENTICATION
                        /*CloseableHttpClient client = HttpClients.createDefault();
                        HttpPost httpPost = new HttpPost("http://192.168.0.180:8000/...");

                                //estas 2 líneas eran para conocer la librería, no son necesarias!
                                //EntityBuilder builder = org.apache.http.client.entity.EntityBuilder.create();
                                //HttpEntity httpEnt = builder.setContentEncoding("UTF-8").setText("texto").build();

                        httpPost.setEntity(new StringEntity("test post", "UTF-8"));
                        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("John", "pass");
                        httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));

                        CloseableHttpResponse response = client.execute(httpPost);
                        client.close();*/
            
            
                        
            //3. SEND JSON
            /*CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://192.168.0.180:8000/api/v1.0/coin/publications/new");

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
            String json = getPublicationJson(byteArray);
            StringEntity entity = new StringEntity(json, "UTF-8");
            httpPost.setEntity(entity);

            CloseableHttpResponse response = client.execute(httpPost);
            client.close();*/
            
            
            
                        //4. FLUENT API  (enviar formulario post en el body)
                        /*HttpResponse response = Request.Post("http://www.example.com").bodyForm(
                                    Form.form().add("username", "John").add("password", "pass").build())
                                    .execute().returnResponse();*/
                        
                        //(enviar json)
                        String json = getPublicationJson(byteArray);
                        StringEntity entity = new StringEntity(json, "UTF-8");
                        HttpResponse response = Request.Post("http://192.168.0.180:8000/api/v1.0/coin/publications/new")
                                .addHeader("Accept", "application/json").addHeader("Content-type", "application/json;charset=UTF-8")
                                .body(entity).execute().returnResponse();
                                
              
                        
            //5. MULTIPART POST REQUEST (INCLUDING FILE)
            /*CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://www.example.com");

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("username", "John");
            builder.addTextBody("password", "pass");
            builder.addBinaryBody("file", new File("test.txt"), ContentType.APPLICATION_OCTET_STREAM, "file.txt");//usaba .ext este último, no .txt

            HttpEntity multipart = builder.build();
            httpPost.setEntity(multipart);
            CloseableHttpResponse response = client.execute(httpPost);
            client.close();*/
                        
            
        } catch (Exception e) {
        }
        
    }
    
    
    /** 
     * @Description: USANDO HTTPCLIENT, PERO CON PROGRESS LISTENER.. 
     * NO ME FUNCIONA, SÓLO SE LLAMA AL LISTENER CUANDO LLEGA A 100%, ENVIAMOS 8MB PARA PROBAR MÁS DATA, USAMOS THREAD, PERO NO SIRVIÓ!
     */
    public void sendPostApacheHttpClient_ProgressListener(byte[] byteArray) {
        
        try {
            //CUALQUIERA DE LOS MÉTODOS USADOS EN EL MÉTODO ANTERIOR
            //USAREMOS FLUENT

            ProgressEntityWrapper.ProgressListener pListener = new ProgressEntityWrapper.ProgressListener() {
               @Override
               public void progress(float percentage) {
                   System.out.println(percentage+" %");
               }
           };

            String json = getPublicationJson(byteArray);
            StringEntity entity = new StringEntity(json, "UTF-8");
            HttpResponse response = Request.Post("http://192.168.0.180:8000/api/v1.0/coin/publications/new")
                    .addHeader("Accept", "application/json").addHeader("Content-type", "application/json;charset=UTF-8")
                    .body(
                        new ProgressEntityWrapper(entity, pListener)
                    ).execute().returnResponse();


        } catch (Exception e) {
        }
            
    }
    
    
    
    /** codificar base64 aumenta un string en 33% de su tamaño
     * no es para cifrar
     * pero si queremos enviar binario por json por ejemplo, pasar un bytearray a json triplica el contenido, 
     * pero si enviamos un string base64 apenas es 33%+ y luego decodificamos a bytearray
     */
    public String encodeBase64(byte[] byteArray) {
        //CODIFICAR Y DECODIFICAR BASE64
        //WHY IS USED FOR?: https://stackoverflow.com/questions/201479/what-is-base-64-encoding-used-for
        //HOW TO USE IT?: https://www.baeldung.com/java-base64-encode-and-decode
        
        //codificar
        String encodedString = Base64.getEncoder().encodeToString(byteArray); //"texto".getBytes()
        //System.out.println("STRING ENCODED_BASE64 LENGHT: " + encodedString.length());//33% +
        //System.out.println(encodedString);
        
                //decodificar
                //byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
                //String decodedString = new String(decodedBytes);
                //System.out.println(decodedString); //"texto"

                            //ALSO URL ENCODING AND DECODING
                            /*String originalUrl = "https://www.google.co.nz/?gfe_rd=cr&ei=dzbFV&gws_rd=ssl#q=java";
                            String encodedUrl = Base64.getUrlEncoder().encodeToString(originalURL.getBytes());
                            byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedUrl);
                            String decodedUrl = new String(decodedBytes);*/
                            //also the is mime encoding and decoding (max 76 chars per line)
                            
        return encodedString;                    
    }
    
    
    public String getPublicationJson(byte[] byteArray) {
        Publication pub = new Publication(11, 0);
        pub.setStartDate("2018-08-04");
        pub.setEndDate("2018-08-20");
        pub.setDescription("Promo en lulifresas");
        pub.setTags("#t1#t5#t7");
        pub.setPlace("Sevilla - España");
        Account account = new Account(11, null, "clave", null, null, 0);
        pub.setAccount(account);

        List<Page> pageL = new ArrayList<>();

        for(int i=0; i<3; i++) {
            Page p1 = new Page();
            p1.setType("image");
            p1.setWidth((short)400);
            p1.setHeight((short)500);
            String multimediaB64 = encodeBase64(byteArray);
            p1.setMultimediaBytesB64(multimediaB64);
            pageL.add(p1);
        }
        

        Page p2 = new Page();
        p2.setType("image");
        p2.setWidth((short)450);
        p2.setHeight((short)600);
        String multimediaB64 = encodeBase64(byteArray);
        p2.setMultimediaBytesB64(multimediaB64);
        pageL.add(p2);

        pub.setPageList(pageL);


        //DE OBJETO A JSON TEXTO
        Gson gson = new Gson().newBuilder().create(); //o new GsonBuilder().create()
        String representacionJSON = gson.toJson(pub);


        System.out.println("STRING LENGHT: "+representacionJSON.length());//STRING LENGHT: 421.643 y ahora 161.300
        //System.out.println(":: "+representacionJSON);
        
        return representacionJSON;
    }
    
    
    /**
     * @Description: get TimeBasedToken
     * 
     * el tiempo que perdería el servidor evaluando todos los valores de la solicitud, que pudiera hacerse desde un cliente no móvil, puede ser usado sólo evaluando este algoritmo ligero
     * se crea una cadena de n chars, se coloca en el primer char la posición de un segundo valor pero ubicado desde el final de la cadena hacia atrás,
     * el segundo valor indica en qué posición empieza el primero de 3 dígitos, separados cada uno por m posiciones
     * estos 3 digitos son los segundos UnixTimestamp del cliente. Se comparan con los segundos UnixTimestamp del servidor, si la diferencia no es mayor de xs segundos, se acepta.
     * PSK con BCrypt ?, tarda algo en resolver la comparación!
     */
    public static String getTimeBasedToken() {
        
        //random phrase of n chars
        StringBuilder token = new StringBuilder();
        final String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        final int N = alphabet.length();
        Random r = new Random();
        for (int i = 0; i < 40; i++) {
            token.append( alphabet.charAt(r.nextInt(N)) );
        }
        
        //set in pos 0 the position of second key counting backward from last char
        int firstKey = 1 + r.nextInt(8);// 1-9  (first key)
        token.setCharAt(0, Integer.toString(firstKey).charAt(0) );
        System.out.println(token);
        
        //set second key in correct position
        int posSecondKey = 40 - firstKey;// 31 a 39
        int secondKey = 1 + r.nextInt(8);// 1-9 (second key)               
        token.setCharAt(posSecondKey, Integer.toString(secondKey).charAt(0) );
        System.out.println(secondKey);
        
        //calculate time, last seconds
        long time = Calendar.getInstance().getTimeInMillis();
        String timeStr = Long.toString(time);
        timeStr = timeStr.substring(7, 10);
        System.out.println(timeStr);
        
        //calculamos pos de los 3 digitos de los segungos.. puede empezar en posición 11 a 19, y terminará en posición 21 a 29
        int f = 10 + secondKey;//first digit
        int s = f + 5;//second digit
        int t = s + 5;//third digit
              
        //seteamos los 3 digitos en la cadena de texto
        token.setCharAt(f, timeStr.charAt(0) );
        token.setCharAt(s, timeStr.charAt(1) );
        token.setCharAt(t, timeStr.charAt(2) );
        
        System.out.println(token);
        
        
        
        
        //----------------- LADO SERVIDOR AHORA
        
        try {//simulamos que tarda 150ms
            //Thread.sleep(2000);
        } catch (Exception e) {
        }
        
        //calculamos el tiempo del servidor
        long timeServer0 = Calendar.getInstance().getTimeInMillis();
        String timeStrSvr = Long.toString(timeServer0);
        timeStrSvr = timeStrSvr.substring(7, 10);
        int timeServer = Integer.parseInt(timeStrSvr);
        System.out.println(timeServer);
        
        //calculamos la posición de cada uno de los 3 dígitos del cliente
        posSecondKey = 40 - Integer.parseInt( Character.toString(token.charAt(0)) );
        secondKey = Integer.parseInt( Character.toString(token.charAt(posSecondKey)) );    
        f = 10 + secondKey;
        s = f + 5;
        t = s + 5;
        
        //armamos el tiempo del cliente
        StringBuilder clientTime = new StringBuilder();
        int timeClient = Integer.parseInt( clientTime.append(token.charAt(f)).append(token.charAt(s)).append(token.charAt(t)).toString() );
        System.out.println(timeClient);
        
        //si el sel servidor es menor, se antepone un 1 (ie. cliente envió 999 y servidor tiene 002, sería realmente 1002)
        if (timeServer < timeClient)
            timeServer += 1000;
        
        //evaluamos la diferencia de tiempo
        int diff = timeServer - timeClient;
        System.out.println("diff::"+diff);
        
        
        //15336 44 611 647 cliente
        //15336 44 653 421 servidor     
        //15336 44 999 000 cliente
        //15336 45 002 000 servidor
        
        return token.toString();
    }
    
    
}
