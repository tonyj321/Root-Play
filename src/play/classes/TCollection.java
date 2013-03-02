package play.classes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import play.RootObject;
import play.RootOutput;
import play.annotations.ClassDef;
import play.annotations.StreamerInfo;

/**
 * Collection abstract base class.
 * @see <a href="http://root.cern.ch/root/htmldoc/TCollection.html"></a>
 * @author tonyj
 */
@ClassDef(version = 3, checkSum = -1882108578, hasStandardHeader = false)
public abstract class TCollection<A extends RootObject> extends TObject implements Iterable<A> {
    @StreamerInfo(value = "name of the collection")
    TString name = TString.empty();
    @StreamerInfo(value = "number of elements in the collection")
    private int fSize;
    List<A> list = new ArrayList<>();

    private void write(RootOutput out) throws IOException {
    }

    public void add(A record) {
        list.add(record);
    }

    @Override
    public Iterator<A> iterator() {
        return list.iterator();
    }
    
}
