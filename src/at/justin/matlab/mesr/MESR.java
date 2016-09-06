package at.justin.matlab.mesr;

/**
 * Created by Andreas Justin on 2016-09-06.
 */
public class MESR {

    private static final MESR INSTANCE = new MESR();

    private MESR() {
    }

    public static void doYourThing() {
        System.out.println("MESR did it");
    }

    public static MESR getInstance() {
        return INSTANCE;
    }

    public static String replaceString(String string, String command) {
        return "";
    }
}
