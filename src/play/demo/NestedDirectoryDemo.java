package play.demo;

import play.classes.TObjString;
import java.io.IOException;
import play.TDirectory;
import play.TFile;

/**
 *
 * @author tonyj
 */
public class NestedDirectoryDemo {
    public static void main(String[] args) throws IOException {
        
        try (TFile file = new TFile("nested.root")) {
            TDirectory dir = file.mkdir("sub-dir");
            TDirectory sdir = dir.mkdir("sub-sub-dir");
            sdir.add(new TObjString("I am a root file written from Java!"));
        }
    }
}
