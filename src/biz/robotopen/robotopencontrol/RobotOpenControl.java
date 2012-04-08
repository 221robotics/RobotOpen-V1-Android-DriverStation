package biz.robotopen.robotopencontrol;

import java.util.Observable;
import java.util.Observer;

import robotopencontrol.instance.ROJoystick;
import robotopencontrol.instance.ROVirtualJoystick;

import com.MobileAnarchy.Android.Widgets.Joystick.DualJoystickView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class RobotOpenControl extends Activity implements Observer {
    
    private SharedPreferences preferences;
    private RORobotInstance robotInstance;
    private ROJoystick joystickHandler;
	private DualJoystickView joystick;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
    }
    
    /* Call this whenever the network settings need to be reloaded */
    public void updateNetworking() {
    	try {
    		robotInstance.getPacketTransmitter().setRemoteTarget(preferences.getString("ipaddress", "192.168.1.22"), Integer.parseInt(preferences.getString("txinterval", "25")));
    	} catch (Exception e) {
    		// Networking exception
    	}
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) 
		{
		    case R.id.preferences:
		    	startActivity(new Intent(this, Preferences.class));
		    	break;
		    case R.id.usbmode:
		    	startActivity(new Intent(this, USBJoystickActivity.class));
		    	break;
		    default:
		    	break;
		}
		
		return true;
	}
	
	@Override
    protected void onResume() {
    	super.onResume();
    	
    	// Initialize preferences
    	preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	        
    	// Setup the RobotOpen instance
    	joystick = (DualJoystickView)findViewById(R.id.dualjoystickView);
    	joystickHandler = new ROVirtualJoystick(joystick);
    	robotInstance = new RORobotInstance(joystickHandler);
		robotInstance.getDashboardData().addObserver(this);
    	
    	// Update networking settings
    	updateNetworking();
    }
    
    @Override
    protected void onPause() {
    	try {
    		// Terminate the instance
    		robotInstance.getDashboardData().deleteObserver(this);
    		robotInstance.getPacketTransmitter().terminate();
    	} catch (Exception e) {
    		// That's not good
    	}
    	
    	super.onPause();
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