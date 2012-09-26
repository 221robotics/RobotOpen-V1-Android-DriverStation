package robotopencontrol.instance;

import android.view.KeyEvent;

/**
 * @author Eric Barch
 */
public class ROXboxJoystick implements ROJoystick {
    
    //private boolean leftActive, rightActive = false;
    private float leftX, leftY, rightX, rightY = 0;
    private byte[] exportValues;
    
    public static final int LEFT_ANALOG_BTN = 6;
    public static final int RIGHT_ANALOG_BTN = 7;
    public static final int DPAD = 8;
    public static final int BTN1 = 9;
    public static final int BTN2 = 10;
    public static final int BTN3 = 11;
    public static final int BTN4 = 12;
    public static final int BTN5 = 13;
    public static final int BTN6 = 14;
    public static final int BTN7 = 15;
    public static final int BTN8 = 16;
    public static final int BTN9 = 17;
    public static final int BTN10 = 18;
   
    // Our constructor for the ROJoystickHandler object
    public ROXboxJoystick() {
    	exportValues = new byte[19];
    	for (int i = 0; i < exportValues.length; i++) {
        	exportValues[i] = (byte)(int)0;
        }
    }
    
    public void reportEventDown(int keyCode) {
    	switch(keyCode) {
	    	case KeyEvent.KEYCODE_BUTTON_L1:
	    		exportValues[BTN5] = (byte)(int)255;
	        	break;
	    	case KeyEvent.KEYCODE_BUTTON_R1:
	    		exportValues[BTN6] = (byte)(int)255;
	        	break;
	    	case KeyEvent.KEYCODE_BUTTON_THUMBR:
	    		exportValues[RIGHT_ANALOG_BTN] = (byte)(int)255;
	        	break;
	    	case KeyEvent.KEYCODE_BUTTON_THUMBL:
	    		exportValues[LEFT_ANALOG_BTN] = (byte)(int)255;
	        	break;
	    	case KeyEvent.KEYCODE_DPAD_LEFT:
	    		exportValues[DPAD] = (byte)(int)255;
	        	break;
	        case KeyEvent.KEYCODE_DPAD_RIGHT:
	        	exportValues[DPAD] = (byte)(int)127;
	        	break;
	        case KeyEvent.KEYCODE_DPAD_UP:
	        	exportValues[DPAD] = (byte)(int)63;
	        	break;
	        case KeyEvent.KEYCODE_DPAD_DOWN:
	        	exportValues[DPAD] = (byte)(int)191;
	        	break;
	        case KeyEvent.KEYCODE_BUTTON_START:
	        	exportValues[BTN10] = (byte)(int)255;
	        	break;
	        case KeyEvent.KEYCODE_BACK:
	        	exportValues[BTN9] = (byte)(int)255;
	        	break;
	        case KeyEvent.KEYCODE_BUTTON_B:
	        	exportValues[BTN3] = (byte)(int)255;
	        	break;
	        case KeyEvent.KEYCODE_BUTTON_A:
	        	exportValues[BTN2] = (byte)(int)255;
	        	break;
	        case KeyEvent.KEYCODE_BUTTON_X:
	        	exportValues[BTN1] = (byte)(int)255;
	        	break;
	        case KeyEvent.KEYCODE_BUTTON_Y:
	        	exportValues[BTN4] = (byte)(int)255;
	        	break;
	        default:
		    	break;
    	}
    }
    
    public void reportEventUp(int keyCode) {
    	switch(keyCode) {
	    	case KeyEvent.KEYCODE_BUTTON_L1:
	    		exportValues[BTN5] = (byte)(int)0;
	        	break;
	    	case KeyEvent.KEYCODE_BUTTON_R1:
	    		exportValues[BTN6] = (byte)(int)0;
	        	break;
	    	case KeyEvent.KEYCODE_BUTTON_THUMBR:
	    		exportValues[RIGHT_ANALOG_BTN] = (byte)(int)0;
	        	break;
	    	case KeyEvent.KEYCODE_BUTTON_THUMBL:
	    		exportValues[LEFT_ANALOG_BTN] = (byte)(int)0;
	        	break;
	    	case KeyEvent.KEYCODE_DPAD_LEFT:
	    		exportValues[DPAD] = (byte)(int)0;
	        	break;
	        case KeyEvent.KEYCODE_DPAD_RIGHT:
	        	exportValues[DPAD] = (byte)(int)0;
	        	break;
	        case KeyEvent.KEYCODE_DPAD_UP:
	        	exportValues[DPAD] = (byte)(int)0;
	        	break;
	        case KeyEvent.KEYCODE_DPAD_DOWN:
	        	exportValues[DPAD] = (byte)(int)0;
	        	break;
	        case KeyEvent.KEYCODE_BUTTON_START:
	        	exportValues[BTN10] = (byte)(int)0;
	        	break;
	        case KeyEvent.KEYCODE_BACK:
	        	exportValues[BTN9] = (byte)(int)0;
	        	break;
	        case KeyEvent.KEYCODE_BUTTON_B:
	        	exportValues[BTN3] = (byte)(int)0;
	        	break;
	        case KeyEvent.KEYCODE_BUTTON_A:
	        	exportValues[BTN2] = (byte)(int)0;
	        	break;
	        case KeyEvent.KEYCODE_BUTTON_X:
	        	exportValues[BTN1] = (byte)(int)0;
	        	break;
	        case KeyEvent.KEYCODE_BUTTON_Y:
	        	exportValues[BTN4] = (byte)(int)0;
	        	break;
	        default:
		    	break;
    	}
    }
    
    public void updateLeftX(float coord) {
    	leftX = coord;
    }
    
    public void updateLeftY(float coord) {
    	leftY = coord * -1;
    }

    public void updateRightX(float coord) {
    	rightX = coord;
    }

    public void updateRightY(float coord) {
    	rightY = coord * -1;
    }
    
    public int getLeftX() {
    	return (int)mapValue(leftX, -1, 1, 0, 255);
    }
    
	public int getLeftY() {
		return (int)mapValue(leftY, -1, 1, 0, 255);
	}

	public int getRightX() {
		return (int)mapValue(rightX, -1, 1, 0, 255);
	}

	public int getRightY() {
		return (int)mapValue(rightY, -1, 1, 0, 255);
	}
    
    public void lTriggerEvent(boolean pressed) {
    	if (pressed)
    		exportValues[BTN7] = (byte)(int)255;
    	else
    		exportValues[BTN7] = (byte)(int)0;
    }
    
    public void rTriggerEvent(boolean pressed) {
    	if (pressed)
    		exportValues[BTN8] = (byte)(int)255;
    	else
    		exportValues[BTN8] = (byte)(int)0;
    }
    
    public byte[] exportValues() {
	    int currentIndex = 0;
	        
	    // Set the length of the bundle
	    exportValues[currentIndex++] = (byte)(int)18;
	            
	    // Set the bundleID
	    exportValues[currentIndex++] = (byte)(48);
	    
	    // Generate analog stick values
	    float analogVal = mapValue(leftX, -1, 1, 0, 255);
        exportValues[currentIndex++] = (byte)(int)analogVal;
        analogVal = mapValue(leftY, -1, 1, 0, 255);
        exportValues[currentIndex++] = (byte)(int)analogVal;
        analogVal = mapValue(rightX, -1, 1, 0, 255);
        exportValues[currentIndex++] = (byte)(int)analogVal;
        analogVal = mapValue(rightY, -1, 1, 0, 255);
        exportValues[currentIndex++] = (byte)(int)analogVal;

	    return exportValues;
    }
    
    private float mapValue(float input, float inMin, float inMax, float outMin, float outMax) {
        return (input - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }
    
    public boolean controllerActive() {
    	return true;
    }

	@Override
	public void setTilt(int val) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFire(boolean fire) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCrab(boolean crab) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTank(boolean tank) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setReady(boolean ready) {
		// TODO Auto-generated method stub
		
	}
}