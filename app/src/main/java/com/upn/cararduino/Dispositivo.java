package com.upn.cararduino;

public class Dispositivo {
    String nombre;
    String mac;

    public Dispositivo(String nombre, String mac) {
        this.nombre = nombre;
        this.mac = mac;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
