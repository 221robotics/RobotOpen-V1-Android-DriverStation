package robotopencontrol.instance;

/**
 * @author Eric Barch
 */
public class ROUSBJoystick implements ROJoystick {
    
    //private boolean leftActive, rightActive = false;
    private int leftX, leftY, rightX, rightY = 0;
   
    // Our constructor for the ROJoystickHandler object
    public ROUSBJoystick() {        
    	//joystick = dualjoystick;
        //joystick.setOnJostickMovedListener(_listenerLeft, _listenerRight);
    }
    
    public byte[] exportValues() {
    	byte[] exportValues = new byte[19];
	    int currentIndex = 0;
	        
	    // Set the length of the bundle
	    exportValues[currentIndex++] = (byte)(int)18;
	            
	    // Set the bundleID
	    exportValues[currentIndex++] = (byte)(48);
	    
	    // Generate analog stick values
	    float analogVal = mapValue(leftX, -150, 150, 0, 255);
        exportValues[currentIndex++] = (byte)(int)analogVal;
        analogVal = mapValue(leftY, -150, 150, 0, 255);
        exportValues[currentIndex++] = (byte)(int)analogVal;
        analogVal = mapValue(rightX, -150, 150, 0, 255);
        exportValues[currentIndex++] = (byte)(int)analogVal;
        analogVal = mapValue(rightY, -150, 150, 0, 255);
        exportValues[currentIndex++] = (byte)(int)analogVal;
        
        // Mark the rest of the controls as false
        for (int i = currentIndex; i <= 18; i++) {
        	exportValues[i] = (byte)(int)0;
        }

	    return exportValues;
    }
    
    private float mapValue(float input, float inMin, float inMax, float outMin, float outMax) {
        return (input - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }
    
    public boolean controllerActive() {
    	return true;
    }
}