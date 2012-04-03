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
    private ROJoystickHandler joystickHandler;
    // Should we be sending data?
    private volatile boolean active = false;
    // Feedback or actual control packet?
    private boolean enabled = false;
    // Used to send data out of
    private DatagramSocket serverSocket;
    // IP to send data to
    private InetAddress ipAddr;
    // Port to send data to
    private int port = 22211;
    // Rate at which to transmit packets (ms)
    private int packetRate = 20;
    // RODashboardData to store received data
    private RODashboardData dashboardData;
   
    public ROPacketTransmitter(ROJoystickHandler assignedJoystickHandler) {
        try {
            ipAddr = InetAddress.getByName("192.168.1.22");
        }
        catch (Exception e) {System.out.println(e.toString());}
        joystickHandler = assignedJoystickHandler;
        
        try {
            serverSocket = new DatagramSocket(port);
            serverSocket.setSoTimeout(250);
        }
        catch (Exception e) {
            active = false;
            System.out.println(e.toString());
        }
        
        start();
    }
    
    public void setJoystickHandler(ROJoystickHandler assignedJoystickHandler) {
        joystickHandler = assignedJoystickHandler;
    }
    
    public void setEnabled() {
    	enabled = true;
    }
    
    public void setDisabled() {
    	enabled = false;
    }
    
    /* Call this to start the main networking thread */
    public synchronized void beginXmit(){
        active = true;
    }
    
    /* Call this to stop the main networking thread */
    public synchronized void terminate(){
    	enabled = false;
        active = false;
    }
    
    public void setDashboardData(RODashboardData newDashboardData) {
        dashboardData = newDashboardData;
    }

    public void run() {
        while (true) {
        	
        	// Create a buffer to read datagrams into
            byte[] buffer = new byte[1500];

            // Create a packet to receive data into the buffer
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            
        	if (active) {
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
	                System.out.println(e.toString());
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
    
    public boolean currentlyTransmitting() {
        return active;
    }
}