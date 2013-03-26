package hep.io.root.output.classes.hist;

import hep.io.root.output.annotations.ClassDef;
import hep.io.root.output.annotations.Title;
import hep.io.root.output.classes.TArrayD;

/**
 * 1-Dim profile.
 *
 * @see <a href="http://root.cern.ch/root/htmldoc/TProfile.html">TProfile</a>
 * @author onoprien
 */
@ClassDef(version = 1)
@Title("1-Dim profile class")
public class TProfile extends TH1D {

    @Title("number of entries per bin")
    protected TArrayD fBinEntries;
    @Title("Array of sum of squares of weights per bin")
    protected TArrayD fBinSumw2;
//    @Title("Option to compute errors")
//    protected EErrorType fErrorMode;
    @Title("!True when TProfile::Scale is called")
    protected boolean fScaling;
    @Title("Total Sum of weight*Y")
    protected double fTsumwy;
    @Title("Total Sum of weight*Y*Y")
    protected double fTsumwy2;
    @Title("Upper limit in Y (if set)")
    protected double fYmax;
    @Title("Lower limit in Y (if set)")
    protected double fYmin;
    @Title("bin error approximation option")
    static protected boolean fgApproximate;

    public TProfile(String name, int nBins, double xMin, double xMax, double[] data) {
        super(name, nBins, xMin, xMax, data);
    }
   
}
