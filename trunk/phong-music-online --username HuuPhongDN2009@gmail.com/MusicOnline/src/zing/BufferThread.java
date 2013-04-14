package zing;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class BufferThread extends Thread {
	
	protected boolean buffering = true;
	protected Object paused = new Object();
	protected InputStream remote = null;
	protected RandomAccessFile out = null;
	
	public void stopBuffer(){
		buffering = false;
		synchronized (paused) {
			paused.notifyAll();
		}
	}
	
	public void pauseBuffer(){
		buffering = false;
	}
	
	public void resumeBuffer(){
		buffering = true;
		synchronized (paused) {
			paused.notifyAll();
		}
	}
	
	public void closeStream(){
		if (remote != null){
			try {
				remote.close();
			} catch (IOException e) {
			}
			remote = null;
		}
		if (out != null){
			try {
				out.close();
			} catch (IOException e) {
			}
			out = null;
		}
	}
}
