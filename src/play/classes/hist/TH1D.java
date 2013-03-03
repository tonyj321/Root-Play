package play.classes.hist;

import java.io.IOException;
import play.RootOutput;
import play.annotations.ClassDef;
import play.annotations.Super;
import play.classes.TArrayD;
import play.classes.TString;

/**
 * Histograms with one double per channel.
 * @see <a href="http://root.cern.ch/root/htmldoc/TH1D.html">TH1D</a>
 * @author tonyj
 */
@ClassDef(version = 1)
public class TH1D extends TH1 {
    private @Super TArrayD array;

    public TH1D(TString name, int nBins, double xMin, double xMax, double[] data) {
        super(name, nBins, xMin, xMax);
        array = new TArrayD(data);
    }

    private void write(RootOutput out) throws IOException {
        out.writeObject(array);
    }
    
}
