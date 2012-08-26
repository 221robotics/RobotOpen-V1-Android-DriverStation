package robotopencontrol.instance;

public interface ROJoystick {
	public boolean controllerActive();
	
	public byte[] exportValues();
	
	public int getLeftX();
	public int getLeftY();
	public int getRightX();
	public int getRightY();
	
	public void setTilt(int val);
	public void setFire(boolean fire);
}
