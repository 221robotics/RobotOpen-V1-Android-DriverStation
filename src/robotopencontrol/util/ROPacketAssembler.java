package robotopencontrol.util;

import robotopencontrol.instance.ROJoystickHandler;

/* ---PACKET FORMAT---
 * Message Type (1 byte)
 * Protocol Version (1 byte)
 * Device ID (1 byte)
 * ...Bundle Payloads... (1 length byte + max of 255 payload bytes for each bundle)
 * CRC16 Checksum (2 bytes)
 */

/**
 * @author Eric Barch
 */
public class ROPacketAssembler {
    
    public static byte[] getXmitBytes(ROJoystickHandler joystickHandler, boolean feedback) {
    	byte messageType;
    	if (feedback)
    		messageType = ROMessageTypes.FEEDBACK_PACKET;
    	else
    		messageType = ROMessageTypes.CONTROL_PACKET;
    	
        byte[] header = { messageType, ROParameters.PROTOCOL_VER, ROParameters.DEVICE_ID };
        if (!feedback) {
        	byte[] joystickPayload = combineByteArrays(header, joystickHandler.exportValues());
        	byte[] checksum = CRC16.genCRC16(joystickPayload);
        	return combineByteArrays(joystickPayload, checksum);
        }
        else {
        	byte[] checksum = CRC16.genCRC16(header);
        	return combineByteArrays(header, checksum);
        }
    }
    
    private static byte[] combineByteArrays(byte[] array1, byte[] array2) {
        byte[] result = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }
}