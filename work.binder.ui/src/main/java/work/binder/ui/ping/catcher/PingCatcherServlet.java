package work.binder.ui.ping.catcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import work.binder.ui.Locations;
import work.binder.ui.UserContext;

public class PingCatcherServlet extends HttpServlet {

    private static final Log LOG = LogFactory.getLog(PingCatcherServlet.class);

    private static final long serialVersionUID = -8172228957633153144L;
    private static final String FINISHED = "Finished";
    private static final String EXECUTION = "execution";
    private static final String FORWARD_FOR = "X-FORWARDED-FOR";
    private static final String PROCESSOR_COUNT = "processorCount";
    private static final String CANCEL = "cancel";
    private static final String CANCELED = "canceled";
    private static final String ZIPPED_PACKAGES = "zippedPackages";
    private static final String CLEAR = "clear";

    @Override
    /**
     * 
     * reason for using FORWARD_FOR:
     * If the user is behind a proxy server or access your web server through a load balancer (for example, in cloud hosting),
     * the above code will get the IP address of the proxy server or load balancer server, not the original IP address of a
     * client. To solve it, you should get the IP address of the request’s HTTP header “X-Forwarded-For (XFF)“.
     * */
    public void doPost(HttpServletRequest request, HttpServletResponse response) {

	String slaveIpAddress = request.getHeader(FORWARD_FOR);
	if (slaveIpAddress == null) {
	    slaveIpAddress = request.getRemoteAddr();
	}

	@SuppressWarnings("unchecked")
	Map<String, String[]> params = request.getParameterMap();

	Boolean alreadyRequestedClearing = UserContext.getContext()
		.getIpsForJobClearing().get(slaveIpAddress);

	if (alreadyRequestedClearing != null) {
	    UserContext.getContext().getIpsForJobClearing()
		    .put(slaveIpAddress, true);

	    response.addHeader(CLEAR, CLEAR);

	} else {

	    Boolean alreadyRequestedCanceling = UserContext.getContext()
		    .getIpsForJobCanceling().get(slaveIpAddress);
	    if (alreadyRequestedCanceling != null) {

		if (!alreadyRequestedCanceling) {
		    UserContext.getContext().getIpsForJobCanceling()
			    .put(slaveIpAddress, true);

		    response.addHeader(CANCEL, null);
		}
	    } else if (UserContext.getPackagesForSending().containsKey(
		    slaveIpAddress)) {

		List<String> packageLocations = UserContext
			.getPackagesForSending().get(slaveIpAddress);
		UserContext.getPackagesForSending().remove(slaveIpAddress);

		try {

		    File zipLocation = new File(
			    Locations.UPLOAD_PACKAGE_LOCATION, ZIPPED_PACKAGES
				    + System.currentTimeMillis());
		    String zippedPackages = zipLocation.getAbsolutePath();
		    boolean done = ZipUtils.zip(packageLocations,
			    zippedPackages);
		    if (done) {
			copyOutFile(response, new File(zippedPackages));
			FileUtils.forceDelete(zipLocation);
		    } else {
			// add appropriate exception
		    }
		} catch (IOException e) {
		    LOG.error(e, e);
		}

	    } else if (UserContext.getContext().getBusyIPs()
		    .containsKey(slaveIpAddress)) {

		if (params.containsKey(EXECUTION)) {

		    String jobStatus = params.get(EXECUTION)[0];

		    if (FINISHED.equals(jobStatus)) {

			UserContext.getContext().getBusyIPs()
				.remove(slaveIpAddress);
			addAvailableIP(params, slaveIpAddress);

		    } else if (CANCELED.equals(jobStatus)) {

			UserContext.getContext().getBusyIPs()
				.remove(slaveIpAddress);
			addAvailableIP(params, slaveIpAddress);
		    }
		    // process this message

		}

	    } else {

		addAvailableIP(params, slaveIpAddress);
	    }
	}

    }

    private void addAvailableIP(Map<String, String[]> params,
	    String slaveIpAddress) {

	// TODO3
	// should add every time some slave
	// if we do that, we know that slave is available (computer
	// isn't turn of i a middle time)
	// if we don't do that we will know how long that machine waits
	// for the job(maybe we should save both info)

	int processorCount = 0;
	if (params.containsKey(PROCESSOR_COUNT)) {
	    String processorCountString = params.get(PROCESSOR_COUNT)[0];

	    processorCount = (StringUtils.isEmpty(processorCountString)) ? 0
		    : Integer.valueOf(processorCountString);
	}

	UserContext.getContext().getAvailableIPs()
		.put(slaveIpAddress, processorCount);

    }

    private void copyOutFile(HttpServletResponse response, File file)
	    throws IOException {

	OutputStream outputStream = response.getOutputStream();

	response.reset();

	outputStream = response.getOutputStream();
	response.setContentType("application/x-download");
	response.setHeader("Content-Disposition", "attachment; filename="
		+ file.getName());

	InputStream inputStream = new FileInputStream(file);
	IOUtils.copy(inputStream, outputStream);

	inputStream.close();
    }

}
