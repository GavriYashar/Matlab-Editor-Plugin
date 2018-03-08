package at.mep;

import at.mep.installer.Install;
import at.mep.path.MPath;
import at.mep.util.ComponentUtil;
import com.mathworks.fileutils.MatlabPath;
import com.mathworks.jmi.NativeMatlab;
import com.mathworks.mde.cmdwin.XCmdWndView;
import com.mathworks.mde.desk.MLDesktop;
import com.mathworks.widgets.desk.DTRootPane;
import matlabcontrol.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Created by Andreas Justin on 2016-09-19. */
public class Matlab {
    public static final double R2014a = 8.3;
    public static final double R2014b = 8.4;
    public static final double R2015a = 8.5;
    public static final double R2015b = 8.6;
    public static final double R2016a = 9.0;
    public static final double R2016b = 9.1;
    public static final double R2017a = 9.2;
    public static final double R2017b = 9.3;
    public static final double R2018a = 9.4;

    private static Matlab INSTANCE = null;

    /** has this format: 9.1.0.441655 (R2016b) */
    private static String verString = "";

    /** from verString (9.1.0.441655 (R2016b)) 9.1*/
    private static double verNumber = 0;

    /**
     * can't be used in a Document Event.
     * When executed while an document event has been notified matconsolectl enters an endless loop
     */
    public final AtomicReference<MatlabProxy> proxyHolder = new AtomicReference<>();

    public static Matlab getInstance() {
        if (INSTANCE != null) return INSTANCE;
        INSTANCE = new Matlab();
        INSTANCE.connectToMatlab();
        return INSTANCE;
    }

    private void connectToMatlab() {
        //Create proxy factory
        MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder()
                .setUsePreviouslyControlledSession(true)
                // .setJavaDebugger(4444)
                .build();
        MatlabProxyFactory factory = new MatlabProxyFactory(options);

        try {
            //Request a proxy
            factory.requestProxy(new MatlabProxyFactory.RequestCallback() {
                @Override
                public void proxyCreated(final MatlabProxy proxy) {
                    proxyHolder.set(proxy);
                    try {
                        Matlab.getInstance().setStatusMessage("MEP Connected! Version: " + Install.getVersion());
                    } catch (IOException e) {
                        Matlab.getInstance().setStatusMessage("MEP Connected, but something went very, very, very wrong");
                    }

                    proxy.addDisconnectionListener(new MatlabProxy.DisconnectionListener() {
                        @Override
                        public void proxyDisconnected(MatlabProxy proxy) {
                            Matlab.getInstance().setStatusMessage("MEP Disconnected!");
                            proxyHolder.set(null);
                        }
                    });
                }
            });
        } catch (MatlabConnectionException ignored) {
        }
    }

    @SuppressWarnings("WeakerAccess")
    public DTRootPane getRootPane() {
        return (DTRootPane) getMlDesktop().getMainFrame().getComponent(0);
    }

    public MLDesktop getMlDesktop() {
        return MLDesktop.getInstance();
    }

    @SuppressWarnings("unused")
    public XCmdWndView getXCmdWndView() {
        return CommandWindow.getXCmdWndView();
    }

    @SuppressWarnings("WeakerAccess")
    public void setStatusMessage(String string) {
        getMlDesktop().setMatlabMessage(string);
    }

    @SuppressWarnings("unused")
    public List<Component> getComponents(String classString) {
        return ComponentUtil.getComponents(getRootPane(), classString);
    }

    @SuppressWarnings("unused")
    public void showClientTitles() {
        for (String str : getInstance().getMlDesktop().getClientTitles()) {
            System.out.println("+----------------------------------------------------------------+");
            System.out.println(str);
            try {
                System.out.println(getInstance().getMlDesktop().getClient(str).getClass().toString());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static String getMatlabVersion() {
        return NativeMatlab.GetMatlabVersion();
    }

    @SuppressWarnings("unused")
    public static String getVerString() {
        if (verString.length() == 0) verString = getMatlabVersion();
        return verString;
    }

    @SuppressWarnings("unused")
    public static double getVerNumber() {
        if (verNumber == 0) {
            Pattern verPattern = Pattern.compile("\\d+\\.\\d+");
            Matcher verMatcher = verPattern.matcher(getVerString());
            verMatcher.find();
            int s = verMatcher.start();
            int e = verMatcher.end();
            verNumber = Double.parseDouble(verString.substring(s, e));
        }
        return verNumber;
    }

    @SuppressWarnings("unused")
    public static boolean verLessThan(double ver) {
        return getVerNumber() < ver;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isBusy() {
        String string = getMlDesktop().getMainFrame().getStatusBar().getText();
        return string != null && string.contains("Busy");
    }

    public static List<File> which(String item) throws MatlabInvocationException {
        return MPath.getInstance().which(item);
    }

    public static List<String> whichString(String item) throws MatlabInvocationException {
        List<String> strings = new ArrayList<>(1);
        List<File> files = MPath.getInstance().which(item);
        for (File file : files) {
            strings.add(file.getAbsolutePath());
        }
        return strings;
    }

    public static List<MatlabPath.PathEntry> path() {
        return com.mathworks.fileutils.MatlabPath.getPathEntries();
    }

}
