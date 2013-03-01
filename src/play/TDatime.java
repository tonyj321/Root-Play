package play;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import play.annotations.RootClass;

/**
 *
 * @author tonyj
 */
@RootClass(version=0, hasStandardHeader=false)
class TDatime implements RootObject {
    private int fDatime;

    TDatime() {
        this(new Date());
    }

    TDatime(Date date) {
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
