package work.binder.ui;

import java.util.List;

public class Job {

    private String _jobPackage;
    private List<String> _ipAddresses;

    public List<String> getIpAddresses() {
	return _ipAddresses;
    }

    public String getJobPackage() {
	return _jobPackage;
    }

    public void setIpAddresses(List<String> ipAddresses) {
	_ipAddresses = ipAddresses;
    }

    public void setJobPackage(String jobPackage) {
	_jobPackage = jobPackage;
    }
}
