package huu.phong.musiconline.audio;

import huu.phong.musiconline.Configure;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;



public class SaveAudioStream extends AudioStream{
	private static File audioFile;
	private OutputStream out;
	private InputStream remote;
	private RandomAccessFile in;
	private boolean buffering = false;
	private Object locked = new Object();
	private Thread buffer = null;
	
	static {
		try {
			audioFile = File.createTempFile("Music Online ", ".music");
			audioFile.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public SaveAudioStream(String link, Streaming listener) {
		streaming = listener;
		try {
			in = new RandomAccessFile(audioFile, "rw");
			out = new BufferedOutputStream(new FileOutputStream(audioFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			url = new URL(link);
			connection = url.openConnection();
			connection.addRequestProperty("User-Agent", Configure.getInstance().userAgent);
			length = connection.getContentLength();
			in.setLength(length);
			remote = connection.getInputStream();
			buffer = new Thread(){
	        	public void run(){
	        		byte[] bytes = new byte[8096];
	        		buffering = true;
	    			while (true){
    					try {
							if (offset < length && remote != null && (reading = remote.read(bytes)) >= 0){
								out.write(bytes, 0, reading);
								out.flush();
								offset += reading;
								streaming.buffering(offset);
								synchronized (buffer) {
									buffer.notifyAll();
								}
							}else{
								try{
									if (remote != null) remote.close();
							        if(++numberReconnect <= totalNumberConnect && offset < length){
							        	connection = url.openConnection();
										connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.43 Safari/537.31");
							        	connection.setRequestProperty("Accept-Ranges", "bytes");
										connection.setRequestProperty("Range", "bytes=" + offset + "-");
										connection.connect();
										remote = connection.getInputStream();
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
							e.printStackTrace();
							break;
						}
	    			}
	    			try {
						if (out != null){
							out.flush();
							out.close();
						}
						if (remote != null ) remote.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	    			buffering = false;
	    			synchronized (locked) {
						locked.notifyAll();
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
		currentPosition++;
		return in.read();
	}

	@Override
	public void seek(int bytes){
		bytes = Math.min(bytes, length);
		try {
			in.seek(bytes);
			currentPosition = bytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void closeStream(){
		length = 0;
		if (buffering){
			synchronized (locked) {
				try {
					locked.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (in != null){
			try {
				in.close();
			} catch (IOException e) {
			}
			in = null;
		}
		if (out != null){
			try {
				out.close();
			} catch (IOException e) {
			}
			out = null;
		}
		if (remote != null){
			try {
				remote.close();
			} catch (IOException e) {
			}
			remote = null;
		}
		currentPosition = 0;
		offset = 0;
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
		in.seek(currentPosition);
	}

	@Override
	public void release() {
		audioFile.delete();
		audioFile = null;
	}
}
