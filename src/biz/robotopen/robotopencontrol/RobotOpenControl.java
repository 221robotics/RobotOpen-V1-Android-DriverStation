package biz.robotopen.robotopencontrol;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import robotopencontrol.instance.ROJoystick;
import robotopencontrol.instance.ROUSBJoystick;
import robotopencontrol.instance.ROVirtualJoystick;

import com.MobileAnarchy.Android.Widgets.Joystick.DualJoystickView;

import de.mjpegsample.MjpegView.MjpegInputStream;
import de.mjpegsample.MjpegView.MjpegView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class RobotOpenControl extends Activity implements Observer {
    
    private SharedPreferences preferences;
    private RORobotInstance robotInstance;
    private ROJoystick joystickHandler;
	private DualJoystickView joystick;
	private MjpegView mv;
	private Thread camera_thread;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> dsArrayList;
	private boolean usbJoystickMode = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        // Setup DS data list view
        dsArrayList = new ArrayList<String>();
        dsArrayList.add("Waiting for DS data...");
        ListView listView = (ListView) findViewById(R.id.dsList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, dsArrayList);
        
        // Assign adapter to ListView
     	listView.setAdapter(adapter);
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
    	if (usbJoystickMode) {
    		joystickHandler = new ROUSBJoystick();
    	}
    	else {
    		joystick = (DualJoystickView)findViewById(R.id.dualjoystickView);
        	joystickHandler = new ROVirtualJoystick(joystick);
    	}
    	
    	robotInstance = new RORobotInstance(joystickHandler);
		robotInstance.getDashboardData().addObserver(this);
    	
    	// Update networking settings
    	updateNetworking();
    	
        mv = (MjpegView)findViewById(R.id.camera);
        mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
        mv.showFps(true);
    }
	
	public void onCameraBtn(View v) {
	    // Perform action on clicks
	    if (((ToggleButton) v).isChecked()) {
	        Toast.makeText(this, "Camera Enabled", Toast.LENGTH_SHORT).show();
	        
	        // Camera code
	    	final String URL = preferences.getString("ipcam", "http://192.168.1.2/video.cgi");
	        
	        camera_thread = new Thread(new Runnable() {
	        	public void run() {
	        		mv.setSource(MjpegInputStream.read(URL));
	        	}
	        });
	        camera_thread.start();
	    } else {
	        Toast.makeText(this, "Camera Disabled", Toast.LENGTH_SHORT).show();
	        
	        try {
	        	mv.stopPlayback();
				camera_thread.join();
			} catch (InterruptedException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	
	public void onConnectBtn(View v) {
		// Perform action on clicks
	    if (((ToggleButton) v).isChecked()) {
	        Toast.makeText(this, "Connecting...", Toast.LENGTH_SHORT).show();
	        
	        robotInstance.getPacketTransmitter().setConnected(true);
	    } else {
	        Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
	        
	        robotInstance.getPacketTransmitter().setConnected(false);
	    }
	}
	
	public void onEnableBtn(View v) {
		// Perform action on clicks
	    if (((ToggleButton) v).isChecked()) {
	        Toast.makeText(this, "Robot Enabled", Toast.LENGTH_SHORT).show();
	        
	        robotInstance.getPacketTransmitter().setEnabled(true);
	    } else {
	        Toast.makeText(this, "Robot Disabled", Toast.LENGTH_SHORT).show();
	        
	        robotInstance.getPacketTransmitter().setEnabled(false);
	    }
	}
	
	public void onUSBBtn(View v) {
		// Perform action on clicks
	    if (((ToggleButton) v).isChecked()) {
	        Toast.makeText(this, "USB Joystick Enabled", Toast.LENGTH_SHORT).show();
	        
	        joystickHandler = new ROUSBJoystick();
	        robotInstance.getPacketTransmitter().setJoystickHandler(joystickHandler);
	        
	        usbJoystickMode = true;
	    } else {
	        Toast.makeText(this, "USB Joystick Disabled", Toast.LENGTH_SHORT).show();
	        usbJoystickMode = false;
	        
	        joystick = (DualJoystickView)findViewById(R.id.dualjoystickView);
	        joystickHandler = new ROVirtualJoystick(joystick);
	        robotInstance.getPacketTransmitter().setJoystickHandler(joystickHandler);
	    }
	}
    
    @Override
    protected void onPause() {
    	try {
    		// Terminate the instance
    		mv.stopPlayback();
    		camera_thread.join();
    		robotInstance.getDashboardData().deleteObserver(this);
    		robotInstance.getPacketTransmitter().terminate();
    	} catch (Exception e) {
    		// That's not good
    	}
    	
    	super.onPause();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (!usbJoystickMode)
    		return super.onKeyDown(keyCode, event);
    	
    	ROUSBJoystick joy = (ROUSBJoystick) joystickHandler;
    	
    	switch(keyCode) {
        	case KeyEvent.KEYCODE_BUTTON_L1:
        		joy.reportEventDown(keyCode);
        		return true;
        	case KeyEvent.KEYCODE_BUTTON_R1:
        		joy.reportEventDown(keyCode);
        		return true;
        	case KeyEvent.KEYCODE_BUTTON_THUMBR:
        		joy.reportEventDown(keyCode);
        		return true;
        	case KeyEvent.KEYCODE_BUTTON_THUMBL:
        		joy.reportEventDown(keyCode);
        		return true;
        	case KeyEvent.KEYCODE_DPAD_LEFT:
        		joy.reportEventDown(keyCode);
        		return true;
	        case KeyEvent.KEYCODE_DPAD_RIGHT:
	        	joy.reportEventDown(keyCode);
	        	return true;
	        case KeyEvent.KEYCODE_DPAD_UP:
	        	joy.reportEventDown(keyCode);
	        	return true;
	        case KeyEvent.KEYCODE_DPAD_DOWN:
	        	joy.reportEventDown(keyCode);
	        	return true;
	        case KeyEvent.KEYCODE_BUTTON_START:
	        	joy.reportEventDown(keyCode);
	        	return true;
	        case KeyEvent.KEYCODE_BACK:
	        	joy.reportEventDown(keyCode);
	        	return true;
	        case KeyEvent.KEYCODE_BUTTON_B:
	        	joy.reportEventDown(keyCode);
	        	return true;
	        case KeyEvent.KEYCODE_BUTTON_A:
	        	joy.reportEventDown(keyCode);
	        	return true;
	        case KeyEvent.KEYCODE_BUTTON_X:
	        	joy.reportEventDown(keyCode);
	        	return true;
	        case KeyEvent.KEYCODE_BUTTON_Y:
	        	joy.reportEventDown(keyCode);
	        	return true;
	        default:
	        	return super.onKeyDown(keyCode, event);
    	}

    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	if (!usbJoystickMode)
    		return super.onKeyDown(keyCode, event);
    	
    	ROUSBJoystick joy = (ROUSBJoystick) joystickHandler;
    	
    	switch(keyCode) {
        	case KeyEvent.KEYCODE_BUTTON_L1:
        		joy.reportEventUp(keyCode);
        		return true;
        	case KeyEvent.KEYCODE_BUTTON_R1:
        		joy.reportEventUp(keyCode);
        		return true;
        	case KeyEvent.KEYCODE_BUTTON_THUMBR:
        		joy.reportEventUp(keyCode);
        		return true;
        	case KeyEvent.KEYCODE_BUTTON_THUMBL:
        		joy.reportEventUp(keyCode);
        		return true;
        	case KeyEvent.KEYCODE_DPAD_LEFT:
        		joy.reportEventUp(keyCode);
        		return true;
	        case KeyEvent.KEYCODE_DPAD_RIGHT:
	        	joy.reportEventUp(keyCode);
	        	return true;
	        case KeyEvent.KEYCODE_DPAD_UP:
	        	joy.reportEventUp(keyCode);
	        	return true;
	        case KeyEvent.KEYCODE_DPAD_DOWN:
	        	joy.reportEventUp(keyCode);
	        	return true;
	        case KeyEvent.KEYCODE_BUTTON_START:
	        	joy.reportEventUp(keyCode);
	        	return true;
	        case KeyEvent.KEYCODE_BACK:
	        	joy.reportEventUp(keyCode);
	        	return true;
	        case KeyEvent.KEYCODE_BUTTON_B:
	        	joy.reportEventUp(keyCode);
	        	return true;
	        case KeyEvent.KEYCODE_BUTTON_A:
	        	joy.reportEventUp(keyCode);
	        	return true;
	        case KeyEvent.KEYCODE_BUTTON_X:
	        	joy.reportEventUp(keyCode);
	        	return true;
	        case KeyEvent.KEYCODE_BUTTON_Y:
	        	joy.reportEventUp(keyCode);
	        	return true;
	        default:
	        	return super.onKeyDown(keyCode, event);
    	}

    }
	
	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		if (!usbJoystickMode)
			return super.onGenericMotionEvent(event);
    	
    	ROUSBJoystick joy = (ROUSBJoystick) joystickHandler;
    	
    	// Update Triggers
    	if (event.getAxisValue(MotionEvent.AXIS_RTRIGGER) > 0.5)
    		joy.rTriggerEvent(true);
    	else
    		joy.rTriggerEvent(false);
    	if (event.getAxisValue(MotionEvent.AXIS_LTRIGGER) > 0.5)
    		joy.lTriggerEvent(true);
    	else
    		joy.lTriggerEvent(false);
    	
    	// Update left stick
    	joy.updateLeftX(event.getX());
    	joy.updateLeftY(event.getY());
    	
    	// Update right stick
    	joy.updateRightX(event.getAxisValue(MotionEvent.AXIS_Z));
    	joy.updateRightY(event.getAxisValue(MotionEvent.AXIS_RZ));
	 
	    return super.onGenericMotionEvent(event);
	}

	@Override
	public void update(Observable obs, Object obj) {
		// New Driver Station Packet
		// Enable to get data
		ArrayList<String> bundles = robotInstance.getDashboardData().getBundles();
			
		final String[] dashboardData = new String[bundles.size()];
			
		for (int i = 0; i < bundles.size(); i++)
			dashboardData[i] = bundles.get(i);
		
		runOnUiThread(new Runnable() {
		    public void run() {
		    	adapter.clear();
		    	adapter.addAll(dashboardData);
		    	adapter.notifyDataSetChanged();
		    }
		});
	}

}