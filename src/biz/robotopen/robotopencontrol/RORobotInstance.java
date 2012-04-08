package biz.robotopen.robotopencontrol;

import robotopencontrol.instance.RODashboardData;
import robotopencontrol.instance.ROJoystick;
import robotopencontrol.instance.ROPacketTransmitter;

/**
 * @author ebarch
 */
public class RORobotInstance {
    private ROJoystick joystickHandler;
    private RODashboardData dashboardData;
    private ROPacketTransmitter packetTransmitter;
    
    public RORobotInstance(ROJoystick joystick) {
        dashboardData = new RODashboardData();
        packetTransmitter = new ROPacketTransmitter(joystick);
        packetTransmitter.setDashboardData(dashboardData);
    }
    
    public ROJoystick getJoystickHandler() {
    	return joystickHandler;
    }
    
    public RODashboardData getDashboardData() {
    	return dashboardData;
    }
    
    public ROPacketTransmitter getPacketTransmitter() {
    	return packetTransmitter;
    }
}