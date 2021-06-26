package org.example.model;

//UN BEAN QUE SERVIRÁ DE OBJETO PARA LA COMUNICACIÓN ENTRE LAS PARTES

public class Device {

    private int id;
    private String name;
    private String status;
    private String type;
    private String description;

    public Device() {
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }
    
    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}