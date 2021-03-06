package huu.phong.musiconline.audio;

import huu.phong.musiconline.model.ISong;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class SmartSeekAudioStream extends AudioStream{
	private static File audioFile;
	private RandomAccessFile in;
	private Vector<Point> allPoints = new Vector<Point>();
	private static final Comparator<Point> comparator;
	private BufferThread buffer = null;
	private Object wait = new Object();
	private String userAgent;
	
	static {
		try {
			audioFile = File.createTempFile("Music Online ", ".music");
			audioFile.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
		comparator = new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				return o1.x - o2.x;
			}
		};
	}
		
	public SmartSeekAudioStream(ISong song, Streaming listener) {
		streaming = listener;
		try {
			in = new RandomAccessFile(audioFile, "rw");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			url = new URL(song.getDirectLink());
			connection = url.openConnection();
			userAgent = song.getSite().getSongAgent();
			connection.addRequestProperty("User-Agent", userAgent);
			length = connection.getContentLength();
//			if (length <= 0){
//				throw new RuntimeException(String.format("Can not play link %s", song.getDirectLink()));
//			}
			in.setLength(length);
			allPoints.add(new Point(0, length - 1));
			Vector<Point> currentPoints = new Vector<Point>();
			currentPoints.addAll(allPoints);
			startBuffer(currentPoints, connection.getInputStream());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	private void startBuffer(final Vector<Point> points, final InputStream in){
		buffer = new BufferThread(){
			public void run(){
				byte[] bytes = new byte[8096];
				int reading = -1;
				for (Point point : points){
					try {
						out = new RandomAccessFile(audioFile, "rw");
						if (in == null){
							remote = prepareStream(point);
						}else{
							remote = in;
						}
						offset = point.x;
						out.seek(offset);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					while (offset < point.y + 1){
						if (!buffering){
							synchronized (paused) {
								try {
									paused.wait();
								} catch (InterruptedException e) {
								}
								if (!buffering){
									break;
								}
							}
						}
						try {
							reading = remote.read(bytes);
						} catch (IOException e) {
							try{
								if (remote != null) remote.close();
						        if(++numberReconnect <= totalNumberConnect && offset < point.y + 1){
									remote = prepareStream(new Point(offset, point.y));
						        	continue;
						        }else if (numberReconnect >= totalNumberConnect){
						        	break;
						        }
							}catch (IOException exception) {
								break;
							}
						}
						if (reading == -1) break;
						try {
							out.write(bytes, 0, reading);
						} catch (IOException e) {
							e.printStackTrace();
						}
						offset += reading;
						streaming.buffering(offset);
						synchronized (wait) {
							wait.notifyAll();
						}
					}
					closeStream();
					if (!buffering) break;
					allPoints.remove(point);
				}
				if (buffering){
					offset = length;
					streaming.buffering(offset);
				}
			}
		};
		buffer.start();
	}
	
	private InputStream prepareStream(Point point) throws IOException{
		connection = url.openConnection();
		connection.addRequestProperty("User-Agent", userAgent);
    	connection.setRequestProperty("Accept-Ranges", "bytes");
		connection.setRequestProperty("Range", "bytes=" + point.x + "-" + point.y);
		connection.connect();
		return connection.getInputStream();
	}

	@Override
	public int read() throws IOException {
		if (currentPosition >= length) return -1;
		while (currentPosition + 1 > offset){
			synchronized (wait) {
				try {
					wait.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		currentPosition++;
		return in.read();
	}

	@Override
	public synchronized void seek(int bytes){
		bytes = Math.min(bytes, length);
		try {
			in.seek(bytes);
			currentPosition = bytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!allPoints.isEmpty()){
			if (buffer != null) buffer.pauseBuffer();
			allPoints = joinBuffer(allPoints, bytes);
			Vector<Point> currentPoints = getNextPoints(allPoints, bytes);
			if (currentPoints.isEmpty()) return;
			if (currentPoints.get(0).x != offset){
				buffer.stopBuffer();
				startBuffer(currentPoints, null);
			}else{
				buffer.resumeBuffer();
			}
		}
	}
	
	@Override
	public void closeStream(){
		length = 0;
		if (buffer != null){
			buffer.pauseBuffer();
			buffer.stopBuffer();
			buffer.closeStream();
		}
		if (in != null){
			try {
				in.close();
			} catch (IOException e) {
			}
			in = null;
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
	
	private Vector<Point> joinBuffer(Vector<Point> points, int index){
		Point point = points.get(0);
		if (offset < point.y) point.x = offset;
		Collections.sort(points, comparator);
		Vector<Point> ret = new Vector<Point>();
		
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
	
	private Vector<Point> getNextPoints(Vector<Point> points, int index){
		Vector<Point> ret = new Vector<Point>();
		
		for (Point point : points){
			if (point.x >= index){
				ret.add(point);
			}
		}
		return ret;
	}
	
	public List<Point> getBufferedPoints(List<Point> points, int length){
		Collections.sort(points, comparator);
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
