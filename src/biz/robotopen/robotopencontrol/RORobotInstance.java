package biz.robotopen.robotopencontrol;

import com.MobileAnarchy.Android.Widgets.Joystick.DualJoystickView;

import robotopencontrol.instance.RODashboardData;
import robotopencontrol.instance.ROJoystickHandler;
import robotopencontrol.instance.ROPacketTransmitter;

/**
 * @author ebarch
 */
public class RORobotInstance {
    private ROJoystickHandler joystickHandler;
    private RODashboardData dashboardData;
    private ROPacketTransmitter packetTransmitter;
    
    public RORobotInstance(DualJoystickView dualjoystick) {
        joystickHandler = new ROJoystickHandler(dualjoystick);
        dashboardData = new RODashboardData();
        packetTransmitter = new ROPacketTransmitter(joystickHandler);
        packetTransmitter.setDashboardData(dashboardData);
    }
    
    public ROJoystickHandler getJoystickHandler() {
    	return joystickHandler;
    }
    
    public RODashboardData getDashboardData() {
    	return dashboardData;
    }
    
    public ROPacketTransmitter getPacketTransmitter() {
    	return packetTransmitter;
    }
}