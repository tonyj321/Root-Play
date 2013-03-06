package hep.io.root.output;

import hep.io.root.output.TFile;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;
import hep.io.root.output.demo.SimpleHistogramFiller;

/**
 *
 * @author tonyj
 */
public class HistogramTest {

    @Test
    public void test1() throws IOException {

        TFile.setTimeWarp(true);
        File tmp = File.createTempFile("histogram", "root");
        tmp.deleteOnExit();
        try (TFile file = new TFile(tmp)) {
            SimpleHistogramFiller demo = new SimpleHistogramFiller(new Random(123456));
            file.add(demo.create1DHistogram("test1", "Histogram created from Java"));
            file.add(demo.create1DHistogram("test2", "Histogram created from Java"));
        }
        assertEquals(4053281214L, POJOTest.computeChecksum(tmp));
    }
}
