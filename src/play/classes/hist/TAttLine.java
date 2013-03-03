package play.classes.hist;

import java.io.IOException;
import play.RootObject;
import play.RootOutput;
import play.annotations.ClassDef;
import play.annotations.Title;

/**
 * Line Attributes class.
 * <a href="http://root.cern.ch/root/htmldoc/TAttLine.html">TAttLine</a>
 * @author tonyj
 */
@ClassDef(version = 1, checkSum = 1369587346)
@Title("Line Attributes")
public class TAttLine implements RootObject {
    @Title("line color")
    private short fLineColor = 1;
    @Title("line style")
    private short fLineStyle = 1;
    @Title("line width")
    private short fLineWidth = 1;

    private void write(RootOutput out) throws IOException {
        out.writeShort(fLineColor);
        out.writeShort(fLineStyle);
        out.writeShort(fLineWidth);
    }
    
}
