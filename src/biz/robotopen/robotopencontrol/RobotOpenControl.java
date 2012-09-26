package biz.robotopen.robotopencontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import robotopencontrol.instance.ROJoystick;
import robotopencontrol.instance.ROXboxJoystick;
import robotopencontrol.instance.ROVirtualJoystick;

import com.MobileAnarchy.Android.Widgets.Joystick.JoystickView;

import de.mjpegsample.MjpegView.MjpegInputStream;
import de.mjpegsample.MjpegView.MjpegView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class RobotOpenControl extends Activity implements Observer {
    
    private SharedPreferences preferences;
    private RORobotInstance robotInstance;
    private ROJoystick joystickHandler;
	private JoystickView leftJoystick;
	private JoystickView rightJoystick;
	private MjpegView mv;
	private Thread camera_thread;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> dsArrayList;
	private boolean usbJoystickMode = false;
	private TextView joyFeedback;
	private boolean camSourceSet = false;
	
	private SeekBar seekBar;
	private Button fireBtn;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        joyFeedback = (TextView)findViewById(R.id.joystickFeedback);
        
        seekBar = (SeekBar)findViewById(R.id.nerfTilt);
        
        fireBtn = (Button)findViewById(R.id.nerfbtn);
        
        // Setup DS data list view
        dsArrayList = new ArrayList<String>();
        dsArrayList.add("Waiting for DS data...");
        ListView listView = (ListView) findViewById(R.id.dsList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, dsArrayList);
        
        // Assign adapter to ListView
     	listView.setAdapter(adapter);
     	
     	// Register our receiver so that we know when a USB device is disconnected
     	IntentFilter filter = new IntentFilter();
     	filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
     	registerReceiver(mUsbReceiver, filter);
     	
     	seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				try {
					// update the tilt amount
					joystickHandler.setTilt(progress);
				} catch (Exception e) {
					// scream and die
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
			}
     		
     	});
     	
     	fireBtn.setOnTouchListener(new OnTouchListener() {
     	    @Override
     	    public boolean onTouch(View v, MotionEvent event) {
     	        if(event.getAction() == MotionEvent.ACTION_DOWN) {
     	        	try {
    					// update the tilt amount
    					joystickHandler.setFire(true);
    				} catch (Exception e) {
    					// scream and die
    				}
     	        } else if (event.getAction() == MotionEvent.ACTION_UP) {
     	        	try {
    					// update the tilt amount
    					joystickHandler.setFire(false);
    				} catch (Exception e) {
    					// scream and die
    				}
     	        }
     	        return true;
     	    }
     	});

    }
    
    /* Call this whenever the network settings need to be reloaded */
    public void updateNetworking() {
    	try {
    		robotInstance.getPacketTransmitter().setRemoteTarget(preferences.getString("ipaddress", "192.168.1.22"), Integer.parseInt(preferences.getString("txinterval", "25")));
    	} catch (Exception e) {
    		// Networking exception
    	}
    }
    
    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
        	String action = intent.getAction();

        	if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
        		UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                	if (device.getVendorId() == 1118) {
                		// Kill the robot - controller was disconnected
                		Toast.makeText(getApplicationContext(), "Controller Disconnected", Toast.LENGTH_SHORT).show();
            	        
            	        robotInstance.getPacketTransmitter().setEnabled(false);
            	        
            	        ToggleButton enableBtn = (ToggleButton)findViewById(R.id.enablebtn);
            	        ToggleButton usbBtn = (ToggleButton)findViewById(R.id.enableusb);
            	        enableBtn.setChecked(false);
            	        usbBtn.setChecked(false);
            	        
            	        usbJoystickMode = false;
            	        
            	        leftJoystick = (JoystickView)findViewById(R.id.joystickViewLeft);
            	        rightJoystick = (JoystickView)findViewById(R.id.joystickViewRight);
            	        joystickHandler = new ROVirtualJoystick(leftJoystick, rightJoystick);
            	        robotInstance.getPacketTransmitter().setJoystickHandler(joystickHandler);
                	}
                }
            }
        	
        }
    };
    
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
    		joystickHandler = new ROXboxJoystick();
    	}
    	else {
    		leftJoystick = (JoystickView)findViewById(R.id.joystickViewLeft);
    		rightJoystick = (JoystickView)findViewById(R.id.joystickViewRight);
        	joystickHandler = new ROVirtualJoystick(leftJoystick, rightJoystick);
    	}
    	
    	robotInstance = new RORobotInstance(joystickHandler);
		robotInstance.getDashboardData().addObserver(this);
    	
    	// Update networking settings
    	updateNetworking();
    	
    	// Invalidate source - may have changed
    	camSourceSet = false;
    	
        mv = (MjpegView)findViewById(R.id.camera);
        mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
        mv.showFps(true);

    }
	
	public void onCameraBtn(View v) {
	    // Perform action on clicks
	    if (((ToggleButton) v).isChecked()) {
	        Toast.makeText(this, "Camera Enabled", Toast.LENGTH_SHORT).show();
	        
	        // Camera code
	    	final String URL = preferences.getString("ipcam", "http://192.168.1.110/cgi/mjpg/mjpg.cgi");
	    	
	    	final String camuser = preferences.getString("ipcam-user", "none");
	    	final String campass = preferences.getString("ipcam-pass", "none");
	        
	        if (!camSourceSet) {
		        camera_thread = new Thread(new Runnable() {
		        	public void run() {
		        		camSourceSet = true;
		        		mv.setSource(MjpegInputStream.read(URL, camuser, campass));
		        	}
		        });
		        camera_thread.start();
		        try {
					camera_thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        }
	        else {
	        	mv.resumePlayback();
	        }
	    } else {
	        Toast.makeText(this, "Camera Disabled", Toast.LENGTH_SHORT).show();
	        
	        try {
	        	mv.stopPlayback();
			} catch (Exception e) {
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
	
	public void readyNerfBtn(View v) {
		// Perform action on clicks
	    if (((ToggleButton) v).isChecked()) {
	    	try {
				// update the tilt amount
				joystickHandler.setReady(true);
			} catch (Exception e) {
				// scream and die
			}
	    } else {
	    	try {
				// update the tilt amount
				joystickHandler.setReady(false);
			} catch (Exception e) {
				// scream and die
			}
	    }
	}
	
	public void onUSBBtn(View v) {
		// Perform action on clicks
	    if (((ToggleButton) v).isChecked()) {
	        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
	        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
	        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
	        while(deviceIterator.hasNext()){
	        	UsbDevice device = deviceIterator.next();
	        	
	        	if (device.getVendorId() == 1118) {
	        		Toast.makeText(this, "USB Joystick Enabled", Toast.LENGTH_SHORT).show();
	        		joystickHandler = new ROXboxJoystick();
		        	robotInstance.getPacketTransmitter().setJoystickHandler(joystickHandler);
		        	usbJoystickMode = true;
	        	}
	        }
	        
	        if (!usbJoystickMode) {
	        	Toast.makeText(this, "USB Joystick Not Found", Toast.LENGTH_SHORT).show();
	        	((ToggleButton) v).setChecked(false);
	        }
	        
	    } else {
	        Toast.makeText(this, "USB Joystick Disabled", Toast.LENGTH_SHORT).show();
	        usbJoystickMode = false;
	        
	        leftJoystick = (JoystickView)findViewById(R.id.joystickViewLeft);
    		rightJoystick = (JoystickView)findViewById(R.id.joystickViewRight);
        	joystickHandler = new ROVirtualJoystick(leftJoystick, rightJoystick);
	        robotInstance.getPacketTransmitter().setJoystickHandler(joystickHandler);
	    }
	}
    
    @Override
    protected void onPause() {
    	try {
    		// Terminate the instance
    		mv.stopPlayback();
    		//camera_thread.join();
    		robotInstance.getDashboardData().deleteObserver(this);
    		
    		robotInstance.getPacketTransmitter().terminate();
    		ToggleButton enableBtn = (ToggleButton)findViewById(R.id.enablebtn);
    		enableBtn.setChecked(false);
    		ToggleButton connectBtn = (ToggleButton)findViewById(R.id.connectbtn);
    		connectBtn.setChecked(false);
    		
    	} catch (Exception e) {
    		// That's not good
    	}
    	
    	super.onPause();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (!usbJoystickMode)
    		return super.onKeyDown(keyCode, event);
    	
    	ROXboxJoystick joy = (ROXboxJoystick) joystickHandler;
    	
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
    	
    	ROXboxJoystick joy = (ROXboxJoystick) joystickHandler;
    	
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
    	
    	ROXboxJoystick joy = (ROXboxJoystick) joystickHandler;
    	
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
			
		for (int i = 0; i < bundles.size(); i++) {
			dashboardData[i] = bundles.get(i);
		}
		
		runOnUiThread(new Runnable() {
		    public void run() {
		    	try {
		    		joyFeedback.setText("Left (" + joystickHandler.getLeftX() + ", " + joystickHandler.getLeftY() + ") --- Right (" + joystickHandler.getRightX() + ", " + joystickHandler.getRightY() + ")");
			    	adapter.clear();
			    	adapter.addAll(dashboardData);
			    	adapter.notifyDataSetChanged();
		    	}
		    	catch (Exception e) {
		    		// pass
		    	}
		    }
		});
	}
	
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.normalDrive:
	            if (checked) {
	            	try {
    					// update the tilt amount
    					joystickHandler.setTank(false);
    					joystickHandler.setCrab(false);
    				} catch (Exception e) {
    					// scream and die
    				}
	            }
	            break;
	        case R.id.tankDrive:
	        	if (checked) {
	            	try {
    					// update the tilt amount
    					joystickHandler.setTank(true);
    					joystickHandler.setCrab(false);
    				} catch (Exception e) {
    					// scream and die
    				}
	            }
	            break;
	        case R.id.crabDrive:
	        	if (checked) {
	            	try {
    					// update the tilt amount
    					joystickHandler.setTank(false);
    					joystickHandler.setCrab(true);
    				} catch (Exception e) {
    					// scream and die
    				}
	            }
	            break;
	    }
	}

}