package com.example.torddis.models;

public class Historial {
    private int id;
    private String fecha_hora;
    private String imagen_evidencia;
    private String observacion;
    private String tipo_distraccion__nombre;
    private int tipo_distraccion__id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(String fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    public String getImagen_evidencia() {
        return imagen_evidencia;
    }

    public void setImagen_evidencia(String imagen_evidencia) {
        this.imagen_evidencia = imagen_evidencia;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getTipo_distraccion__nombre() {
        return tipo_distraccion__nombre;
    }

    public void setTipo_distraccion__nombre(String tipo_distraccion__nombre) {
        this.tipo_distraccion__nombre = tipo_distraccion__nombre;
    }

    public int getTipo_distraccion__id() {
        return tipo_distraccion__id;
    }

    public void setTipo_distraccion__id(int tipo_distraccion__id) {
        this.tipo_distraccion__id = tipo_distraccion__id;
    }
}
