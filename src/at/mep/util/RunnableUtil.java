package at.mep.util;

import com.mathworks.jmi.AWTUtilities;

import java.awt.*;

/** Created by Andreas Justin on 2016-09-26. */
public class RunnableUtil {
    public static void invokeInDispatchThreadIfNeeded(Runnable runnable) {
        if (EventQueue.isDispatchThread()) {
            runnable.run();
        } else {
            try {
                AWTUtilities.invokeAndWait(runnable);
            } catch (Throwable throwable) {
                System.out.println(throwable);
            }
        }
    }
}
