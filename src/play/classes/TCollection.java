package play.classes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import play.RootObject;
import play.RootOutput;
import play.annotations.ClassDef;
import play.annotations.Title;

/**
 * Collection abstract base class.
 * @see <a href="http://root.cern.ch/root/htmldoc/TCollection.html"></a>
 * @author tonyj
 */
@ClassDef(version = 3, checkSum = -1882108578, hasStandardHeader = false)
public abstract class TCollection<A extends RootObject> extends TObject implements Iterable<A> {
    @Title("name of the collection")
    TString name = TString.empty();
    @Title("number of elements in the collection")
    private int fSize;
    transient Collection<A> list = new ArrayList<>();

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
