package play.classes;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import play.RootObject;
import play.RootOutput;
import play.annotations.ClassDef;

/**
 * This class stores the date and time with a precision of one second
 * in an unsigned 32 bit word (e.g. 950130 124559). The date is stored
 * with the origin being the 1st january 1995.
 * @see <a href="http://root.cern.ch/root/htmldoc/TDatime.html">TDatime</a>
 * @author tonyj
 */
@ClassDef(hasStandardHeader=false)
public class TDatime implements RootObject {
    private int fDatime;

    public TDatime() {
        this(new Date());
    }

    public TDatime(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR) - 1995;
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);

        fDatime = year << 26 | month << 22 | day << 17 | hour << 12 | min << 6 | sec;
    }

    private void write(RootOutput out) throws IOException {
        out.writeInt(fDatime);
    }
}
