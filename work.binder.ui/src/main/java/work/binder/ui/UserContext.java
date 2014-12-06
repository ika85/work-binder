package work.binder.ui;

import java.util.HashMap;
import java.util.Map;

public class UserContext {

    private static UserContext _userContext;
    private static Package _job = new Package();

    public static UserContext getContext() {
	if (_userContext == null) {
	    _userContext = new UserContext();
	}
	return _userContext;
    }

    public static Map<String, String> getPackagesForSending() {
	return _packagesForSending;
    }

    public static void setPackagesForSending(
	    Map<String, String> packagesForSending) {
	_packagesForSending = packagesForSending;
    }

    private Map<String, Integer> _availableIPs = new HashMap<String, Integer>();
    private Map<String, String> _busyIPs = new HashMap<String, String>();

    private static Map<String, String> _packagesForSending = new HashMap<String, String>();

    // IP + is request for canceling sent?
    private Map<String, Boolean> _ipsForJobCanceling = new HashMap<String, Boolean>();

    private UserContext() {

	setJob(new Package());

    }

    public void addIPForCanceling(String ip) {
	getIpsForJobCanceling().put(ip, false);
    }

    public Map<String, Integer> getAvailableIPs() {
	return _availableIPs;
    }

    public Map<String, String> getBusyIPs() {
	return _busyIPs;
    }

    public Map<String, Boolean> getIpsForJobCanceling() {
	return _ipsForJobCanceling;
    }

    public Package getJob() {
	return _job;
    }

    public void setAvailableIPs(Map<String, Integer> availableIPs) {
	_availableIPs = availableIPs;
    }

    public void setIpsForJobCanceling(Map<String, Boolean> ipsForJobCanceling) {
	_ipsForJobCanceling = ipsForJobCanceling;
    }

    private void setJob(Package job) {
	_job = job;
    }

}
