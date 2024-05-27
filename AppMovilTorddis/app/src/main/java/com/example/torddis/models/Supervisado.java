package com.example.torddis.models;

public class Supervisado {
    int id;
    int tutor_id;
    int persona_id;
    String persona__nombres;
    String persona__apellidos;
    String persona__fecha_nacimiento;
    String persona__foto_perfil;
    String persona__edad;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTutor_id() {
        return tutor_id;
    }

    public void setTutor_id(int tutor_id) {
        this.tutor_id = tutor_id;
    }

    public int getPersona_id() {
        return persona_id;
    }

    public void setPersona_id(int persona_id) {
        this.persona_id = persona_id;
    }

    public String getPersona__nombres() {
        return persona__nombres;
    }

    public void setPersona__nombres(String persona__nombres) {
        this.persona__nombres = persona__nombres;
    }

    public String getPersona__apellidos() {
        return persona__apellidos;
    }

    public void setPersona__apellidos(String persona__apellidos) {
        this.persona__apellidos = persona__apellidos;
    }

    public String getPersona__fecha_nacimiento() {
        return persona__fecha_nacimiento;
    }

    public void setPersona__fecha_nacimiento(String persona__fecha_nacimiento) {
        this.persona__fecha_nacimiento = persona__fecha_nacimiento;
    }

    public String getPersona__foto_perfil() {
        return persona__foto_perfil;
    }

    public void setPersona__foto_perfil(String persona__foto_perfil) {
        this.persona__foto_perfil = persona__foto_perfil;
    }

    public String getPersona__edad() {
        return persona__edad;
    }

    public void setPersona__edad(String persona__edad) {
        this.persona__edad = persona__edad;
    }
}
