package at.mep.utiltest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Created by Gavri on 2017-05-16. */
public class UtilTest {

    public static void setUpEnvironMent() {
        try {
            startMatlab();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startMatlab() throws IOException {
        /*
        https://stackoverflow.com/questions/14596599/run-command-prompt-as-administrator

        https://stackoverflow.com/questions/16316950/java-junit-how-to-test-if-an-external-program-process-application-is-running
         */

        String exeH = "G:\\Program Files\\MATLAB\\R2016b\\bin\\matlab.exe";
        String exeW = "D:\\Programme\\Matlab\\R2017a\\bin\\matlab.exe";

        // nothing happens
        String startUpCmd = "-r G:\\Program Files\\MATLAB\\R2016b\\bin\\startup.m";

        // still nothing
        String startMEP = "at.mep.Start.start('F:\\Coding\\IntelliJ\\matlab-editor-plugin\\out\\artifacts\\MEP_1_14\\CustomProps.properties', 'F:\\Coding\\IntelliJ\\matlab-editor-plugin\\out\\artifacts\\MEP_1_14\\DefaultProps.properties')";

        // works... at least *yay*
        String jdb = "-jdb";

        List<String> command = new ArrayList<>(5);
        command.add(exeH);
        command.add(startUpCmd);
        command.add(jdb);
        Process process = new ProcessBuilder(command).start();
    }
}
