package work.binder.ui.ping.catcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
//import org.apache.commons.io.StreamUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import work.binder.ui.UserContext;

public class PingCatcherServlet extends HttpServlet {

    private static final Log LOG = LogFactory.getLog(PingCatcherServlet.class);

    private static final long serialVersionUID = -8172228957633153144L;
    private static final String FINISHED = "Finished";
    private static final String JOB = "job";
    private static final String FORWARD_FOR = "X-FORWARDED-FOR";
    private static final String SLOT_COUNT = "slotCount";

    @Override
    /**
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

	if (UserContext.getFutureJobs().containsKey(slaveIpAddress)) {

	    String jarLocation = UserContext.getFutureJobs()
		    .get(slaveIpAddress);
	    UserContext.getContext().getAvailableIPs().remove(slaveIpAddress);
	    UserContext.getFutureJobs().remove(slaveIpAddress);
	    UserContext.getContext().getBusyIPs()
		    .put(slaveIpAddress, System.currentTimeMillis());

	    try {
		copyOutFile(response, new File(jarLocation));
	    } catch (IOException e) {
		LOG.error(e, e);
	    }

	} else if (UserContext.getContext().getBusyIPs()
		.containsKey(slaveIpAddress)) {

	    if (params.containsKey(JOB)) {

		String jobStatus = params.get(JOB)[0];

		if (FINISHED.equals(jobStatus)) {
		    // http://stackoverflow.com/questions/6873830/in-java-determine-if-a-process-created-using-runtime-environment-has-finished-ex

		}
		// process this message

	    }

	} else {
	    // TODO3
	    // should add every time some slave
	    // if we do that, we know that slave is available (computer
	    // isn't turn of i a middle time)
	    // if we don't do that we will know how long that machine waits
	    // for the job(maybe we should save both info)

	    int slotCount = 0;
	    if (params.containsKey(SLOT_COUNT)) {
		String slotCountString = params.get(SLOT_COUNT)[0];

		slotCount = (StringUtils.isEmpty(slotCountString)) ? 0
			: Integer.valueOf(slotCountString);
	    }

	    UserContext.getContext().getAvailableIPs()
		    .put(slaveIpAddress, slotCount);
	}

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

	// response.flushBuffer();

	inputStream.close();
    }

}
