package play.classes.hist;

import java.io.IOException;
import play.RootObject;
import play.RootOutput;
import play.annotations.ClassDef;
import play.annotations.StreamerInfo;

/**
 * Manages histogram axis attributes. 
 * @see <a href="http://root.cern.ch/root/htmldoc/TAttAxis.html">TAttAxis</a>
 * @author tonyj
 */
@ClassDef(version = 4, checkSum = 1395276684, title = "Axis Attributes")
public class TAttAxis implements RootObject {
    @StreamerInfo(value = "Number of divisions(10000*n3 + 100*n2 + n1)")
    private int fNdivisions = 510;
    @StreamerInfo(value = "color of the line axis")
    private short fAxisColor = 1;
    @StreamerInfo(value = "color of labels")
    private short fLabelColor = 1;
    @StreamerInfo(value = "font for labels")
    private short fLabelFont = 62;
    @StreamerInfo(value = "offset of labels")
    private float fLabelOffset = 0.005f;
    @StreamerInfo(value = "size of labels")
    private float fLabelSize = 0.04f;
    @StreamerInfo(value = "length of tick marks")
    private float fTickLength = 0.03f;
    @StreamerInfo(value = "offset of axis title")
    private float fTitleOffset = 1.0f;
    @StreamerInfo(value = "size of axis title")
    private float fTitleSize = 0.04f;
    @StreamerInfo(value = "color of axis title")
    private short fTitleColor = 1;
    @StreamerInfo(value = "font for axis title")
    private short fTitleFont = 62;
    private static final int version = 4;

    private void write(RootOutput out) throws IOException {
        out.writeInt(fNdivisions);
        out.writeShort(fAxisColor);
        out.writeShort(fLabelColor);
        out.writeShort(fLabelFont);
        out.writeFloat(fLabelOffset);
        out.writeFloat(fLabelSize);
        out.writeFloat(fTickLength);
        out.writeFloat(fTitleOffset);
        out.writeFloat(fTitleSize);
        out.writeShort(fTitleColor);
        out.writeShort(fTitleFont);
    }
    
}
