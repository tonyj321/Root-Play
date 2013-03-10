package hep.io.root.output;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;
import hep.io.root.output.demo.SimpleHistogramFiller;
import org.junit.Before;

/**
 *
 * @author tonyj
 */
public class HistogramTest {

    @Before
    public void setup() {
        TFile.setTimeWarp(true);
    }

    @Test
    public void test1() throws IOException {

        File tmp = File.createTempFile("histogram", "root");
        tmp.deleteOnExit();
        try (TFile file = new TFile(tmp)) {
            file.setCompressionLevel(0);
            SimpleHistogramFiller demo = new SimpleHistogramFiller(new Random(123456));
            file.add(demo.create1DHistogram("test1", "Histogram created from Java"));
            file.add(demo.create1DHistogram("test2", "Histogram created from Java"));
        }
        assertEquals(4053281214L, POJOTest.computeChecksum(tmp));
    }

    @Test
    public void test2() throws IOException {

        File tmp = File.createTempFile("histogram", "root");
        tmp.deleteOnExit();
        try (TFile file = new TFile(tmp)) {
            SimpleHistogramFiller demo = new SimpleHistogramFiller(new Random(123456));
            file.add(demo.create1DHistogram("test1", "Histogram created from Java"));
            file.add(demo.create1DHistogram("test2", "Histogram created from Java"));
        }
        assertEquals(3804655589L, POJOTest.computeChecksum(tmp));
    }
}
