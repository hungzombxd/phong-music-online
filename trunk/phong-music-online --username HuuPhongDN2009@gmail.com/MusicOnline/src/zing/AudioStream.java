package zing;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public abstract class AudioStream extends InputStream{	
	protected int length;
	protected int currentPosition;
	protected int numberReconnect;
	protected int totalNumberConnect = 5;
	protected URLConnection connection;
	protected URL url;
	protected int offset = 0;
	protected int reading = -1;
//	protected Thread buffer;
	protected Streaming streaming;
	protected int markPosition;
	protected int limit;

	public abstract int read() throws IOException;

	public boolean isCompleted(){
		return offset == length;
	}

	public int getLength(){
		return length;
	}

	public int getBufferingValue(){
		return offset;
	}

	public abstract void seek(int bytes);

	public int getType(){
		int ret = -1;
		try {
			byte[] bytes = new byte[AudioCodec.MAX_LENGTH];
			mark(AudioCodec.MAX_LENGTH);
			read(bytes, 0, AudioCodec.MAX_LENGTH);
			ret = AudioCodec.getType(bytes);
			reset();
		} catch (IOException e) {
			e.printStackTrace();
			ret = -1;
		}
		return ret;
	}

	public abstract void closeStream();

	public boolean markSupported(){
		return true;
	}

	public abstract void mark(int readlimit);

	public abstract void reset() throws IOException;

	public abstract void release();
	
	public int getCurrentPosition(){
		return currentPosition;
	}

}