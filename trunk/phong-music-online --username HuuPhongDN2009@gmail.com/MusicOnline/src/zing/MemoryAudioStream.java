package zing;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

public class MemoryAudioStream extends AudioStream{
	private  byte[] bytes = new byte[60000000];
	private InputStream in;
	
	public MemoryAudioStream(String link, Streaming listener) {
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
							if (offset < length && in != null && (reading = in.read(bytes, offset, length - offset)) >= 0){
								offset += reading;
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
		if (currentPosition >= length) return -1;
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
	public int read(byte[] b, int off, int len) throws IOException {
		if (currentPosition >= length) return -1;
		while (!isCompleted() && currentPosition + len > offset){
			synchronized (buffer) {
				try {
					buffer.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		len = Math.min(Math.min(len, b.length - off), length - currentPosition);
		System.arraycopy(bytes, currentPosition, b, off, len);
		currentPosition += len;
		return len;
	}
	
	public void seek(int bytes){
		currentPosition = Math.min(bytes, length);
	}
	
	public int getType(){
		while (offset < AudioCodec.MAX_LENGTH){
			synchronized (buffer) {
				try {
					buffer.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		byte[] bytes = Arrays.copyOfRange(this.bytes, 0, AudioCodec.MAX_LENGTH);
		return AudioCodec.getType(bytes);
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

	@Override
	public void release() {
		
	}
}
