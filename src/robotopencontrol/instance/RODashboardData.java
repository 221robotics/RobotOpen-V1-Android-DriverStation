package robotopencontrol.instance;

import java.util.ArrayList;
import java.util.Observable;

/**
 * @author Eric Barch
 */
public class RODashboardData extends Observable {
    // Timestamp of the last received valid packet
    private int lastPacketTime;
    // Last uptime reported
    private int lastUptime;
    // State of the robot
    private int robotState;
    // Last firmware version reported
    private int lastFirmwareVer;
    // Valid RX count
    private int validRxPackets;
    // Invalid RX count
    private int invalidRxPackets;
    // Stores all of the data received from the RC receiver
    ArrayList<String> bundles;
   
    /* Our constructor for the RODashboardData object */
    public RODashboardData() {
        lastPacketTime = -1;
        lastUptime = -1;
        robotState = -1;
        lastFirmwareVer = -1;
        validRxPackets = 0;
        invalidRxPackets = 0;
        bundles = new ArrayList<String>();
    }
    
    public void updatePacketTime(int packetTime) {
        lastPacketTime = packetTime;
    }
    
    public void updateUptime(int uptime) {
        lastUptime = uptime;
    }
    
    public void updateFirmwareVer(int firmwareVer) {
        lastFirmwareVer = firmwareVer;
    }
    
    public void updateRobotState(int currentState) {
        robotState = currentState;
    }
    
    public void incrementValidRxCount() {
        validRxPackets++;
        setChanged();
        notifyObservers();
    }
    
    public void incrementInvalidRxCount() {
        invalidRxPackets++;
        setChanged();
        notifyObservers();
    }
    
    public void updateBundles(byte[] data) {
        bundles.clear();
        
        int dIndex = 0;
        
        while (dIndex < data.length) {
        	int startIndex = dIndex+2;
        	byte bundleID = data[dIndex+1];
        	int endIndex = (int)data[dIndex] + dIndex;
        	
        	long value = 0;
        	for (int i = startIndex; i <= endIndex; i++)
        		value = (value << 8) + (data[i] & 0xff);
        	
        	bundles.add("Bundle " + (char)bundleID + ": " + String.valueOf(value));
        	
        	dIndex = endIndex + 1;
        }
    }
    
    public int getPacketTime() {
        return lastPacketTime;
    }
    
    public int getUptime() {
        return lastUptime;
    }
    
    public int getFirmwareVer() {
        return lastFirmwareVer;
    }
    
    public int getRobotState() {
        return robotState;
    }
    
    public int getValidRxCount() {
        return validRxPackets;
    }
    
    public int getInvalidRxCount() {
        return invalidRxPackets;
    }
    
    public ArrayList<String> getBundles() {
    	return bundles;
    }
}