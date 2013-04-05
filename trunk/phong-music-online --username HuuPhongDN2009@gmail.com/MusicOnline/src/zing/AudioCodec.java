package zing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class AudioCodec {
	
	public static final int[][] MP3_FORMAT = {{0x49, 0x44, 0x33}, {0xFF, 0xFB}};
	public static final int MP3_STREAM = 0;
	
	public static final int[][] FLAC_FORMAT = {{0x66, 0x4C, 0x61, 0x43}};
	public static final int FLAC_STREAM = 1;
	
	public static final int[][] APE_FORMAT = {{0x4D, 0x41, 0x43}};
	public static final int APE_STREAM = 2;
	
	public static final int UNKNOW_STREAM = -1;
	
	public static final int MAX_LENGTH = 4;
	
	private static final Map<Integer, int[][]> types = new HashMap<Integer, int[][]>();
	static{
		types.put(MP3_STREAM, MP3_FORMAT);
		types.put(FLAC_STREAM, FLAC_FORMAT);
		types.put(APE_STREAM, APE_FORMAT);
	}
	
	public static int getType(byte[] bytes){
		for (Iterator<Integer> i = types.keySet().iterator(); i.hasNext();) {
			int key = i.next();
			int[][] codecs = types.get(key);
			for (int j = 0; j < codecs.length; j++){
				if (match(bytes, codecs[j])){
					return key;
				}
			}
		}
		return UNKNOW_STREAM;
	}
	
	private static boolean match(byte[] bytes, int[] codec){
		for (int i = 0; i < codec.length; i++){
			if (codec[i] != (0xFF & bytes[i])){
				return false;
			}
		}
		return true;
	}
}
