package com.example.torddis.models;

public class Tutor {
    private Integer id;
    private String usuario;
    private String correo;
    private Integer persona_id;
    private String persona__nombres;
    private String persona__apellidos;
    private String persona__fecha_nacimiento;
    private String foto_perfil;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Integer getPersona_id() {
        return persona_id;
    }

    public void setPersona_id(Integer persona_id) {
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

    public String getFoto_perfil() {
        return foto_perfil;
    }

    public void setFoto_perfil(String foto_perfil) {
        this.foto_perfil = foto_perfil;
    }
}
