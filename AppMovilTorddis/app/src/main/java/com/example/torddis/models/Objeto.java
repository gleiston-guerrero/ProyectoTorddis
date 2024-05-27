package com.example.torddis.models;

public class Objeto {
    private int id;
    private String nombre;
    private String foto_objeto;
    private Boolean habilitado;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFoto_objeto() {
        return foto_objeto;
    }

    public void setFoto_objeto(String foto_objeto) {
        this.foto_objeto = foto_objeto;
    }

    public Boolean getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(Boolean habilitado) {
        this.habilitado = habilitado;
    }
}
