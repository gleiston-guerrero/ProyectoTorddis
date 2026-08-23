package com.example.torddis.interfaces;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * UT-MB-03 — Back-end base URL.
 *
 * Two properties are checked, and the second is a regression guard rather
 * than a functional test: the repository history contained a hard-coded
 * household IP address, which was removed in commit f1d5837. This test fails
 * if a real address is committed again.
 */
public class APIBaseTest {

    @Test
    public void laUrlBaseTerminaEnBarra() {
        // Every call site concatenates a path directly onto URLBASE, so a
        // missing trailing slash produces a 404 at runtime.
        assertTrue(APIBase.URLBASE.endsWith("/"));
    }

    @Test
    public void laUrlBaseNoContieneUnaDireccionRealEmbebida() {
        String url = APIBase.URLBASE;
        assertTrue("The base URL must remain a placeholder in version control",
                url.contains("BACKEND_IP") || url.contains("localhost")
                        || url.contains("127.0.0.1") || url.contains("10.0.2.2"));
        assertFalse("A literal household IP address must not be committed",
                url.matches(".*//(?!10\\.0\\.2\\.2|127\\.0\\.0\\.1)\\d+\\.\\d+\\.\\d+\\.\\d+.*"));
    }
}
