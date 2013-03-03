package play.demo;

import java.io.IOException;
import play.RootOutput;
import play.TFile;

/**
 *
 * @author tonyj
 */
public class POJODemo {

    public static void main(String[] args) throws IOException {

        try (TFile file = new TFile("pojo.root")) {
            file.add(new POJO());
        }
    }

    public static class POJO {

        private int i = 25;
        private double d = 1234.567;
        private String hello = "Hello World";

        // Right now we need an explicit streamer, but soon this will not be 
        // needed.
        private void write(RootOutput out) throws IOException {
            out.writeInt(i);
            out.writeDouble(d);
            out.writeObject(hello);
        }
    }
}
