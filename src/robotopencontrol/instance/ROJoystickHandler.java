package robotopencontrol.instance;

import java.util.Observable;

import com.MobileAnarchy.Android.Widgets.Joystick.DualJoystickView;
import com.MobileAnarchy.Android.Widgets.Joystick.JoystickMovedListener;

/**
 * @author Eric Barch
 */
public class ROJoystickHandler extends Observable {
    
    private boolean leftActive, rightActive = false;
    private int leftX, leftY, rightX, rightY = 0;
    
    private DualJoystickView joystick;
   
    // Our constructor for the ROJoystickHandler object
    public ROJoystickHandler(DualJoystickView dualjoystick) {        
    	joystick = dualjoystick;
        joystick.setOnJostickMovedListener(_listenerLeft, _listenerRight);
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
        
        for (int i = currentIndex; i <= 18; i++) {
        	exportValues[i] = (byte)(int)0;
        }

	    return exportValues;
    }
    
    private float mapValue(float input, float inMin, float inMax, float outMin, float outMax) {
        return (input - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }
    
    public boolean controllersActive() {
    	if (leftActive && rightActive)
    		return true;
    	else
    		return false;
    }
    
    private void updateState() {
    	setChanged();
        notifyObservers();
    }
    
    private JoystickMovedListener _listenerLeft = new JoystickMovedListener() {

		@Override
		public void OnMoved(int pan, int tilt) {
			leftX = pan;
			leftY = tilt;
			leftActive = true;
			updateState();
		}

		@Override
		public void OnReleased() {
			leftActive = false;
			updateState();
		}
		
		public void OnReturnedToCenter() {
			leftActive = false;
			updateState();
		};
	};
	
    private JoystickMovedListener _listenerRight = new JoystickMovedListener() {

    	@Override
		public void OnMoved(int pan, int tilt) {
    		rightX = pan;
			rightY = tilt;
			rightActive = true;
			updateState();
		}

		@Override
		public void OnReleased() {
			rightActive = false;
			updateState();
		}
		
		public void OnReturnedToCenter() {
			rightActive = false;
			updateState();
		};
	};
}