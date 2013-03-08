package hep.io.root.output;

import java.io.File;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import hep.io.root.output.classes.TObjString;

/**
 *
 * @author tonyj
 */
public class NestedDirectoryTest {

    @Test
    public void test1() throws IOException {
        TFile.setTimeWarp(true);
        File tmp = File.createTempFile("nested", "root");
        tmp.deleteOnExit();
        try (TFile file = new TFile(tmp)) {
            file.setCompressionLevel(0);
            TDirectory dir = file.mkdir("sub-dir");
            TDirectory sdir = dir.mkdir("sub-sub-dir");
            sdir.add(new TObjString("I am a root file written from Java!"));
        }
        assertEquals(3349830476L, POJOTest.computeChecksum(tmp));
    }
}
