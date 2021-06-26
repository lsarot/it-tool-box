
package org.example.websocket;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import org.example.model.Device;

@ApplicationScoped //EXISTEN VARIOS SCOPES (SESSION, REQUEST, NORMAL, ETC) 
public class DeviceSessionHandler {
    private int deviceId = 0;
    private final Set<Session> sessions = new HashSet<>(); //Each client connected to the application has its own session.
    private final Set<Device> devices = new HashSet<>();
    
    public void handleMessage(String message, Session session) {
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();

            if ("add".equals(jsonMessage.getString("action"))) {
                Device device = new Device();
                device.setName(jsonMessage.getString("name"));
                device.setDescription(jsonMessage.getString("description"));
                device.setType(jsonMessage.getString("type"));
                device.setStatus("Off");
                addDevice(device);
            }

            if ("remove".equals(jsonMessage.getString("action"))) {
                int id = (int) jsonMessage.getInt("id");
                removeDevice(id);
            }

            if ("toggle".equals(jsonMessage.getString("action"))) {
                int id = (int) jsonMessage.getInt("id");
                toggleDevice(id);
            }
        }
    }
    
    public void addSession(Session session) {
        System.out.println("Adding Session!");
        //cuando se abre una sesión(un usuario carga la página), se registra la session y se le envían los Device(un bean) (uno por uno al cliente).
        sessions.add(session);
        for (Device device : devices) {
            JsonObject addMessage = createAddMessage(device);//se crea un mensage add (objeto JSON) con cada Device y se envía a la pág recién cargada
            sendToSession(session, addMessage);
        }
    }

    public void removeSession(Session session) {
        System.out.println("Removing Session!");
        sessions.remove(session);
    }
    
    public List<Device> getDevices() {
        return new ArrayList<>(devices);
    }

    public void addDevice(Device device) {
        System.out.println("Adding Device!");
        device.setId(deviceId); //setea el id
        devices.add(device); //añade a la lista
        deviceId++; //incrementa el id para el próximo
        JsonObject addMessage = createAddMessage(device); //crea un mensaje add con el objeto
        sendToAllConnectedSessions(addMessage); //lo envía a todos
    }

    public void removeDevice(int id) {
        System.out.println("Removing Device!");
        Device device = getDeviceById(id);
        if (device != null) {
            devices.remove(device); //lo elimina de la lista
            JsonProvider provider = JsonProvider.provider();
            JsonObject removeMessage = provider.createObjectBuilder()
                    .add("action", "remove")
                    .add("id", id)
                    .build();
            sendToAllConnectedSessions(removeMessage); //crea un mensaje remove y lo envía a todos
        }
    }

    //Toggle the device status. (On/Off)
    public void toggleDevice(int id) {     
        Device device = getDeviceById(id);
        if (device != null) {
            if ("On".equals(device.getStatus())) {
                device.setStatus("Off");
            } else {
                device.setStatus("On");
            }
            JsonProvider provider = JsonProvider.provider();
            JsonObject updateDevMessage = provider.createObjectBuilder()
                    .add("action", "toggle")
                    .add("id", device.getId())
                    .add("status", device.getStatus())
                    .build();
            sendToAllConnectedSessions(updateDevMessage); //crea un mensaje toggle (el cual no hace nada más que refrescar un Device con el objeto enviado.. el cambio lo hace el servidor)
        }
    }

    private Device getDeviceById(int id) {
        for (Device device : devices) {
            if (device.getId() == id) {
                return device;
            }
        }
        return null;
    }

    //Build a JSON message for adding a device to the application.
    private JsonObject createAddMessage(Device device) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add("action", "add")  //add (enviado por servidor) es para que el cliente lo agregue en su GUI (add enviado por cliente es para que servidor lo agregue a la lista)
                .add("id", device.getId())
                .add("name", device.getName())
                .add("type", device.getType())
                .add("status", device.getStatus())
                .add("description", device.getDescription())
                .build();
        return addMessage;
    }

    //Send an event message to all connected clients.
    private void sendToAllConnectedSessions(JsonObject message) {
        for (Session session : sessions) {
            sendToSession(session, message);
        }
    }

    //Send an event message to a client.
    private void sendToSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
            //session.getAsyncRemote().sendText("");
            //session.getBasicRemote().sendPing(byteBuffer);
            //session.getBasicRemote().sendBinary(byteBuffer);
        } catch (IOException ex) {
            sessions.remove(session);
            Logger.getLogger(DeviceSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}