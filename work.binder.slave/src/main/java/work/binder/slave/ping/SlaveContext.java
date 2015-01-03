package work.binder.slave.ping;

import java.util.ArrayList;
import java.util.List;

public class SlaveContext {

    private static boolean _occupied;
    private static List<ProcessData> _processesData;

    static {

	setProcesses(new ArrayList<ProcessData>());
    }

    public static List<ProcessData> getProcesses() {
	return _processesData;
    }

    public static synchronized boolean isOccupied() {
	return _occupied;
    }

    public static synchronized void setOccupied(boolean occupied) {
	_occupied = occupied;
    }

    public static void setProcesses(List<ProcessData> processesData) {
	_processesData = processesData;
    }

}
