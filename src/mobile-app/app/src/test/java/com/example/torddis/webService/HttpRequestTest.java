package com.example.torddis.webService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * UT-MB-02 — URL construction in {@link HttpRequest}.
 *
 * The back-end endpoints are all query-parameter driven (tutor_id,
 * supervisado_id, fecha, direccion_ruta), so a malformed query string means
 * the guardian sees another family's data or none at all. These are pure
 * static methods and run on the JVM without an emulator.
 */
public class HttpRequestTest {

    @Test
    public void anexaElPrimerParametroConInterrogacion() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("tutor_id", 7);
        String url = HttpRequest.append("http://192.0.2.10:8000/persona/supervisado/", params);
        assertEquals("http://192.0.2.10:8000/persona/supervisado/?tutor_id=7", url);
    }

    @Test
    public void anexaVariosParametrosConAmpersand() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("supervisado_id", 3);
        params.put("fecha", "2026-08-20");
        String url = HttpRequest.append("http://192.0.2.10:8000/monitoreo/graficos/", params);
        assertEquals("http://192.0.2.10:8000/monitoreo/graficos/?supervisado_id=3&fecha=2026-08-20", url);
    }

    @Test
    public void conservaLaUrlSiNoHayParametros() {
        String base = "http://192.0.2.10:8000/monitoreo/historial/";
        assertEquals(base, HttpRequest.append(base, new LinkedHashMap<String, Object>()));
        assertEquals(base, HttpRequest.append(base, (Map<?, ?>) null));
    }

    @Test
    public void anexaSobreUnaUrlQueYaTieneParametros() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("fecha", "2026-08-20");
        String url = HttpRequest.append(
                "http://192.0.2.10:8000/monitoreo/historial/?supervisado_id=3", params);
        assertTrue(url.contains("&fecha=2026-08-20"));
    }

    @Test
    public void anexaParesNombreValorVariadicos() {
        String url = HttpRequest.append("http://192.0.2.10:8000/monitoreo/distraccion/",
                "direccion_ruta", "192.0.2.11");
        assertEquals("http://192.0.2.10:8000/monitoreo/distraccion/?direccion_ruta=192.0.2.11", url);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaUnNumeroImparDeParametros() {
        HttpRequest.append("http://192.0.2.10:8000/monitoreo/", "tutor_id");
    }

    @Test
    public void codificaLosEspaciosDeLaRuta() {
        String url = HttpRequest.encode("http://192.0.2.10:8000/persona/tutor/?nombres=Maria Gomez");
        assertTrue(url.contains("Maria%20Gomez"));
    }

    @Test
    public void base64CodificaLasCredencialesBasic() {
        // 'tutora01:clave' -> dHV0b3JhMDE6Y2xhdmU=
        assertEquals("dHV0b3JhMDE6Y2xhdmU=", HttpRequest.Base64.encode("tutora01:clave"));
    }
}
