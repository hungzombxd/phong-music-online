package zing;

import java.io.IOException;
import java.util.Stack;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class AudioPlayer{
	private AudioStream in;
	private byte[] buffer = new byte[32768];
	private boolean stoped = false;
	private SourceDataLine source;
	private AudioFormat fmtTarget;
	private boolean paused = false;
	private AudioPlayerListener listener;
	private int reading = -1;
	private AudioDecoder decoder;
	private int plusDuration = 0;
	private Streaming streaming;
	private AudioInfo audioInfo;
	private Object locked = new Object();
	private Stack<Thread> threads = new Stack<Thread>();
	private Thread processPlay;
	private boolean playing = false;
	
	public AudioPlayer(){
		listener = new AudioPlayerListener() {
			
			@Override
			public void playing(AudioPlayer player) {
			}
			
			@Override
			public void paused(AudioPlayer player) {
			}
			
			@Override
			public void init(AudioPlayer player) {
			}
			
			@Override
			public void finished(AudioPlayer player) {
			}
		};
		streaming = new Streaming() {
			
			@Override
			public void buffering(int length) {
			}
		};
		processPlay = new Thread(){
			public void run(){
				while (true){
					while (!threads.isEmpty()){
						Thread thread = threads.lastElement();
						thread.start();
						if (thread.equals(threads.lastElement())){
							threads.clear();
						} else {
							AudioPlayer.this.stop();
							continue;
						}
						try {
							thread.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					synchronized (locked) {
						try {
							locked.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		processPlay.start();
	}
	
	public void setListener(AudioPlayerListener listener){
		this.listener = listener;
	}
	
	public void setStreaming(Streaming streaming){
		this.streaming = streaming;
	}
	
	private void prepare(String url){
		in = new FileAudioStream(url, streaming);
		switch (in.getType()) {
		case AudioCodec.MP3_STREAM:
			decoder = new MP3FileDecoder(in);
			break;
			
		case AudioCodec.FLAC_STREAM:
			decoder = new FLACFileDecoder(in);
			break;

		default:
			throw new RuntimeException("Not support this file type");
		}
		audioInfo = decoder.getAudioInfo();
		listener.init(this);
	}
	
	public int getBuffering(){
		if (in == null) return 0;
		return in.getBufferingValue();
	}
	
	public int getLength(){
		if (in == null) return 0;
		return in.getLength();
	}
	
	public boolean isBuffered(){
		if (in == null) return false;
		return in.isCompleted();
	}
	
	public void seek(int size){
		if (source == null) return;
		source.stop();
		source.close();
		plusDuration = sizeToDuration(size);
		decoder.seek(size);
		try {
			source.open(fmtTarget);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		source.start();
	}
	
	public void play(final Song song){
		stop();
		threads.add(new Thread(){
			public void run(){
				try {
					prepare(song.getDirectLink());
				} catch (IOException e) {
					e.printStackTrace();
				}
				play();
			}
		});
		synchronized (locked) {
			locked.notifyAll();
		}
	}
	
	private synchronized void play(){
		createSource();
		stoped = false;
		plusDuration = 0;
		while (!stoped){
			reading = decoder.getPCMData(buffer);
			if (reading == -1){
				break;
			}
			if (paused){
				synchronized (source) {
					source.stop();
					listener.paused(AudioPlayer.this);
					try {
						source.wait();
						source.start();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if(!stoped) listener.playing(AudioPlayer.this);
			source.write(buffer, 0, reading);
		}
		if (in != null){
			in.closeStream();
		}
		if(!stoped){
			source.drain();
			listener.playing(AudioPlayer.this);
		}
		source.flush();
		source.close();
		source = null;
		decoder = null;
		if (!stoped) listener.finished(AudioPlayer.this);
		stoped = true;
	}
	
	private void createSource(){
		fmtTarget = decoder.getAudioFormat();
		try {
			Line line = AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, fmtTarget));
			if (line instanceof SourceDataLine) {
				source = (SourceDataLine) line;
				source.open(fmtTarget);
				source.start();
			}
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public void stop(){
		if (paused) resume();
		stoped = true;
	}
	
	public void pause(){
		paused = true;
	}
	
	public void resume(){
		if (source == null) return;
		paused = false;
		synchronized (source) {
			source.notify();
		}
	}
	
	public int getDuration(){
		if (decoder == null) return 0;
		return decoder.getDuration();
	}
	
	public AudioInfo getAudioInfo(){
		if (decoder == null) return new AudioInfo();
		return audioInfo;
	}
	
	public static String toDuaration(int position){
		int min = position / 60000;
		int sec = (position / 1000) % 60;
		return numberToString(min) + ":" + numberToString(sec);
	}

	private static String numberToString(int number){
		String ret = String.valueOf(number);
		while (ret.length() < 2){
			ret = "0" + ret;
		}
		return ret;
	}
	
	public int getCurrentDuration(){
		if (source == null) return 0;
		return (int) (plusDuration + (source.getMicrosecondPosition() / 1000));
	}
	
	public String getPlayingDuration(){
		return toDuaration(getCurrentDuration()) + " / " + toDuaration(getDuration());
	}
	
	public String getPlayingInfo(){
		return "[" + getAudioInfo() + " | " + getPlayingDuration() + "]";
	}
	
	public int durationToSize(int duration){
		if (decoder == null) return 0;
		return decoder.durationToSize(duration);
	}
	
	public int sizeToDuration(int size){
		if (decoder == null) return 0;
		return decoder.sizeToDuration(size);
	}
	
	public void release(){
		if (in != null){
			in.closeStream();
			in.release();
		}
	}
	
	public int getCurrentSize(){
		if (in == null) return 0;
		return in.getCurrentPosition();
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public boolean isPlaying(){
		return playing;
	}
}
