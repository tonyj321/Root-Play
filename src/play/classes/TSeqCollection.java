package play.classes;

import java.io.IOException;
import play.RootOutput;
import play.annotations.ClassDef;

/**
 * Sequenceable collection abstract base class.
 * @see <a href="http://root.cern.ch/root/htmldoc/TSeqCollection.html">TSeqCollection</a>
 * @author tonyj
 */
@ClassDef(version = 0, checkSum = -677769907, hasStandardHeader = false)
public class TSeqCollection<A> extends TCollection<A> {

    private void write(RootOutput out) throws IOException {
    }
    
}
