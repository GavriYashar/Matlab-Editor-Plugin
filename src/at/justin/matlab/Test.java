package at.justin.matlab;

import at.justin.matlab.fileStructure.FileStructure;
import com.mathworks.mde.desk.MLDesktop;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Gavri on 05.02.2016.
 */
public class Test {
    private static java.util.List<String> VALID_EDITOR_CLASSES = new ArrayList<String>() {{
        add("com.mathworks.widgets.desk.DTMaximizedPane");
        add("com.mathworks.widgets.desk.DTFloatingPane");
        add("com.mathworks.widgets.desk.DTTiledPane");
    }};
    public Component mainPane = null;

    public static void main(String[] args) throws InterruptedException {
//        FileStructure fileStructure = new FileStructure();
//        fileStructure.setVisible(true);

        debugRunMatlab(); // jdb -connect com.sun.jdi.SocketAttach:port=4444
        justRun();
    }

    public static void debugRunMatlab() throws InterruptedException {
        Matlab ml = Matlab.getInstance();

        justRun();
    }

    private static void justRun() throws InterruptedException {
        DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
        Date dateObj = new Date();
        while (true) {
            Thread.sleep(500);
            dateObj.setTime(System.currentTimeMillis());
            System.out.println("running " + df.format(dateObj));
        }
    }

    private void getContainer() {
        Container container = MLDesktop.getInstance().getGroupContainer("Editor");
        if (container == null) {
            System.out.println("No editor found - or floating");
            System.out.println("Init failed, abort plugin usage");
            return;
        }
        for (int i = 0; i < container.getComponentCount(); i++) {
            Component component = container.getComponent(i);
            if (VALID_EDITOR_CLASSES.contains(component.getClass().toString())) {
                mainPane = component;
                break;
            }
        }
        if (mainPane == null) return;
        if (mainPane.getClass().toString().equals(VALID_EDITOR_CLASSES.get(2))) {
            System.out.println("Floating");
        }
    }
}