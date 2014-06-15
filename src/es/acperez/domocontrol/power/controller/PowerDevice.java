package es.acperez.domocontrol.power.controller;

public class PowerDevice {
	private byte key[];
	public byte token[];
	public byte tokenResponse[];
	public boolean status[];
	
	private int unsignedKey[];
	private int unsignedToken[];
	
    protected static final byte HELLO = 0x11;
    protected static final byte[] BYE = {0x01, 0x02, 0x03, 0x04};
	
    protected static final byte POWER_ON = 0x41;
    protected static final byte POWER_OFF = (byte)0x82;
    
    protected static final byte SWITCH_ON = 0x01;
    protected static final byte SWITCH_OFF = 0x02;
    protected static final byte SWITCH_NONE = 0x04;
	
	public PowerDevice(String password) {
		key = String.format("%-8s", password).getBytes();
		unsignedKey = byte2int(key);
	}

	public void addToken(byte[] seed) {
		token = seed;
		unsignedToken = byte2int(token);
		
		int low = ((unsignedToken[0] ^ unsignedKey[2]) * unsignedKey[0]) ^ (unsignedKey[6] | (unsignedKey[4] << 8 )) ^ unsignedToken[2];
		int high = ((unsignedToken[1] ^ unsignedKey[3]) * unsignedKey[1]) ^ (unsignedKey[7] | (unsignedKey[5] << 8)) ^ unsignedToken[3];

		tokenResponse = new byte[4];
		tokenResponse[0] = (byte) (low & 0xFF);
		tokenResponse[1] = (byte) ((low & 0xFF00) >> 8);
		tokenResponse[2] = (byte) (high & 0xFF);
		tokenResponse[3] = (byte) ((high & 0xFF00) >> 8);
	}

	public void addStatus(byte[] encryptedStatus) {
		int[] unsignedEncryptedStatus = byte2int(encryptedStatus);
		status = new boolean[encryptedStatus.length];
		
		for (int i = 0; i < encryptedStatus.length; i++) {
			byte state = (byte) ((((unsignedEncryptedStatus[i] - unsignedKey[1]) ^ unsignedKey[0]) - unsignedToken[3]) ^ unsignedToken[2] & 0xFF);
			status[status.length - 1 - i] = state == POWER_ON;
		}
	}
	
	public byte[] setStatus(int plug, boolean value) {
		byte[] ctrl = new byte[status.length];
		byte[] ctrlEncrypted = new byte[status.length];

		for (int i = 0; i < status.length; i++) {
			ctrl[i] = SWITCH_NONE;
			
			if (i == plug) {
				if (value) {
					ctrl[i] = SWITCH_ON;
				} else {
					ctrl[i] = SWITCH_OFF;
				}
			}
		}
		
		for (int i = 0; i < status.length; i++) {
			ctrlEncrypted[i] = (byte) ((((ctrl[status.length - 1 - i] ^ unsignedToken[2]) + unsignedToken[3]) ^ unsignedKey[0]) + unsignedKey[1] & 0xFF);
		}
		
		return ctrlEncrypted;
	}
	
	private int[] byte2int(byte input[]) {
		int output[] = new int[input.length];
		for (int i = 0; i < input.length; i++) {
			output[i] = input[i] & (0xff);
		}
		
		return output;
	}
}