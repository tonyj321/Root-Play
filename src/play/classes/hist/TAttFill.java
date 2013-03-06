package play.classes.hist;

import play.annotations.ClassDef;
import play.annotations.Title;

/**
 * Fill Area Attributes.
 * @see <a href="http://root.cern.ch/root/htmldoc/TAttFill.html">TAttFill</a>
 * @author tonyj
 */
@ClassDef(version = 1, checkSum = 1204118360)
@Title("Fill Area Attributes")
public class TAttFill {
    @Title("fill area color")
    private short fFillColor = 0;
    @Title("fill area style")
    private short fFillStyle = 1001;
}
