package hep.io.root.output.demo;

import hep.io.root.output.classes.TObjString;
import java.io.IOException;
import hep.io.root.output.TFile;

/**
 *
 * @author tonyj
 */
public class HistogramDemo {
    public static void main(String[] args) throws IOException {
        
        try (TFile file = new TFile("play.root")) {
            file.setCompressionLevel(1);
            file.add(new TObjString("I am a root file written from Java!"));
            SimpleHistogramFiller demo = new SimpleHistogramFiller();
            file.add(demo.create1DHistogram("test1","Histogram created from Java"));
            file.add(demo.create1DHistogram("test2","Histogram created from Java"));
        }
    }
}
