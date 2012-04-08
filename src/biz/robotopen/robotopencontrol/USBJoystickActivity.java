package biz.robotopen.robotopencontrol;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

public class USBJoystickActivity extends Activity implements Observer
{
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

        setContentView(R.layout.usb);
	}
	
	@Override
    protected void onResume() {
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
    	try {
    		// Terminate the instance
    	} catch (Exception e) {
    		// That's not good
    	}
    	
    	super.onPause();
    }
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		((TextView)findViewById(R.id.textview)).setText("Event: " + keyCode);
		return super.onKeyDown(keyCode, event);
    }
	
	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		
		((TextView)findViewById(R.id.textview)).setText("Motion: " + event.getX());
	 
	    //Log.d("Right Trigger Value", event.getAxisValue(MotionEvent.AXIS_RTRIGGER) + "");
	    //Log.d("Left Trigger Value", event.getAxisValue(MotionEvent.AXIS_LTRIGGER) + "");
	 
	    //Log.d("Left Stick X", event.getX() + "");
	    //Log.d("Left Stick Y", event.getY() + "");
	 
	    //Log.d("Right Stick Y", event.getAxisValue(MotionEvent.AXIS_RZ) + "");
	    //Log.d("Right Stick X", event.getAxisValue(MotionEvent.AXIS_Z) + ""); 
	 
	    return super.onGenericMotionEvent(event);
	}
	
	@Override
	public void update(Observable obs, Object obj) {
		// New Driver Station Packet
		// Enable to get data
		/*ArrayList<String> bundles = robotInstance.getDashboardData().getBundles();
			
		String[] dashboardData = new String[bundles.size()];
			
		for (int i = 0; i < bundles.size(); i++)
			dashboardData[i] = bundles.get(i);*/
	}
}