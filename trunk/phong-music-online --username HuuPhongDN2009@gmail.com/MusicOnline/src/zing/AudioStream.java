package zing;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class AudioStream extends InputStream{
	public static final int MP3_STREAM = 0;
	public static final int FLAC_STREAM = 1;
	public static final int APE_STREAM = 2;
	public static final String[] codes = {"ID3", "fLaC", "MAC"};

	private static byte[] bytes = new byte[60000000];
	private InputStream in;
	private int length;
	private int currentPosition;
	private int numberReconnect;
	private int totalNumberConnect = 5;
	private URLConnection connection;
	private URL url;
	private int offset = 0;
	private int numRead = -1;
	private Thread buffer;
	private Streaming streaming;
	private int markPosition;
	private int limit;
	
	public AudioStream(String link, Streaming listener) {
		streaming = listener;
		
		try {
			url = new URL(link);
			connection = url.openConnection();
			length = connection.getContentLength();
			in = connection.getInputStream();
			if (length > bytes.length){
				bytes = new byte[length];
			}
			buffer = new Thread(){
	        	public void run(){
	    			while (true){
    					try {
							if (offset < length && in != null && (numRead = in.read(bytes, offset, length - offset)) >= 0){
								offset += numRead;
								streaming.buffering(offset);
								synchronized (buffer) {
									buffer.notifyAll();
								}
							}else{
								try{
									if (in != null) in.close();
							        if(++numberReconnect <= totalNumberConnect && offset < length){
							        	connection = url.openConnection();
							        	connection.setRequestProperty("Accept-Ranges", "bytes");
										connection.setRequestProperty("Range", "bytes=" + offset + "-");
										connection.connect();
							        	in = connection.getInputStream();
							        	continue;
							        }else if (offset == length){
							        	break;
							        }else if (numberReconnect >= totalNumberConnect){
							        	break;
							        }
								}catch (IOException e) {
									e.printStackTrace();
									break;
								}
							}
						} catch (Exception e) {
//							throw new RuntimeException("Buffering error");
							break;
						}
	    			}
	        	}
	        };
	        buffer.start();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Can not get stream audio");
		}
	}

	@Override
	public int read() throws IOException {
		if (currentPosition == length) return -1;
		while (currentPosition + 1 > offset){
			synchronized (buffer) {
				try {
					buffer.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return bytes[currentPosition++];
	}
	
	@Override
	public int read(byte[] buff) throws IOException {
		return read(buff, 0, buff.length);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (currentPosition == length) return -1;
		while (!isCompleted() && currentPosition + len > offset){
			synchronized (buffer) {
				try {
					buffer.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		len = Math.min(Math.min(len, b.length), length - currentPosition);
		System.arraycopy(bytes, currentPosition, b, off, len);
		currentPosition += len;
		return len;
	}
	
	public boolean isCompleted(){
		return offset == length;
	}
	
	public int getLength(){
		return length;
	}
	
	public int getBufferingValue(){
		return offset;
	}
	
	public void seek(int bytes){
		currentPosition = Math.min(bytes, length);
	}
	
	public int getType(){
		while (offset < 4){
			synchronized (buffer) {
				try {
					buffer.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		int ret = -1;
		for (int i = 0; i < codes.length; i++){
			if (compareBytes(bytes, codes[i].getBytes())){
				ret = i;
				break;
			}
		}
		return ret;
	}
	
	private boolean compareBytes(byte[] source, byte[] code){
		boolean ret = false;
		for (int i = 0; i < code.length; i++){
			ret = code[i] == source[i];
			if (!ret) break;
		}
		return ret;
	}
	
	public void closeStream(){
		length = 0;
		if (in != null){
			try {
				in.close();
			} catch (IOException e) {
			}
		}
		in = null;
	}
	
	@Override
	public boolean markSupported() {
		return true;
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		limit = readlimit;
		markPosition = currentPosition;
	}
	
	@Override
	public synchronized void reset() throws IOException {
		if ((currentPosition - markPosition) >= limit){
			currentPosition = currentPosition - limit;
		}else{
			currentPosition = markPosition;
		}
	}
}
