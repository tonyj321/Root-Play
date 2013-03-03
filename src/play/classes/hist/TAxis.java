package play.classes.hist;

import java.io.IOException;
import play.RootOutput;
import play.annotations.ClassDef;
import play.Type;
import play.annotations.FieldType;
import play.annotations.Super;
import play.annotations.Title;
import play.classes.TArrayD;
import play.classes.THashList;
import play.classes.TNamed;

/**
 * This class manages histogram axis.
 *
 * @see <a href="http://root.cern.ch/root/htmldoc/TAxis.html">TAxis</a>
 * @author tonyj
 */
@ClassDef(version = 9, checkSum = 2116140609)
public class TAxis extends TNamed {

    private @Super TAttAxis tAttAxis = new TAttAxis();
    @Title("Number of bins")
    int fNbins;
    @Title("low edge of first bin")
    double fXmin;
    @Title("upper edge of last bin")
    double fXmax;
    @Title("Bin edges array in X")
    private TArrayD fXbins;
    @Title("first bin to display")
    private int fFirst = 0;
    @Title("last bin to display")
    private int fLast = 0;
    @Title("second bit status word")
    @FieldType(value = Type.kUShort)
    private short fBits2 = 0;
    @Title("on/off displaying time values instead of numerics")
    private boolean fTimeDisplay = false;
    @Title("Date&time format, ex: 09/12/99 12:34:00")
    private String fTimeFormat;
    @Title("List of labels")
    @FieldType(value = Type.kObjectP)
    private THashList fLabels;

    TAxis(String name, int nBins, double xMin, double xMax) {
        super(name, "");
        this.fNbins = nBins;
        this.fXmin = xMin;
        this.fXmax = xMax;
    }

    private void write(RootOutput out) throws IOException {
        out.writeObject(tAttAxis);
        out.writeInt(fNbins);
        out.writeDouble(fXmin);
        out.writeDouble(fXmax);
        out.writeObject(fXbins);
        out.writeInt(fFirst);
        out.writeInt(fLast);
        out.writeShort(fBits2);
        out.writeByte(fTimeDisplay ? 1 : 0); // TODO: Check this
        out.writeObject(fTimeFormat);
        out.writeObjectRef(fLabels);
    }
}
