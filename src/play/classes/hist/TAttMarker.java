package play.classes.hist;

import java.io.IOException;
import play.RootOutput;
import play.annotations.ClassDef;
import play.annotations.Title;

/**
 * Marker Attributes class.
 * <a href="http://root.cern.ch/root/htmldoc/TAttMarker.html">TAttMarker</a>
 * @author tonyj
 */
@ClassDef(version = 2, checkSum = -87219836)
@Title("Marker Attributes")
public class TAttMarker {
    @Title("Marker color index")
    private short fMarkerColor = 1;
    @Title("Marker style")
    private short fMarkerStyle = 1;
    @Title("Marker size")
    private float fMarkerSize = 1;

    private void write(RootOutput out) throws IOException {
        out.writeShort(fMarkerColor);
        out.writeShort(fMarkerStyle);
        out.writeFloat(fMarkerSize);
    }
    
}
