package at.justin.matlab;

import com.mathworks.mde.desk.MLDesktop;
import com.mathworks.widgets.desk.DTRootPane;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;

import java.awt.*;
import java.util.ArrayList;
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
                .build();
        MatlabProxyFactory factory = new MatlabProxyFactory(options);

        try {
            //Request a proxy
            factory.requestProxy(new MatlabProxyFactory.RequestCallback() {
                @Override
                public void proxyCreated(final MatlabProxy proxy) {
                    proxyHolder.set(proxy);
                    Matlab.getInstance().setStatusMessage("Connected!");

                    proxy.addDisconnectionListener(new MatlabProxy.DisconnectionListener() {
                        @Override
                        public void proxyDisconnected(MatlabProxy proxy) {
                            Matlab.getInstance().setStatusMessage("Disconnected!");
                            proxyHolder.set(null);
                        }
                    });
                }
            });
        }
        catch(MatlabConnectionException ignored) { }
    }

    public List<Component> getAllComponents() {
        return getAllComponents(getRootPane());
    }

    public DTRootPane getRootPane() {
        return (DTRootPane) getMlDesktop().getMainFrame().getComponent(0);
    }

    public MLDesktop getMlDesktop(){
        return MLDesktop.getInstance();
    }

    public void setStatusMessage(String string) {
        getMlDesktop().setMatlabMessage(string);
    }

    public List<Component> getComponents(String classString) {
        List<Component> list = new ArrayList<>();
        for (Component component : getAllComponents()) {
            if (component.getClass().toString().endsWith(classString)) {
                list.add(component);
            }
        }
        return list;
    }

    public void showClientTitles() {
        for (String str : getInstance().getMlDesktop().getClientTitles()){
            System.out.println("+----------------------------------------------------------------+");
            System.out.println(str);
            try {
                System.out.println(getInstance().getMlDesktop().getClient(str).getClass().toString());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private List<Component> getAllComponents(final Container container) {
        Component[] comps = container.getComponents();
        List<Component> compList = new ArrayList<>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container){
                compList.addAll(getAllComponents((Container) comp));
            }
        }
        return compList;
    }
}
