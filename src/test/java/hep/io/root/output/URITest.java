package hep.io.root.output;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author tonyj
 */
public class URITest {

    @Before
    public void setup() {
        TFile.setTimeWarp(false);
    }
    
    @Test
    public void URITest() throws IOException {
        File tmp = File.createTempFile("uri", "root");
        tmp.deleteOnExit();
        URI uri = tmp.toURI();
        try (TFile file = new TFile(uri)) {
            assertEquals(tmp.getName(),file.getName());
        }
    }
    @Test
    public void URITest2() throws IOException, URISyntaxException {
        URI uri = new URI("dummy:foo.bar");
        try (TFile file = new TFile(uri)) {
            assertTrue("Exception should have been throw",false);
        } catch (FileSystemNotFoundException x) {
            // OK, as expected
        }
    }
    @Test
    public void URITest3() throws IOException, URISyntaxException {
        File tmp = File.createTempFile("uri", "root");
        tmp.deleteOnExit();
        URI uri = tmp.toURI();
        try (TFile file = TFile.open(uri.toString())) {
            assertEquals(tmp.getName(),file.getName());
        }
    }
    @Test
    public void URITest4() throws IOException, URISyntaxException {
        File tmp = File.createTempFile("uri", "root");
        tmp.deleteOnExit();
        try (TFile file = TFile.open(tmp.getAbsolutePath())) {
            assertEquals(tmp.getName(),file.getName());
        }
    }
}