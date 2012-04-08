package robotopencontrol.instance;

public interface ROJoystick {
	public boolean controllerActive();
	
	public byte[] exportValues();
}
