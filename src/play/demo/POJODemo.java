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

    private static class POJO {

        int i = 25;
        double d = 1234.567;

        // Right now we need an explicit streamer, but soon this will not be 
        // needed.
        private void write(RootOutput out) throws IOException {
            out.writeInt(i);
            out.writeDouble(d);
        }
    }
}
