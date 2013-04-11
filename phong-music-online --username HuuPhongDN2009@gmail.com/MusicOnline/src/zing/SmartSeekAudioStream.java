package zing;

import java.awt.Point;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

public class SmartSeekAudioStream extends AudioStream{
	private static File audioFile;
	private OutputStream out;
	private InputStream remote;
	private RandomAccessFile in;
	private boolean buffering = false;
	private Object locked = new Object();
	private boolean hasWait = false;
	private Stack<Point> points = new Stack<Point>();
	private boolean working = false;
	
	static {
		try {
			audioFile = File.createTempFile("Music Online ", ".music");
			audioFile.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public SmartSeekAudioStream(String link, Streaming listener) {
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
			connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.43 Safari/537.31");
			length = connection.getContentLength();
			in.setLength(length);
			startBuffer(new Point(0, length));
//			remote = connection.getInputStream();
//			buffer = new Thread(){
//	        	public void run(){
//	        		byte[] bytes = new byte[8096];
//	        		buffering = true;
//	    			while (true){
//    					try {
//							if (offset < length && remote != null && (reading = remote.read(bytes)) >= 0){
//								out.write(bytes, 0, reading);
//								out.flush();
//								offset += reading;
//								streaming.buffering(offset);
//								synchronized (buffer) {
//									buffer.notifyAll();
//								}
//							}else{
//								try{
//									if (remote != null) remote.close();
//							        if(++numberReconnect <= totalNumberConnect && offset < length){
//							        	connection = url.openConnection();
//										connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.43 Safari/537.31");
//							        	connection.setRequestProperty("Accept-Ranges", "bytes");
//										connection.setRequestProperty("Range", "bytes=" + offset + "-");
//										connection.connect();
//										remote = connection.getInputStream();
//							        	continue;
//							        }else if (offset == length){
//							        	break;
//							        }else if (numberReconnect >= totalNumberConnect){
//							        	break;
//							        }
//								}catch (IOException e) {
//									e.printStackTrace();
//									break;
//								}
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//							break;
//						}
//	    			}
//	    			try {
//						if (out != null){
//							out.flush();
//							out.close();
//						}
//						if (remote != null ) remote.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//	    			buffering = false;
//	    			synchronized (locked) {
//						locked.notifyAll();
//					}
//	        	}
//	        };
//	        buffer.start();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Can not get stream audio");
		}
	}
	
	private void startBuffer(final Point point){
		buffer = new Thread(){
			public void run(){
				stopBuffer();
				points.add(point);
				if (buffering){
					if (hasWait){
						return;
					}
					hasWait = true;
					try {
						synchronized (buffer) {
							buffer.wait();
						}
						hasWait = false;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				buffering = true;
				do {
					Point point = points.peek();
					try {
						prepareStream(point);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} while (!point.equals(points.peek()));
				points.clear();
				offset = point.x;
				try {
					in.seek(offset);
				} catch (IOException e) {
					e.printStackTrace();
				}
				byte[] bytes = new byte[8096];
				working = true;
				while (working && offset < point.y + 1){
					try {
						reading = remote.read(bytes);
						if (reading == -1) break;
						out.write(bytes, 0, reading);
						out.flush();
						offset += reading;
						streaming.buffering(offset);
						synchronized (buffer) {
							buffer.notifyAll();
						}
						if (!working){
							break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				buffering = false;
				working = false;
				synchronized (buffer) {
					buffer.notify();
				}
			}
		};
		buffer.start();
	}
	
	private void stopBuffer(){
		working = false;
	}
	
	private void prepareStream(Point point) throws IOException{
		connection = url.openConnection();
		connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.43 Safari/537.31");
    	connection.setRequestProperty("Accept-Ranges", "bytes");
		connection.setRequestProperty("Range", "bytes=" + point.x + "-" + point.y);
		System.out.println(connection.getHeaderFields());
		connection.connect();
		remote = connection.getInputStream();
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
		startBuffer(new Point(bytes, length));
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
	
	public List<Point> joinBuffer(List<Point> points, int index){
		Collections.sort(points, new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				return o1.x - o2.x;
			}
		});
		List<Point> ret = new ArrayList<Point>();
		
		for (Point p : points){
			if (index > p.x && index < p.y){
				points.remove(p);
				ret.add(new Point(index, p.y));
				for (Point pp : points){
					if (pp.x > p.y){
						ret.add(pp);
					}
				}
				for (Point pp : points){
					if (pp.x < p.y){
						ret.add(pp);
					}
				}
				ret.add(new Point(p.x, index - 1));
				break;
			}
			if (index <= p.x){
				points.remove(p);
				ret.add(p);
				for (Point pp : points){
					if (pp.x > p.y){
						ret.add(pp);
					}
				}
				for (Point pp : points){
					if (pp.x < p.y){
						ret.add(pp);
					}
				}
				break;
			}
		}
		return ret;
	}
	
	public List<Point> getBufferedPoints(List<Point> points, int length){
		Collections.sort(points, new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				return o1.x - o2.x;
			}
		});
		List<Point> ret = new ArrayList<Point>();
		int index = 0;
		for (Point p : points){
			if (p.x > index){
				ret.add(new Point(index, p.x));
			}
			index = p.y + 1;
		}
		if (index < length) ret.add(new Point(index, length));
		return ret;
	}
}
