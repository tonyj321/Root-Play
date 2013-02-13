package play;

import java.io.IOException;
import java.util.Random;
import play.TFile.TH1D;
import play.TFile.TString;

/**
 *
 * @author tonyj
 */
public class Main {
    public static void main(String[] args) throws IOException {
        try (TFile file = new TFile("play.root")) {
            file.add(new TObjString("I am a root file written from Java!"),
                    new TString("TObjString"),new TString("string"),TString.empty());
            
            int nBins = 100;
            double[] data = new double[nBins+2];
            double xMin = -5;
            double xMax = 5;
            Random random = new Random();
            for (int i=0; i<1000; i++) {
                double d = random.nextDouble();
                int bin = (int) Math.floor(nBins*(d-xMin)/(xMax-xMin));
                data[1+bin]++;
            }
            TString name = new TString("Test Histo");
            TH1D th1d = new TH1D(name,nBins,xMin,xMax,data);
            file.add(th1d,new TString("TH1D"),name,TString.empty());
        }
    }
}
