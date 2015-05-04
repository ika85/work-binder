package work.binder.slave.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import work.binder.slave.ping.Pinger;

public class Activator implements BundleActivator {

    private static Log LOG = LogFactory.getLog(Activator.class);
    private static BundleContext context;
    private static final String MASTER_URL = "master.url";
    private static final String EMPTY_STRING = "";

    static BundleContext getContext() {
	return context;
    }

    public void start(BundleContext bundleContext) throws Exception {

	Activator.context = bundleContext;
	final String masterUrl = System.getProperty(MASTER_URL);

	if (masterUrl == null || masterUrl.equals(EMPTY_STRING)) {
	    LOG.error("master.url must be specified");
	}

	Thread thread = new Thread() {

	    @Override
	    public void run() {

		while (true) {

		    Pinger.ping(masterUrl);

		    try {
			Thread.sleep(5000);
		    } catch (InterruptedException e) {
			LOG.error(e, e);
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
