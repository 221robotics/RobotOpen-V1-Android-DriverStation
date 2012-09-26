package robotopencontrol.instance;

import com.MobileAnarchy.Android.Widgets.Joystick.JoystickView;
import com.MobileAnarchy.Android.Widgets.Joystick.JoystickMovedListener;

/**
 * @author Eric Barch
 */
public class ROVirtualJoystick implements ROJoystick {
    
    private boolean leftActive, rightActive = false;
    private int leftX, leftY, rightX, rightY = 0;
    
    private JoystickView joystickLeft;
    private JoystickView joystickRight;
    private int tiltAmount = 50;
    private boolean readyNerf = false;
    private boolean firingNerf = false;
    private boolean crabDrive = false;
    private boolean tankDrive = false;
   
    // Our constructor for the ROJoystickHandler object
    public ROVirtualJoystick(JoystickView leftJoystick, JoystickView rightJoystick) {        
    	joystickLeft = leftJoystick;
    	joystickRight = rightJoystick;
        joystickLeft.setOnJostickMovedListener(_listenerLeft);
        joystickRight.setOnJostickMovedListener(_listenerRight);
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
        
        // we will exploit the D-Pad byte to transmit some analog data
        exportValues[8] = (byte)(int)mapValue(tiltAmount, 0, 100, 0, 255);
        
        // set btn3 in robotopen on or off based on the button state
        if (readyNerf)
        	exportValues[11] = (byte)255;
        else
        	exportValues[11] = 0;
        
        // set btn6 in robotopen on or off based on the button state
        if (firingNerf)
        	exportValues[14] = (byte)255;
        else
        	exportValues[14] = 0;
        
        // set btn7 in robotopen on or off based on the button state
        if (tankDrive)
        	exportValues[15] = (byte)255;
        else
        	exportValues[15] = 0;
        
        // set btn8 in robotopen on or off based on the button state
        if (crabDrive)
        	exportValues[16] = (byte)255;
        else
        	exportValues[16] = 0;

	    return exportValues;
    }
    
    public int getLeftX() {
    	return (int)mapValue(leftX, -150, 150, 0, 255);
    }
    
	public int getLeftY() {
		return (int)mapValue(leftY, -150, 150, 0, 255);
	}

	public int getRightX() {
		return (int)mapValue(rightX, -150, 150, 0, 255);
	}

	public int getRightY() {
		return (int)mapValue(rightY, -150, 150, 0, 255);
	}
    
    private float mapValue(float input, float inMin, float inMax, float outMin, float outMax) {
        return (input - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }
    
    public boolean controllerActive() {
    	if (leftActive || rightActive)
    		return true;
    	else
    		return false;
    }
    
    private JoystickMovedListener _listenerLeft = new JoystickMovedListener() {
		@Override
		public void OnMoved(int pan, int tilt) {
			leftX = pan;
			leftY = tilt;
			leftActive = true;
		}

		@Override
		public void OnReleased() {
			leftActive = false;
		}
		
		public void OnReturnedToCenter() {
			leftActive = false;
		};
	};
	
    private JoystickMovedListener _listenerRight = new JoystickMovedListener() {
    	@Override
		public void OnMoved(int pan, int tilt) {
    		rightX = pan;
			rightY = tilt;
			rightActive = true;
		}

		@Override
		public void OnReleased() {
			rightActive = false;
		}
		
		public void OnReturnedToCenter() {
			rightActive = false;
		};
	};

	@Override
	public void setTilt(int val) {
		tiltAmount = val;
	}
	
	@Override
	public void setCrab(boolean crab) {
		if (crab)
			crabDrive = true;
		else
			crabDrive = false;
	}
	
	@Override
	public void setTank(boolean tank) {
		if (tank)
			tankDrive = true;
		else
			tankDrive = false;
	}
	
	@Override
	public void setReady(boolean ready) {
		if (ready)
			readyNerf = true;
		else
			readyNerf = false;
	}

	@Override
	public void setFire(boolean fire) {
		if (fire)
			firingNerf = true;
		else
			firingNerf = false;
	}
}