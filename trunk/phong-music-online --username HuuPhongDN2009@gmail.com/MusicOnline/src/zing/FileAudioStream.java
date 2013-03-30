package zing;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;

public class FileAudioStream extends AudioStream{
	private static File audioFile;
	private OutputStream out;
	private InputStream remote;
	private RandomAccessFile in;
	private boolean buffering = false;
	static {
		try {
			audioFile = File.createTempFile("Music Online ", ".music");
			audioFile.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public FileAudioStream(String link, Streaming listener) {
		streaming = listener;
		try {
			in = new RandomAccessFile(audioFile, "r");
			out = new BufferedOutputStream(new FileOutputStream(audioFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			url = new URL(link);
			connection = url.openConnection();
			length = connection.getContentLength();
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
	    			synchronized (buffer) {
						buffer.notifyAll();
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
		return in.readByte();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (currentPosition >= length) return -1;
		while (!isCompleted() && (currentPosition + len > offset)){
			synchronized (buffer) {
				try {
					buffer.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		len = in.read(b, off, Math.min(Math.min(len, b.length - off), length - currentPosition));
		currentPosition += len;
		return len;
	}
	
	@Override
	public void seek(int bytes){
		currentPosition = Math.min(bytes, length);
		try {
			in.seek(currentPosition);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
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
		for (int i = 0; i < CODECS.length; i++){
			if (compareBytes(in, CODECS[i].getBytes())){
				ret = i;
				break;
			}
		}
		try {
			in.seek(0);
		} catch (IOException e) {
			e.printStackTrace();
			ret = -1;
		}
		return ret;
	}
	
	private boolean compareBytes(RandomAccessFile in, byte[] code){
		boolean ret = false;
		try {
			in.seek(0);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		try {
			for (int i = 0; i < code.length; i++){
				ret = code[i] == in.readByte();
				if (!ret) break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return ret;
	}
	
	@Override
	public void closeStream(){
		length = 0;
		if (buffering){
			synchronized (buffer) {
				try {
					buffer.wait();
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
