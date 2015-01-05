package work.binder.slave.ping;

import java.util.ArrayList;
import java.util.List;

public class SlaveContext {

    private static boolean _occupied;
    
    private static List<ProcessData> _processesData = new ArrayList<>();
    private static boolean _canceled;
    private static boolean _cleared;

    public static List<ProcessData> getProcessesData() {
	return _processesData;
    }

    public static boolean isCanceled() {
	return _canceled;
    }

    public static boolean isCleared() {
	return _cleared;
    }

    public static synchronized boolean isOccupied() {
	return _occupied;
    }
    public static void setCanceled(boolean canceled) {
	_canceled = canceled;
    }

    public static void setCleared(boolean cleared) {
	_cleared = cleared;
    }

    public static synchronized void setOccupied(boolean occupied) {
	_occupied = occupied;
    }

    public static void setProcessesData(List<ProcessData> processesData) {
	_processesData = processesData;
    }

}
