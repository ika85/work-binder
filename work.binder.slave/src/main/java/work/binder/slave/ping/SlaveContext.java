package work.binder.slave.ping;

import java.util.ArrayList;
import java.util.List;

public class SlaveContext {

    private static boolean _occupied;
    private static boolean _finished;
    private static List<ProcessData> _processesData;

    static {

	setProcesses(new ArrayList<ProcessData>());
    }

    public static List<ProcessData> getProcesses() {
	return _processesData;
    }

    public static boolean isFinished() {
	return _finished;
    }

    public static synchronized boolean isOccupied() {
	return _occupied;
    }

    public static void setFinished(boolean finished) {
	_finished = finished;
    }

    public static synchronized void setOccupied(boolean occupied) {
	_occupied = occupied;
    }

    public static void setProcesses(List<ProcessData> processesData) {
	_processesData = processesData;
    }

}
