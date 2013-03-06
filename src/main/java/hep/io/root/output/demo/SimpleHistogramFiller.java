package hep.io.root.output.demo;

import java.util.Random;
import hep.io.root.output.classes.hist.TH1D;

/**
 *
 * @author tonyj
 */
public class SimpleHistogramFiller {
    private final Random random;

    public SimpleHistogramFiller() {
        this(new Random());
    }
    public SimpleHistogramFiller(Random random) {
        this.random = random;
    }
    public TH1D create1DHistogram(String name, String title) {
        int nBins = 100;
        double[] data = new double[nBins + 2];
        double xMin = -5;
        double xMax = 5;
        final int entries = 10000;
        double sumx = 0;
        double sumx2 = 0;

        for (int i = 0; i < entries; i++) {
            double d = random.nextGaussian();
            sumx += d;
            sumx2 += d * d;
            int bin = (int) Math.floor(nBins * (d - xMin) / (xMax - xMin));
            data[1 + bin]++;
        }
        TH1D th1d = new TH1D(name, nBins, xMin, xMax, data);
        th1d.setTitle(title);
        th1d.setEntries(entries);
        th1d.setSumw(entries);
        th1d.setSumw2(entries);
        th1d.setSumx(sumx);
        th1d.setSumx2(sumx2);
        return th1d;
    }
}
