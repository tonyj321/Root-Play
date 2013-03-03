package play;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import org.junit.Test;
import play.demo.POJODemo;
import static org.junit.Assert.*;

/**
 *
 * @author tonyj
 */
public class POJOTest {

    @Test
    public void test1() throws IOException {

       TFile.setTimeWarp(true);
       File tmp = File.createTempFile("pojo", "root");
        tmp.deleteOnExit();
        try (TFile file = new TFile(tmp)) {
            file.add(new POJODemo.POJO());
        }
        try (CheckedInputStream in = new CheckedInputStream(new FileInputStream(tmp), new Adler32())) {
            byte[] buffer = new byte[4096];
            for (;;) {
                int l = in.read(buffer);
                if (l<0) {
                    break;
                }
            }
            assertEquals(3428885616L,in.getChecksum().getValue());
        }
    }
}
