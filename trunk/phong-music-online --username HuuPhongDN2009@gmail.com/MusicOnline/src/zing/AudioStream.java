package zing;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public abstract class AudioStream extends InputStream{

	public static final int MP3_STREAM = 0;
	public static final int FLAC_STREAM = 1;
	public static final int APE_STREAM = 2;
	public static final String[] CODECS = { "ID3", "fLaC", "MAC" };
	
	protected int length;
	protected int currentPosition;
	protected int numberReconnect;
	protected int totalNumberConnect = 5;
	protected URLConnection connection;
	protected URL url;
	protected int offset = 0;
	protected int reading = -1;
	protected Thread buffer;
	protected Streaming streaming;
	protected int markPosition;
	protected int limit;

	public abstract int read() throws IOException;

	public int read(byte[] buff) throws IOException{
		return read(buff, 0, buff.length);
	}

	public abstract int read(byte[] b, int off, int len) throws IOException;

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

	public abstract int getType();

	public abstract void closeStream();

	public boolean markSupported(){
		return true;
	}

	public abstract void mark(int readlimit);

	public abstract void reset() throws IOException;

	public abstract void release();

}