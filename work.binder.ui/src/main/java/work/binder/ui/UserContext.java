package work.binder.ui;

import java.util.HashMap;
import java.util.List;
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

    public static Map<String, List<String>> getPackagesForSending() {
	return _packagesForSending;
    }

    public static void setPackagesForSending(
	    Map<String, List<String>> packagesForSending) {
	_packagesForSending = packagesForSending;
    }

    private Map<String, Integer> _availableIPs = new HashMap<String, Integer>();
    private Map<String, List<String>> _busyIPs = new HashMap<String, List<String>>();
    private static Map<String, List<String>> _packagesForSending = new HashMap<String, List<String>>();
    // IP + is request for canceling sent?
    private Map<String, Boolean> _ipsForJobCanceling = new HashMap<String, Boolean>();
    private Map<String, Boolean> _ipsForJobClearing = new HashMap<String, Boolean>();

    private UserContext() {

	setJob(new Package());

    }

    public void addIPForCanceling(String ip) {
	getIpsForJobCanceling().put(ip, false);
    }

    public void addIPForClearing(String ip) {
	getIpsForJobClearing().put(ip, false);
    }

    public Map<String, Integer> getAvailableIPs() {
	return _availableIPs;
    }

    public Map<String, List<String>> getBusyIPs() {
	return _busyIPs;
    }

    public Map<String, Boolean> getIpsForJobCanceling() {
	return _ipsForJobCanceling;
    }

    public Map<String, Boolean> getIpsForJobClearing() {
	return _ipsForJobClearing;
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

    public void setIpsForJobClearing(Map<String, Boolean> ipsForJobClearing) {
	_ipsForJobClearing = ipsForJobClearing;
    }

    private void setJob(Package job) {
	_job = job;
    }

}
