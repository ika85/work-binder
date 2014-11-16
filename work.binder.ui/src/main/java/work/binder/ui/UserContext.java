package work.binder.ui;

import java.util.HashMap;
import java.util.Map;

public class UserContext {

    private static UserContext _userContext;
    private static Job _job = new Job();

    public static Map<String, String> getFutureJobs() {
	return _futureJobs;
    }

    private Map<String, Integer> _availableIPs = new HashMap<String, Integer>();
    private Map<String, Long> _busyIPs = new HashMap<String, Long>();
    private static Map<String, String> _futureJobs = new HashMap<String, String>();

    public static UserContext getContext() {
	if (_userContext == null) {
	    _userContext = new UserContext();
	}
	return _userContext;
    }

    public static void setFutureJobs(Map<String, String> futureJobs) {
	_futureJobs = futureJobs;
    }

    private UserContext() {

	setJob(new Job());

    }

    public Map<String, Integer> getAvailableIPs() {
	return _availableIPs;
    }

    public Map<String, Long> getBusyIPs() {
	return _busyIPs;
    }

    public Job getJob() {
	return _job;
    }

    public void setAvailableIPs(Map<String, Integer> availableIPs) {
	_availableIPs = availableIPs;
    }

    public void setBusyIPs(Map<String, Long> busyIPs) {
	_busyIPs = busyIPs;
    }

    private void setJob(Job job) {
	_job = job;
    }

}
