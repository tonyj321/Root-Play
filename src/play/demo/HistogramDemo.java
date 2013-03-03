package play.demo;

import play.classes.TObjString;
import java.io.IOException;
import play.TFile;

/**
 *
 * @author tonyj
 */
public class HistogramDemo {
    public static void main(String[] args) throws IOException {
        
        try (TFile file = new TFile("play.root")) {
            file.add(new TObjString("I am a root file written from Java!"));
            SimpleHistogramFiller demo = new SimpleHistogramFiller();
            file.add(demo.create1DHistogram("test1","Histogram created from Java"));
            file.add(demo.create1DHistogram("test2","Histogram created from Java"));
        }
    }
}
