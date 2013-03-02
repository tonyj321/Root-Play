package play.classes.hist;

import java.io.IOException;
import play.RootObject;
import play.RootOutput;
import play.annotations.ClassDef;
import play.annotations.StreamerInfo;

/**
 * Marker Attributes class.
 * <a href="http://root.cern.ch/root/htmldoc/TAttMarker.html">TAttMarker</a>
 * @author tonyj
 */
@ClassDef(version = 2, checkSum = -87219836, title = "Marker Attributes")
public class TAttMarker implements RootObject {
    @StreamerInfo(value = "Marker color index")
    private short fMarkerColor = 1;
    @StreamerInfo(value = "Marker style")
    private short fMarkerStyle = 1;
    @StreamerInfo(value = "Marker size")
    private float fMarkerSize = 1;

    private void write(RootOutput out) throws IOException {
        out.writeShort(fMarkerColor);
        out.writeShort(fMarkerStyle);
        out.writeFloat(fMarkerSize);
    }
    
}
