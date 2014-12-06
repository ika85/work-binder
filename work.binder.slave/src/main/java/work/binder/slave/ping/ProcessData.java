package work.binder.slave.ping;

public class ProcessData {

    private Process _process;
    private String _serviceName;

    public Process getProcess() {
	return _process;
    }

    public String getServiceName() {
	return _serviceName;
    }

    public void setProcess(Process process) {
	_process = process;
    }

    public void setServiceName(String serviceName) {
	_serviceName = serviceName;
    }

}
