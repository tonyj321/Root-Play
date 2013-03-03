package play;

import play.classes.TString;

/**
 *
 * @author tonyj
 */
class Checksum {

    private int id = 0;

    void compute(TString string) {
        compute(string.toString());
    }
    void compute(String string) {
        for (int i = 0; i < string.length(); i++) {
            compute(string.charAt(i));
        }
    }
    void compute(int i) {
        id = id * 3 + i;
    }

    int getValue() {
        return id;
    }

    public static void main(String[] args) {
        Checksum ck = new Checksum();
        ck.compute("TH1D");
        ck.compute("TH1");
        ck.compute("TArrayD");
//        ck.compute("TAttFill");
//        ck.compute("short");
//        ck.compute("fFillColor");
//        ck.compute("short");
//        ck.compute("fFillStyle");
        System.out.printf("%d\n", ck.getValue());
    }
}
