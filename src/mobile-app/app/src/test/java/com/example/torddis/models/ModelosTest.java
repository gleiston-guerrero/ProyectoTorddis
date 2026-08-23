package com.example.torddis.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

/**
 * UT-MB-01 — Data-transfer objects of the Torddis mobile application.
 *
 * These are local JVM unit tests: they need no emulator and no device, and
 * they run with {@code ./gradlew test}. They verify that the field names of
 * the models match the keys the Django back-end serialises, because a
 * mismatch there is silent — Gson simply leaves the field null — and would
 * surface only as an empty screen during a monitoring session.
 */
public class ModelosTest {

    @After
    public void limpiarSesion() {
        UsuarioLogeado.unTutor = null;
    }

    @Test
    public void tutorConservaLosValoresAsignados() {
        Tutor tutor = new Tutor();
        tutor.setId(7);
        tutor.setUsuario("tutora01");
        tutor.setCorreo("tutora01@example.org");
        tutor.setPersona_id(11);
        tutor.setPersona__nombres("Maria");
        tutor.setPersona__apellidos("Gomez");
        tutor.setPersona__fecha_nacimiento("1985-06-01");

        assertEquals(Integer.valueOf(7), tutor.getId());
        assertEquals("tutora01", tutor.getUsuario());
        assertEquals("Maria", tutor.getPersona__nombres());
        assertEquals("1985-06-01", tutor.getPersona__fecha_nacimiento());
    }

    @Test
    public void tutorRecienCreadoTieneCamposNulos() {
        Tutor tutor = new Tutor();
        assertNull(tutor.getId());
        assertNull(tutor.getUsuario());
        assertNull(tutor.getFoto_perfil());
    }

    @Test
    public void supervisadoConservaLosValoresAsignados() {
        Supervisado supervisado = new Supervisado();
        supervisado.setId(3);
        supervisado.setTutor_id(7);
        supervisado.setPersona__nombres("Luis");
        supervisado.setPersona__edad("9 años 2 meses 5 días");

        assertEquals(3, supervisado.getId());
        assertEquals(7, supervisado.getTutor_id());
        assertEquals("Luis", supervisado.getPersona__nombres());
        assertTrue(supervisado.getPersona__edad().contains("años"));
    }

    @Test
    public void supervisadoRecienCreadoTieneIdentificadoresEnCero() {
        // The primitive int fields default to 0, which the activities use as
        // the "not yet selected" sentinel.
        Supervisado supervisado = new Supervisado();
        assertEquals(0, supervisado.getId());
        assertEquals(0, supervisado.getTutor_id());
    }

    @Test
    public void usuarioLogeadoEmpiezaVacio() {
        assertNull(UsuarioLogeado.unTutor);
    }

    @Test
    public void usuarioLogeadoGuardaYLiberaLaSesion() {
        Tutor tutor = new Tutor();
        tutor.setId(7);
        tutor.setUsuario("tutora01");

        UsuarioLogeado.unTutor = tutor;
        assertNotNull(UsuarioLogeado.unTutor);
        assertEquals("tutora01", UsuarioLogeado.unTutor.getUsuario());

        // Logging out must clear the static reference; otherwise the next
        // guardian to use the same device would inherit the session.
        UsuarioLogeado.unTutor = null;
        assertNull(UsuarioLogeado.unTutor);
    }
}
