package robotopencontrol.instance;

import robotopencontrol.util.ROPacketAssembler;
import robotopencontrol.util.ROPacketParser;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author Eric Barch
 */
public class ROPacketTransmitter extends Thread {
    private ROJoystick joystickHandler;
    // Used to send data out of
    private DatagramSocket serverSocket;
    // Should we be transmitting packets?
    private boolean connected = false;
    // Send control packets
    private boolean enabled = false;
    // The condition under which our thread continues to run
    private boolean active = true;
    // IP to send data to
    private InetAddress ipAddr;
    // Port to send data to
    private int port = 22211;
    // Rate at which to transmit packets (ms)
    private int packetRate = 25;
    // RODashboardData to store received data
    private RODashboardData dashboardData;
   
    public ROPacketTransmitter(ROJoystick assignedJoystickHandler) {
        try {
            ipAddr = InetAddress.getByName("192.168.1.22");
        }
        catch (Exception e) {System.out.println(e.toString());}
        joystickHandler = assignedJoystickHandler;
        
        try {
            serverSocket = new DatagramSocket(port);
            serverSocket.setSoTimeout(250);
            start();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }    
    }
    
    public void setJoystickHandler(ROJoystick assignedJoystickHandler) {
        joystickHandler = assignedJoystickHandler;
    }
    
    public void setConnected(boolean state) {
    	connected = state;
    }
    
    public void setEnabled(boolean state) {
    	enabled = state;
    }
    
    /* Call this to stop the main networking thread */
    public synchronized void terminate(){
        active = false;
        try {
        	serverSocket.close();
        } catch (Exception e) {
        	System.out.println(e.toString());
        }
    }
    
    public void setDashboardData(RODashboardData newDashboardData) {
        dashboardData = newDashboardData;
    }

    public void run() {
        while (active) {
            
        	if (connected) {
        		
        		// Create a buffer to read datagrams into
                byte[] buffer = new byte[256];

                // Create a packet to receive data into the buffer
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        		
	            try {
	                byte[] outgoingPacket = ROPacketAssembler.getXmitBytes(joystickHandler, !enabled);
	                
	                /* BEGIN OUTPUT DEBUG */
	                /*StringBuffer hexString = new StringBuffer();
	                for (int i=0;i<outgoingPacket.length;i++) {
	                    String hex = Integer.toHexString(0xFF & outgoingPacket[i]);
	                    if (hex.length() == 1)
	                        hexString.append('0');
	                    hexString.append(hex).append(" ");
	                }
	                System.out.println(hexString);*/
	                /* END OUTPUT DEBUG */
	                
	                DatagramPacket sendPacket = new DatagramPacket(outgoingPacket, outgoingPacket.length, ipAddr, port);
	                serverSocket.send(sendPacket);
	                
	                try {
	                	serverSocket.receive(packet);
	                	
	                	// Process packet
	                    ROPacketParser.parsePacket(buffer, packet.getLength(), dashboardData);
	                }
	                catch (Exception SocketTimeoutException) {
	                	// Didn't get a packet back
	                }

                    // Reset the length of the packet
                    packet.setLength(buffer.length);
	                
	                try {
	                    Thread.sleep(packetRate);
	                }
	                catch (InterruptedException e) {
	                    active = false;
	                }
	            }
	            catch (Exception e) {
	                active = false;
	                e.printStackTrace();
	            }
        	}
        	else {
        		try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        }
    }
    
    public void setRemoteTarget(String remoteIP, int targetPacketRate) {
        try {
            ipAddr = InetAddress.getByName(remoteIP);
            packetRate = targetPacketRate;
        }
        catch (Exception e) {System.out.println(e.toString());}
    }
}