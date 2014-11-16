package work.binder.slave.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import work.binder.slave.ping.Pinger;

public class Activator implements BundleActivator {

    private static final String MASTER_URL = "http://192.168.1.2:8080/work.binder.ui/ping";

    private static BundleContext context;

    static BundleContext getContext() {
	return context;
    }

    public void start(BundleContext bundleContext) throws Exception {

	Activator.context = bundleContext;

	Thread thread = new Thread() {

	    @Override
	    public void run() {

		while (true) {

		    Pinger.ping(MASTER_URL);

		    try {
			Thread.sleep(5000);
		    } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
	    }
	};

	thread.start();

    }

    public void stop(BundleContext bundleContext) throws Exception {
	Activator.context = null;
    }

}
