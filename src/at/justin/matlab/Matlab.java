package at.justin.matlab;

import at.justin.matlab.util.ComponentUtil;
import com.mathworks.mde.cmdwin.XCmdWndView;
import com.mathworks.mde.desk.MLDesktop;
import com.mathworks.widgets.desk.DTRootPane;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Andreas Justin on 2016 - 02 - 06.
 */
public class Matlab {
    private static Matlab INSTANCE = null;

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
                    Matlab.getInstance().setStatusMessage("MEP Connected!");

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

    public DTRootPane getRootPane() {
        return (DTRootPane) getMlDesktop().getMainFrame().getComponent(0);
    }

    public MLDesktop getMlDesktop() {
        return MLDesktop.getInstance();
    }

    public XCmdWndView getXCmdWndView() {
        return CommandWindow.getXCmdWndView();
    }

    public void setStatusMessage(String string) {
        getMlDesktop().setMatlabMessage(string);
    }

    public List<Component> getComponents(String classString) {
        return ComponentUtil.getComponents(getRootPane(), classString);
    }

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

}
